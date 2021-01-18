package com.ordersmanagement.crm.chains.validators;

import com.ordersmanagement.crm.dao.orders.CustomerRepository;
import com.ordersmanagement.crm.models.entities.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SecondPhoneValidator implements CustomerValidator {

    private final CustomerRepository customerRepository;

    @Override
    public boolean validate(CustomerEntity customer) {
        if (customer.getSecondPhone().isEmpty()) return true;
        return !customerRepository.existsByFirstPhone(customer.getSecondPhone()) &&
               !customerRepository.existsBySecondPhone(customer.getSecondPhone()) &&
               !customerRepository.existsByThirdPhone(customer.getSecondPhone());
    }
}
