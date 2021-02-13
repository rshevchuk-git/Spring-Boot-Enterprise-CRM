package com.ordersmanagement.crm.services.facades;

import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.models.entities.Order;
import com.ordersmanagement.crm.services.CustomerPaymentsManager;
import com.ordersmanagement.crm.services.MailService;
import com.ordersmanagement.crm.services.OrderPaymentsManager;
import com.ordersmanagement.crm.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class OrderServiceFacade {

    private final OrderService orderService;
    private final CustomerPaymentsManager customerPaymentsManager;
    private final OrderPaymentsManager orderPaymentsManager;

    public Order addOrder(Order newOrder) throws CustomerNotFoundException {
        customerPaymentsManager.payFromCustomerBalance(newOrder);
        return orderService.saveNewOrder(newOrder);
    }

    public void deleteOrder(Order order) {
        orderPaymentsManager.removePaymentsFrom(order);
        orderService.deleteById(order.getOrderId());
    }
}
