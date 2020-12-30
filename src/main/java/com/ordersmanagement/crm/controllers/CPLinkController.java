package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.models.keys.CPLinkKey;
import com.ordersmanagement.crm.models.entities.CustomerEntity;
import com.ordersmanagement.crm.models.entities.OrderKindEntity;
import com.ordersmanagement.crm.models.entities.CPLinkEntity;
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
    public ResponseEntity<List<CPLinkEntity>> getPriceLinks() {
        return new ResponseEntity<>(linkService.getAllCPLinks(), HttpStatus.OK);
    }

    @GetMapping(value = "/{customer_id}/{kind_id}/{price}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<CPLinkEntity> makePriceLink(@PathVariable(name = "customer_id") CustomerEntity customer,
                                                      @PathVariable(name = "kind_id")     OrderKindEntity kind,
                                                      @PathVariable(name = "price")       Double price) {
        CPLinkKey key = new CPLinkKey(customer.getCustomerId(), kind.getKindId());
        CPLinkEntity newLink = new CPLinkEntity(key, customer, kind, price);
        return new ResponseEntity<>(linkService.saveCPLink(newLink), HttpStatus.OK);
    }
}
