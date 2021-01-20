package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.models.dto.PaymentForm;
import com.ordersmanagement.crm.models.entities.PaymentMethodEntity;
import com.ordersmanagement.crm.services.PaymentService;
import com.ordersmanagement.crm.services.PaymentServiceFacade;
import com.ordersmanagement.crm.utils.LoggerUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final Logger logger = LoggerFactory.getLogger("[Payment Controller]");

    private final PaymentService paymentService;
    private final PaymentServiceFacade paymentServiceFacade;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<List<PaymentMethodEntity>> getAllPaymentTypes() {
        List<PaymentMethodEntity> paymentMethodList = paymentService.getAllPaymentMethods();
        return new ResponseEntity<>(paymentMethodList, HttpStatus.OK);
    }

    @PutMapping("/types/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<PaymentMethodEntity> updatePaymentType(@RequestBody PaymentMethodEntity paymentType) {
        PaymentMethodEntity updatedPaymentMethod = paymentService.updatePaymentMethod(paymentType);
        return new ResponseEntity<>(updatedPaymentMethod, HttpStatus.OK);
    }

    @PostMapping(value = "/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> makePayment(@RequestBody PaymentForm payment) {
        LoggerUtils.logUserAction(logger, "makes payment:\n" + payment);
        paymentServiceFacade.makePayment(payment);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
