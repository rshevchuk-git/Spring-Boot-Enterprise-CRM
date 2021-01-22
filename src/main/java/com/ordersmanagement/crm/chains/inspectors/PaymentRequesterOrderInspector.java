package com.ordersmanagement.crm.chains.inspectors;


import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.models.entities.Order;
import com.ordersmanagement.crm.services.CustomerPaymentsManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRequesterOrderInspector implements OrderInspector {

    private final CustomerPaymentsManager customerPaymentsManager;

    @Override
    public Order process(Order order) throws CustomerNotFoundException {
        if (order.getPaySum() < order.getFinalSum()) {
            customerPaymentsManager.payFromCustomerBalance(order);
        }
        return order;
    }
}
