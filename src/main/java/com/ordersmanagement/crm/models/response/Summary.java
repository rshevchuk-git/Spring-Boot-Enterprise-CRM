package com.ordersmanagement.crm.models.response;

import com.ordersmanagement.crm.models.entities.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Summary {
    private List<OrderEntity> listOfOrders;
    private int paid;
    private int fees;
    private int amount;
    private double m2;
}
