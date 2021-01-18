package com.ordersmanagement.crm.chains.validators;

import com.ordersmanagement.crm.models.entities.CustomerEntity;

public interface CustomerValidator {
    boolean validate(CustomerEntity customer);
}
