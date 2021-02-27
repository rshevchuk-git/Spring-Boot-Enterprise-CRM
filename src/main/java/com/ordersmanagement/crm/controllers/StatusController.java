package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.models.entities.Order;
import com.ordersmanagement.crm.models.entities.Status;
import com.ordersmanagement.crm.services.MailService;
import com.ordersmanagement.crm.services.OrderService;
import com.ordersmanagement.crm.services.StatusService;
import com.ordersmanagement.crm.utils.LoggerUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/statuses")
public class StatusController {

    private final Logger logger = LoggerFactory.getLogger("[Status Controller]");

    private final StatusService statusService;
    private final OrderService orderService;
    private final MailService mailService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER') or hasRole('CUSTOMER')")
    public ResponseEntity<List<Status>> getAllStatuses() {
        List<Status> statusList = statusService.getAllStatuses();
        return new ResponseEntity<>(statusList, HttpStatus.OK);
    }

    @GetMapping("/{order_id}/{status_id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<Order> changeStatus(@PathVariable(name = "order_id") Order order,
                                              @PathVariable(name = "status_id") Status status) {
        if (order.getStatus().getId().equals(status.getId())) {
            return new ResponseEntity<>(order, HttpStatus.OK);
        }
        LoggerUtils.logUserAction(logger, "changes status of " + order.getOrderId() + " to " + status.getName());
        Order updatedOrder = orderService.changeOrderStatus(order, status);
        mailService.informAboutStatusChange(updatedOrder);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

}
