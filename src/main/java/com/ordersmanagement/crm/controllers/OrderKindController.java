package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.models.entities.OrderKindEntity;
import com.ordersmanagement.crm.services.OrderKindService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/order-kinds")
public class OrderKindController {

    private final OrderKindService orderKindService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<List<OrderKindEntity>> getAllOrderKinds() {
        return new ResponseEntity<>(orderKindService.getAllOrderKinds(), HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = {RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<OrderKindEntity> updateOrderKind(@RequestBody OrderKindEntity orderKind) {
        return new ResponseEntity<>(orderKindService.saveOrderKind(orderKind), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteOrderKind(@PathVariable(name = "id") Integer orderKindId) {
        if (orderKindService.deleteOrderKind(orderKindId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/replace/{replace_id}/{new_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeOrderKinds(@PathVariable(name = "replace_id") OrderKindEntity replaceKind,
                                              @PathVariable(name = "new_id")     OrderKindEntity newKind) {
        orderKindService.replaceOrderKinds(replaceKind, newKind);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
