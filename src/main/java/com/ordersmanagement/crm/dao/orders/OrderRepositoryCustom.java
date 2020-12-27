package com.ordersmanagement.crm.dao.orders;

import com.ordersmanagement.crm.models.entities.OrderEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepositoryCustom {
    List<OrderEntity> getRecentOrders();
    List<OrderEntity> getOrdersMadeBy(Integer customerID);
}
