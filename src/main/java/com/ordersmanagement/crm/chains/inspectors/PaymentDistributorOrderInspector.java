package com.ordersmanagement.crm.chains.inspectors;

import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.services.OrderService;
import com.ordersmanagement.crm.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentDistributorOrderInspector implements OrderInspector {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @Override
    public OrderEntity process(OrderEntity order) throws CustomerNotFoundException {
        if (order.getPaySum() > order.getFinalSum()) {
            orderService.updateOrder(order); // Save new 'finalSum' first, to be skipped during re-payment
            paymentService.distributeOverpayment(order, order.getPaySum() - order.getFinalSum());
        }
        return order;
    }
}
