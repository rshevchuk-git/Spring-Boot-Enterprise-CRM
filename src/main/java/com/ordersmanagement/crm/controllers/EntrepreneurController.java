package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.models.entities.EntrepreneurEntity;
import com.ordersmanagement.crm.services.EntrepreneurService;
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
@RequestMapping("/api/entrepreneurs")
public class EntrepreneurController {

    private final EntrepreneurService entrepreneurService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<List<EntrepreneurEntity>> getAllEntrepreneurs() {
        return new ResponseEntity<>(entrepreneurService.getAllEntrepreneurs(), HttpStatus.OK);
    }
}
