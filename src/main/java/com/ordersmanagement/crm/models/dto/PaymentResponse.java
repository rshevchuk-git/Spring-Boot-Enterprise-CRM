package com.ordersmanagement.crm.models.dto;

import com.ordersmanagement.crm.models.entities.CustomerEntity;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private List<OrderEntity> paidOrders;
    private CustomerEntity customer;
}
