package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.chains.CustomerValidatorChain;
import com.ordersmanagement.crm.models.dto.CustomersWrapper;
import com.ordersmanagement.crm.models.entities.CustomerEntity;
import com.ordersmanagement.crm.services.CustomerService;
import com.ordersmanagement.crm.utils.CustomerExcelExporter;
import com.ordersmanagement.crm.utils.LoggerUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger("[Customer Controller]");

    private final CustomerService customerService;
    private final CustomerValidatorChain validatorChain;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<List<CustomerEntity>> getAllCustomers() {
        List<CustomerEntity> customerList = customerService.getAllCustomers();
        return new ResponseEntity<>(customerList, HttpStatus.OK);
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<CustomerEntity> saveCustomer(@Valid @RequestBody CustomerEntity newCustomer) {
        LoggerUtils.logUserAction(logger, "creates:\n" + newCustomer);
        boolean isValid = validatorChain.validate(newCustomer);
        if (!isValid) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        CustomerEntity savedCustomer = customerService.saveCustomer(newCustomer);
        return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
    }

    @PutMapping("/")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<CustomerEntity> updateCustomer(@Valid @RequestBody CustomerEntity newCustomer) {
        LoggerUtils.logUserAction(logger, "changes customer [" + newCustomer.getCustomerId() + "] to:\n" + newCustomer);
        CustomerEntity updatedCustomer = customerService.saveCustomer(newCustomer);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> deleteCustomer(@PathVariable("id") int customerId) {
        LoggerUtils.logUserAction(logger, "deletes customer [" + customerId + "]");
        if (customerService.deleteCustomer(customerId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> exportToExcel(@RequestBody CustomersWrapper customers) throws IOException {
        LoggerUtils.logUserAction(logger, "exports customers");
        ByteArrayInputStream byteStream = new CustomerExcelExporter(customers.getCustomers()).export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=clients.xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(byteStream));
    }
}


//
//    @GetMapping("/fix")
//    public ResponseEntity<?> fixCustomers() {
//        List<CustomerEntity> all = customerRepository.findAll();
//
//        all.forEach(customer -> {
//            String payLog = customer.getPayLog();
//            if( payLog != null ) {
//                String[] payments = payLog.split("\\n");
//                List<String> newPayments = new ArrayList<>();
//                if (payments.length > 0) {
//                    for (String payment : payments) {
//                        if (payment.isEmpty()) continue;
//                        payment = payment.trim();
//                        String storedDate = payment.substring(payment.indexOf("та : ") + 5, payment.indexOf(" Сум") - 1);
//
//                        String year = storedDate.substring(0, 4);
//                        String month = storedDate.substring(5, 7);
//                        String day = storedDate.substring(8, 10);
//
//                        String hour = storedDate.substring(11, 13);
//                        String minute = storedDate.substring(14, 16);
//
//                        String newDate = day + "/" + month + "/" + year + " " + hour + ":" + minute;
//                        String newPayment = payment.substring(0, payment.indexOf("та : ") + 5) + newDate + " " + payment.substring(payment.indexOf("Сум") - 1);
//                        int sum = (int) Double.parseDouble(newPayment.substring(newPayment.indexOf("ма : ") + 5, newPayment.indexOf(" Отр")));
//                        String finalPayment = newPayment.substring(0, newPayment.indexOf("ма : ") + 5) + sum + " " + newPayment.substring(newPayment.indexOf(" Отр"));
//                        newPayments.add(finalPayment);
//                    }
//                    String newLog = String.join("\n", newPayments) + "\n";
//                    customer.setPayLog(newLog);
//                    System.out.println(customer.getPayLog());
//                    customerRepository.save(customer);
//                }
//            }
//        });
//
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

//
//    @GetMapping("/{username}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER') or hasRole('CUSTOMER')")
//    public CustomerEntity getCustomer(@PathVariable(name = "username") String customerName){
//        return customerRepository.findByCustomerName(customerName);
//    }
