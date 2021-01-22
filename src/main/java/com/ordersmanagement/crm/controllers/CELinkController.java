package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.models.entities.CELink;
import com.ordersmanagement.crm.models.entities.Customer;
import com.ordersmanagement.crm.models.entities.Entrepreneur;
import com.ordersmanagement.crm.models.keys.CEKey;
import com.ordersmanagement.crm.services.LinkService;
import lombok.AllArgsConstructor;
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
@RequestMapping("/api/ce-links")
public class CELinkController {

    private final LinkService linkService;

    @GetMapping(value = "/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<List<CELink>> getAllCELinks() {
        List<CELink> ceLinksList = linkService.getAllCELinks();
        return new ResponseEntity<>(ceLinksList, HttpStatus.OK);
    }

    @GetMapping(value = "/{customer_id}/{entrepreneur_id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<CELink> makeCELink(@PathVariable(name = "customer_id") Customer customer,
                                             @PathVariable(name = "entrepreneur_id") Entrepreneur entrepreneur) {
        CEKey key = new CEKey(customer.getCustomerId(), entrepreneur.getEntrepreneurId());
        CELink newLink = new CELink(key, customer, entrepreneur);
        CELink savedLink = linkService.saveCELink(newLink);
        return new ResponseEntity<>(savedLink, HttpStatus.OK);
    }
}
