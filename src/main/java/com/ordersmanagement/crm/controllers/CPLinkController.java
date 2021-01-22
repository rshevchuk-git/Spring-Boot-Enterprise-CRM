package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.models.entities.CPLink;
import com.ordersmanagement.crm.models.keys.CPLinkKey;
import com.ordersmanagement.crm.models.entities.Customer;
import com.ordersmanagement.crm.models.entities.OrderKind;
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
@RequestMapping("/api/cp-links")
public class CPLinkController {

    private final LinkService linkService;

    @GetMapping(value = "/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<List<CPLink>> getPriceLinks() {
        List<CPLink> cpLinkList = linkService.getAllCPLinks();
        return new ResponseEntity<>(cpLinkList, HttpStatus.OK);
    }

    @GetMapping(value = "/{customer_id}/{kind_id}/{price}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<CPLink> makePriceLink(@PathVariable(name = "customer_id") Customer customer,
                                                @PathVariable(name = "kind_id") OrderKind kind,
                                                @PathVariable(name = "price")       Double price) {
        CPLinkKey key = new CPLinkKey(customer.getCustomerId(), kind.getKindId());
        CPLink newLink = new CPLink(key, customer, kind, price);
        CPLink savedLink = linkService.saveCPLink(newLink);
        return new ResponseEntity<>(savedLink, HttpStatus.OK);
    }
}
