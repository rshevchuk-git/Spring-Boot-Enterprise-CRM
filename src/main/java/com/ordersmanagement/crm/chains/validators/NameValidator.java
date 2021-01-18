package com.ordersmanagement.crm.chains.validators;

import com.ordersmanagement.crm.dao.orders.CustomerRepository;
import com.ordersmanagement.crm.models.entities.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NameValidator implements CustomerValidator {

    private final CustomerRepository customerRepository;

    @Override
    public boolean validate(CustomerEntity customer) {
        return !customerRepository.existsByCustomerName(customer.getCustomerName());
    }
}
