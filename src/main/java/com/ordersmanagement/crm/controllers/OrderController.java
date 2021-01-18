package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.chains.OrderInspectorChain;
import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.exceptions.OrderNotFoundException;
import com.ordersmanagement.crm.models.entities.StatusEntity;
import com.ordersmanagement.crm.models.forms.OrdersWrapper;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.forms.SortForm;
import com.ordersmanagement.crm.models.response.Summary;
import com.ordersmanagement.crm.services.*;
import com.ordersmanagement.crm.utils.OrderExcelExporter;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.validation.Valid;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final OrderInspectorChain inspectorChain;

    @GetMapping("/{customer_id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER') or hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderEntity>> getCustomerOrders(@PathVariable("customer_id") Integer customerId) {
        List<OrderEntity> customerOrders = orderService.getCustomerOrders(customerId);
        return new ResponseEntity<>(customerOrders, HttpStatus.OK);
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> addNewOrder(@Valid @RequestBody OrderEntity newOrder) {
        try {
            paymentService.payFromCustomerBalance(newOrder);
            OrderEntity savedOrder = orderService.saveNewOrder(newOrder);
            return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody OrderEntity changedOrder) {
        try {
            OrderEntity preparedOrder = inspectorChain.inspect(changedOrder);
            OrderEntity updatedOrder  = orderService.updateOrder(preparedOrder);
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (OrderNotFoundException | CustomerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") OrderEntity order) {
        paymentService.removePaymentsFrom(order);
        boolean isDeleted = orderService.deleteOrder(order.getOrderId());
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/filter")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER') or hasRole('CUSTOMER')")
    public ResponseEntity<?> filterOrders(@RequestParam(name = "stat", required = false, defaultValue = "false") Boolean showStatistics,
                                          @RequestBody SortForm selections) {
        List<OrderEntity> filteredList = orderService.getSortedOrders(selections);
        if (showStatistics) {
            Summary ordersSummary = orderService.summarize(filteredList, selections.getReceiver(), selections.getCustomer());
            return new ResponseEntity<>(ordersSummary, HttpStatus.OK);
        }
        return new ResponseEntity<>(filteredList, HttpStatus.OK);
    }

    @GetMapping("/statuses/{order_id}/{status_id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<OrderEntity> changeStatus(@PathVariable(name = "order_id")  Integer orderId,
                                                    @PathVariable(name = "status_id") StatusEntity status) {
        return orderService.getOrderById(orderId)
                .map(order -> {
                        order.setStatus(status);
                        OrderEntity updatedOrder = orderService.updateOrder(order);
                        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
                    })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = "/export", consumes="application/json", produces="application/json")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_ORDERS_EXPORTER')")
    public ResponseEntity<InputStreamResource> exportToExcel(@RequestBody OrdersWrapper orders) throws IOException {
        ByteArrayInputStream byteStream = new OrderExcelExporter(orders.getOrders()).export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=список замовлень.xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(byteStream));
    }
}
