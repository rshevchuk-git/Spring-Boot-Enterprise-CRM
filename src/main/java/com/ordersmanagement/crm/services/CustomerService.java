package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.business.CustomerRepository;
import com.ordersmanagement.crm.models.entities.Customer;
import com.ordersmanagement.crm.utils.PaymentUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerService {

    private final MailService mailService;
    private final CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public boolean existsById(Integer customerId) {
        return customerRepository.existsById(customerId);
    }

    public Optional<Customer> getById(Integer customerId) {
        return customerRepository.findById(customerId);
    }

    public Customer saveCustomer(Customer newCustomer) {
        mailService.sendNotification(newCustomer);
        return customerRepository.save(newCustomer);
    }

    public boolean deleteCustomer(Integer customerID){
        if(existsById(customerID)) {
            customerRepository.deleteById((customerID));
            return true;
        } else {
            return false;
        }
    }

    public int paidOnCustomerBalance(Integer customerId, String receiver){
        return customerRepository.findAllByCustomerIdAndPayLogContaining(customerId, receiver)
                .stream()
                .map(Customer::getPayLog)
                .map(log -> log.split("\n"))
                .flatMap(Arrays::stream)
                .filter(log -> log.contains(receiver))
                .reduce(0, (currentVal, log) -> currentVal + PaymentUtils.getSumFromLog(log), Integer::sum);
    }
}
