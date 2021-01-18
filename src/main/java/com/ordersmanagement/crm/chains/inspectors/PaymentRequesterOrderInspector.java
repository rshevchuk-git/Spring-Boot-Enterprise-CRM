package com.ordersmanagement.crm.chains.inspectors;


import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRequesterOrderInspector implements OrderInspector {

    private final PaymentService paymentService;

    @Override
    public OrderEntity process(OrderEntity order) throws CustomerNotFoundException {
        if (order.getPaySum() < order.getFinalSum()) {
            paymentService.payFromCustomerBalance(order);
        }
        return order;
    }
}
