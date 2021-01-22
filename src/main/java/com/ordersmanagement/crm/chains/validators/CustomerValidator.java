package com.ordersmanagement.crm.chains.validators;

import com.ordersmanagement.crm.models.entities.Customer;

public interface CustomerValidator {
    boolean validate(Customer customer);
}
