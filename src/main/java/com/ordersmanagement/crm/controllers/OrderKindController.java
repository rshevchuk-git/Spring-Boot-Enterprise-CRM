package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.models.entities.OrderKind;
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
    public ResponseEntity<List<OrderKind>> getAllOrderKinds() {
        List<OrderKind> orderKindList = orderKindService.getAllOrderKinds();
        return new ResponseEntity<>(orderKindList, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = {RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<OrderKind> updateOrderKind(@RequestBody OrderKind orderKind) {
        OrderKind updatedOrderKind = orderKindService.saveOrderKind(orderKind);
        return new ResponseEntity<>(updatedOrderKind, HttpStatus.OK);
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
    public ResponseEntity<?> changeOrderKinds(@PathVariable(name = "replace_id") OrderKind replaceKind,
                                              @PathVariable(name = "new_id") OrderKind newKind) {
        orderKindService.replaceOrderKinds(replaceKind, newKind);
        orderKindService.deleteOrderKind(replaceKind.getKindId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
