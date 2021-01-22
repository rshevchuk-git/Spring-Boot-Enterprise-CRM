package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.models.entities.Status;
import com.ordersmanagement.crm.services.StatusService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/orders/statuses")
public class StatusController {

    private final StatusService statusService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER') or hasRole('CUSTOMER')")
    public ResponseEntity<List<Status>> getAllStatuses() {
        List<Status> statusList = statusService.getAllStatuses();
        return new ResponseEntity<>(statusList, HttpStatus.OK);
    }
}
