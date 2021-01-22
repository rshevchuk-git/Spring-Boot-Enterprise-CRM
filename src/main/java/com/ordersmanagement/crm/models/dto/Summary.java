package com.ordersmanagement.crm.models.dto;

import com.ordersmanagement.crm.models.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Summary {
    private List<Order> listOfOrders;
    private int paid;
    private int fees;
    private int amount;
    private double m2;
}
