package com.ordersmanagement.crm.models.dto;

import com.ordersmanagement.crm.models.entities.OrderEntity;
import java.util.List;
import lombok.Data;

@Data
public class OrdersWrapper {
    private List<OrderEntity> orders;
}
