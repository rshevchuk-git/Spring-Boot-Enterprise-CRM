package com.ordersmanagement.crm.chains.inspectors;


import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.services.CustomerPaymentsManager;
import com.ordersmanagement.crm.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRequesterOrderInspector implements OrderInspector {

    private final CustomerPaymentsManager customerPaymentsManager;

    @Override
    public OrderEntity process(OrderEntity order) throws CustomerNotFoundException {
        if (order.getPaySum() < order.getFinalSum()) {
            customerPaymentsManager.payFromCustomerBalance(order);
        }
        return order;
    }
}
