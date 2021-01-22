package com.ordersmanagement.crm.chains.validators;

import com.ordersmanagement.crm.dao.business.CustomerRepository;
import com.ordersmanagement.crm.models.entities.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class FirstPhoneValidator implements CustomerValidator {

    private final CustomerRepository customerRepository;

    @Override
    public boolean validate(Customer customer) {
        if (customer.getFirstPhone().isEmpty()) return true;
        return !customerRepository.existsByFirstPhone(customer.getFirstPhone()) &&
               !customerRepository.existsBySecondPhone(customer.getFirstPhone()) &&
               !customerRepository.existsByThirdPhone(customer.getFirstPhone());
    }
}
