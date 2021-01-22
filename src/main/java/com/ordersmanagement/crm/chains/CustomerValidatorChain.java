package com.ordersmanagement.crm.chains;

import com.ordersmanagement.crm.chains.validators.CustomerValidator;
import com.ordersmanagement.crm.models.entities.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerValidatorChain {

    @Autowired
    private List<CustomerValidator> validators;

    public boolean validate(Customer customer) {
        for (CustomerValidator validator : validators) {
            if (!validator.validate(customer)) {
                return false;
            }
        }
        return true;
    }
}
