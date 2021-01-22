package com.ordersmanagement.crm.chains.inspectors;

import com.ordersmanagement.crm.models.entities.Order;
import com.ordersmanagement.crm.services.OrderPaymentsManager;
import com.ordersmanagement.crm.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentDistributorOrderInspector implements OrderInspector {

    private final OrderService orderService;
    private final OrderPaymentsManager orderPaymentsManager;

    @Override
    public Order process(Order order) {
        if (order.getPaySum() > order.getFinalSum()) {
            orderService.updateOrder(order); // Save new 'finalSum' first, to be skipped during re-payment
            orderPaymentsManager.distributeOverpayment(order, order.getPaySum() - order.getFinalSum());
        }
        return order;
    }
}
