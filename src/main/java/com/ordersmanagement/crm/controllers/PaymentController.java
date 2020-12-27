package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.dao.orders.CustomerRepository;
import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.models.entities.CustomerEntity;
import com.ordersmanagement.crm.models.forms.PaymentForm;
import com.ordersmanagement.crm.models.entities.PaymentMethodEntity;
import com.ordersmanagement.crm.services.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<List<PaymentMethodEntity>> getAllPaymentTypes() {
        return new ResponseEntity<>(paymentService.getAllPaymentMethods(), HttpStatus.OK);
    }

    @PostMapping("/types/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<PaymentMethodEntity> savePaymentType(@RequestBody PaymentMethodEntity paymentType) {
        return paymentService.savePaymentMethod(paymentType)
                .map(savedPaymentMethod -> new ResponseEntity<>(savedPaymentMethod, HttpStatus.CREATED))
                .orElseGet(()           -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PostMapping(value = "/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> makePayment(@RequestBody PaymentForm payment) {
        try {
            paymentService.makePayment(payment);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
