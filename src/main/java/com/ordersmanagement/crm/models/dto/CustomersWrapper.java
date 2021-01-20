package com.ordersmanagement.crm.models.dto;

import com.ordersmanagement.crm.models.entities.CustomerEntity;
import java.util.List;
import lombok.Data;

@Data
public class CustomersWrapper {
    private List<CustomerEntity> customers;
}
