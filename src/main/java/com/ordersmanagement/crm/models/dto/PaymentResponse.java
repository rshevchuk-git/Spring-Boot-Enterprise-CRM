package com.ordersmanagement.crm.models.dto;

import com.ordersmanagement.crm.models.entities.Customer;
import com.ordersmanagement.crm.models.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private List<Order> paidOrders;
    private Customer customer;
}
