package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.dao.users.User32Repository;
import com.ordersmanagement.crm.models.entities.Status;
import com.ordersmanagement.crm.models.entities.User32;
import com.ordersmanagement.crm.services.MailService;
import com.ordersmanagement.crm.services.OrderService;
import com.ordersmanagement.crm.services.StatusService;
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
@RequestMapping(path = "/api/users")
public class UserController {

    private final User32Repository usersRepository;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User32>> getNewUsers() {
        List<User32> newUsers = usersRepository.findAllByCustomerID(0);
        return new ResponseEntity<>(newUsers, HttpStatus.OK);
    }

    @GetMapping("/{userId}/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User32> assignUserToCustomer(@PathVariable(name = "userId") User32 user,
                                                             @PathVariable Integer customerId) {
        user.setCustomerID(customerId);
        User32 savedUser = usersRepository.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }
}
