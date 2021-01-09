package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.CustomerRepository;
import com.ordersmanagement.crm.models.entities.CustomerEntity;
import com.ordersmanagement.crm.models.entities.QCustomerEntity;
import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<CustomerEntity> getAllCustomers() {
        return customerRepository.findAll();
    }

    public boolean existsById(Integer customerId) {
        return customerRepository.existsById(customerId);
    }

    public boolean deleteCustomer(Integer customerID){
        if(existsById(customerID)) {
            customerRepository.deleteById((customerID));
            return true;
        } else {
            return false;
        }
    }

    public CustomerEntity updateCustomer(CustomerEntity customer) {
        return customerRepository.save(customer);
    }

    public Optional<CustomerEntity> saveCustomer(CustomerEntity newCustomer) {
        if (customerRepository.existsByCustomerName(newCustomer.getCustomerName()))
            return Optional.empty();
        if (!newCustomer.getFirstPhone().isEmpty() &&
                customerRepository.existsByFirstPhone(newCustomer.getFirstPhone()) &&
                customerRepository.existsBySecondPhone(newCustomer.getFirstPhone()) &&
                customerRepository.existsByThirdPhone(newCustomer.getFirstPhone()))
            return Optional.empty();
        if (!newCustomer.getSecondPhone().isEmpty() &&
                customerRepository.existsByFirstPhone(newCustomer.getSecondPhone()) &&
                customerRepository.existsBySecondPhone(newCustomer.getSecondPhone()) &&
                customerRepository.existsByThirdPhone(newCustomer.getSecondPhone()))
            return Optional.empty();
        if (!newCustomer.getThirdPhone().isEmpty() &&
                customerRepository.existsByFirstPhone(newCustomer.getThirdPhone()) &&
                customerRepository.existsBySecondPhone(newCustomer.getThirdPhone()) &&
                customerRepository.existsByThirdPhone(newCustomer.getThirdPhone()))
            return Optional.empty();
        return Optional.of(customerRepository.save(newCustomer));
    }

    public int paymentsOnCustomerBalance(String receiver, CustomerEntity customer){
        QCustomerEntity customerQuery = QCustomerEntity.customerEntity;
        BooleanBuilder where = new BooleanBuilder();

        where.and(customerQuery.payLog.contains(receiver));
        if (customer != null) where.and(customerQuery.customerId.eq(customer.getCustomerId()));

        return customerRepository.findAll(where)
                .stream()
                .map(CustomerEntity::getPayLog)
                .filter(log -> log.contains(receiver))
                .map(log -> log.split("\n"))
                .flatMap(Arrays::stream)
                .filter(log -> log.contains(receiver))
                .reduce(0, (preVal, log) -> preVal + Integer.parseInt(log.substring(log.indexOf("ма : ") + 5, log.indexOf(" Отр") - 1)), Integer::sum);
    }
}
