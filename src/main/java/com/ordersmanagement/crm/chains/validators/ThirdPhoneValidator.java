package com.ordersmanagement.crm.chains.validators;

import com.ordersmanagement.crm.dao.orders.CustomerRepository;
import com.ordersmanagement.crm.models.entities.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ThirdPhoneValidator implements CustomerValidator {

    private final CustomerRepository customerRepository;

    @Override
    public boolean validate(CustomerEntity customer) {
        if (customer.getThirdPhone().isEmpty()) return true;
        return !customerRepository.existsByFirstPhone(customer.getThirdPhone()) &&
               !customerRepository.existsBySecondPhone(customer.getThirdPhone()) &&
               !customerRepository.existsByThirdPhone(customer.getThirdPhone());
    }
}
