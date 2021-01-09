package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.models.entities.OrderTypeEntity;
import com.ordersmanagement.crm.services.OrderTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/order-types")
public class OrderTypeController {

    private final OrderTypeService orderTypeService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<List<OrderTypeEntity>> getAllTypes() {
        return new ResponseEntity<>(orderTypeService.getAllOrderTypes(), HttpStatus.OK);
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<OrderTypeEntity> addNewType(@RequestBody OrderTypeEntity newType) {
        return orderTypeService.saveOrderType(newType)
                .map(savedOrderType -> new ResponseEntity<>(savedOrderType, HttpStatus.CREATED))
                .orElseGet(()       -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteType(@PathVariable(name = "id") Integer orderTypeId) {
        if (orderTypeService.deleteOrderType(orderTypeId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/replace/{replace_id}/{new_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeTypes(@PathVariable(name = "replace_id") OrderTypeEntity replaceType,
                                         @PathVariable(name = "new_id")     OrderTypeEntity newType) {
        orderTypeService.replaceOrderType(replaceType, newType);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
