package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.exceptions.OrderNotFoundException;
import com.ordersmanagement.crm.models.entities.StatusEntity;
import com.ordersmanagement.crm.models.forms.OrdersWrapper;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.forms.SortForm;
import com.ordersmanagement.crm.models.response.Summary;
import com.ordersmanagement.crm.services.*;
import com.ordersmanagement.crm.utils.OrderExcelExporter;
import com.ordersmanagement.crm.utils.PaymentUtils;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
@AllArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final AuthService authService;
    private final OrderService orderService;
    private final StatusService statusService;
    private final PaymentService paymentService;
    private final CustomerService customerService;
    private final OrderTypeService orderTypeService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<List<OrderEntity>> getRecentOrders() {
        return new ResponseEntity<>(orderService.getRecentOrders(), HttpStatus.OK);
    }

    @GetMapping("/{customer_id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER') or hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderEntity>> getCustomerOrders(@PathVariable("customer_id") Integer customerId) {
        if (!customerService.existsById(customerId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(orderService.getCustomerOrders(customerId), HttpStatus.OK);
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> addNewOrder(@Valid @RequestBody OrderEntity newOrder) {
        try {
            paymentService.payFromCustomerBalance(newOrder);
        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(orderService.saveNewOrder(newOrder), HttpStatus.CREATED);
    }

    @PutMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody OrderEntity updatedOrder) {
        try {
            if (orderService.isCustomerChanged(updatedOrder)) {
                paymentService.removePaymentsFrom(updatedOrder);
                paymentService.payFromCustomerBalance(updatedOrder);
            }
            if (updatedOrder.getPaySum() < updatedOrder.getFinalSum()) {
                paymentService.payFromCustomerBalance(updatedOrder);
            }
            if (updatedOrder.getPaySum() > updatedOrder.getFinalSum()) {
                orderService.updateOrder(updatedOrder); // Save new 'finalSum' first, to be skipped during re-payment
                paymentService.distributeOverpayment(updatedOrder, updatedOrder.getPaySum() - updatedOrder.getFinalSum());
            }
        } catch (OrderNotFoundException | CustomerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        updatedOrder.setPaySum(PaymentUtils.calculatePaymentSum(updatedOrder.getPayLog()));
        return new ResponseEntity<>(orderService.updateOrder(updatedOrder), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") Integer orderId) {
        if (orderService.deleteOrder(orderId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/filter")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER') or hasRole('CUSTOMER')")
    public ResponseEntity<?> filterOrders(@RequestParam(name = "stat", required = false, defaultValue = "false") Boolean showStatistics,
                                          @RequestBody SortForm selections) {
        List<OrderEntity> filteredList = selections.isEmptyForm() ? orderService.getRecentOrders()
                                                                  : orderService.getSortedOrders(selections);
        for (GrantedAuthority grantedAuthority : authService.getUserRoles()) {
            filteredList = filteredList.stream()
                    .filter(orderEntity -> orderTypeService.typeFilterByRole
                            .getOrDefault(grantedAuthority.getAuthority(), (val) -> true)
                            .apply(orderEntity.getOrderType()))
                    .collect(toList());
        }
        if (showStatistics) {
            Summary response = orderService.summarize(filteredList, selections.getSelectedReceiver(), selections.getSelectedCustomer());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(filteredList, HttpStatus.OK);
    }

    @GetMapping("/statuses/{order_id}/{status_id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<OrderEntity> setStatusDone(@PathVariable(name = "order_id")  Integer orderId,
                                                     @PathVariable(name = "status_id") Integer statusId) {
        Optional<StatusEntity> optionalStatus = statusService.getStatusById(statusId);
        Optional<OrderEntity>  optionalOrder  = orderService.getOrderById(orderId);
        if (optionalOrder.isPresent() && optionalStatus.isPresent()) {
            optionalOrder.get().setStatus(optionalStatus.get());
            return new ResponseEntity<>(orderService.updateOrder(optionalOrder.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/export", consumes="application/json", produces="application/json")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<InputStreamResource> exportToExcel(@RequestBody OrdersWrapper orders) throws IOException {
        ByteArrayInputStream byteStream = new OrderExcelExporter(orders.getOrders()).export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=список замовлень.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(byteStream));
    }
}
