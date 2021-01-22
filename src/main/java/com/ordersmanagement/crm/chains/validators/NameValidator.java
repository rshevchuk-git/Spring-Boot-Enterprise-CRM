package com.ordersmanagement.crm.chains.validators;

import com.ordersmanagement.crm.dao.business.CustomerRepository;
import com.ordersmanagement.crm.models.entities.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NameValidator implements CustomerValidator {

    private final CustomerRepository customerRepository;

    @Override
    public boolean validate(Customer customer) {
        return !customerRepository.existsByCustomerName(customer.getCustomerName());
    }
}
