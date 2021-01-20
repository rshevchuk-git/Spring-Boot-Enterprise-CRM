package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class OrderServiceFacade {

    private final OrderService orderService;
    private final CustomerPaymentsManager customerPaymentsManager;
    private final OrderPaymentsManager orderPaymentsManager;

    public OrderEntity addOrder(OrderEntity newOrder) throws CustomerNotFoundException {
        customerPaymentsManager.payFromCustomerBalance(newOrder);
        return orderService.saveNewOrder(newOrder);
    }

    public void deleteOrder(OrderEntity order) {
        orderPaymentsManager.removePaymentsFrom(order);
        orderService.deleteById(order.getOrderId());
    }
}
