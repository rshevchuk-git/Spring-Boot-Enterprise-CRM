package com.ordersmanagement.crm.models.dto;

import com.ordersmanagement.crm.models.entities.Order;
import java.util.List;
import lombok.Data;

@Data
public class OrdersWrapper {
    private List<Order> orders;
}
