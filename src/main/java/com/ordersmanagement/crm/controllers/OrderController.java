package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.chains.OrderInspectorChain;
import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.exceptions.OrderNotFoundException;
import com.ordersmanagement.crm.models.dto.OrdersWrapper;
import com.ordersmanagement.crm.models.dto.SortForm;
import com.ordersmanagement.crm.models.dto.Summary;
import com.ordersmanagement.crm.models.entities.Order;
import com.ordersmanagement.crm.models.entities.Status;
import com.ordersmanagement.crm.services.OrderService;
import com.ordersmanagement.crm.services.facades.OrderServiceFacade;
import com.ordersmanagement.crm.utils.LoggerUtils;
import com.ordersmanagement.crm.utils.OrderExcelExporter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final Logger logger = LoggerFactory.getLogger("[Order Controller]");

    private final OrderService orderService;
    private final OrderServiceFacade orderServiceFacade;
    private final OrderInspectorChain inspectorChain;

    @GetMapping("/{customer_id}")
    public ResponseEntity<List<Order>> getCustomerOrders(@PathVariable("customer_id") Integer customerId) {
        List<Order> customerOrders = orderService.getCustomerOrders(customerId);
        return new ResponseEntity<>(customerOrders, HttpStatus.OK);
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> addNewOrder(@Valid @RequestBody Order newOrder) {
        try {
            Order savedOrder = orderServiceFacade.addOrder(newOrder);
            return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody Order changedOrder) {
        LoggerUtils.logUserAction(logger, "changes " + changedOrder.getOrderId() + " to:\n" + changedOrder);
        try {
            Order preparedOrder = inspectorChain.inspect(changedOrder);
            Order updatedOrder = orderService.updateOrder(preparedOrder);
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (OrderNotFoundException | CustomerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") Order order) {
        LoggerUtils.logUserAction(logger, "deletes:\n" + order);
        orderServiceFacade.deleteOrder(order);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/filter")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER') or hasRole('CUSTOMER')")
    public ResponseEntity<?> filterOrders(@RequestParam(name = "stat", required = false, defaultValue = "false") Boolean showStatistics,
                                          @RequestBody SortForm selections) {
        List<Order> filteredList = orderService.getFilteredOrders(selections);
        if (showStatistics) {
            Summary ordersSummary = orderService.summarize(filteredList, selections);
            return new ResponseEntity<>(ordersSummary, HttpStatus.OK);
        }
        return new ResponseEntity<>(filteredList, HttpStatus.OK);
    }

    @GetMapping("/statuses/{order_id}/{status_id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<Order> changeStatus(@PathVariable(name = "order_id") Order order,
                                              @PathVariable(name = "status_id") Status status) {
        LoggerUtils.logUserAction(logger, "changes status of " + order.getOrderId() + " to " + status.getName());
        Order updatedOrder = orderService.changeOrderStatus(order, status);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @PostMapping(value = "/export", consumes="application/json", produces="application/json")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_ORDERS_EXPORTER')")
    public ResponseEntity<InputStreamResource> exportToExcel(@RequestBody OrdersWrapper orders) throws IOException {
        LoggerUtils.logUserAction(logger, "requests orders export");
        List<Order> orderList = orders.getOrders();
        Collections.reverse(orderList);
        ByteArrayInputStream byteStream = new OrderExcelExporter(orderList).export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=список замовлень.xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(byteStream));
    }
}
