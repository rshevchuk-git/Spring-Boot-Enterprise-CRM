package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.models.forms.CustomersWrapper;
import com.ordersmanagement.crm.models.entities.CustomerEntity;
import com.ordersmanagement.crm.services.CustomerService;
import com.ordersmanagement.crm.utils.CustomerExcelExporter;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<List<CustomerEntity>> getAllCustomers() {
        return new ResponseEntity<>(customerService.getAllCustomers(), HttpStatus.OK);
    }
//
//    @GetMapping("/{username}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER') or hasRole('CUSTOMER')")
//    public CustomerEntity getCustomer(@PathVariable(name = "username") String customerName){
//        return customerRepository.findByCustomerName(customerName);
//    }

    @RequestMapping(value={"/"}, method={RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<CustomerEntity> saveCustomer(@Valid @RequestBody CustomerEntity newCustomer) {
        return customerService.saveCustomer(newCustomer)
                .map(savedCustomer -> new ResponseEntity<>(savedCustomer, HttpStatus.CREATED))
                .orElseGet(()      -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> deleteCustomer(@PathVariable("id") int customerId) {
        if (customerService.deleteCustomer(customerId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/export", consumes="application/json", produces="application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> exportToExcel(@RequestBody CustomersWrapper customers) throws IOException {
        ByteArrayInputStream byteStream = new CustomerExcelExporter(customers.getCustomers()).export();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=список замовлень.xlsx");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(byteStream));
    }
}
