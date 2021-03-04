package com.ordersmanagement.crm.chains.inspectors;

import com.ordersmanagement.crm.models.entities.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayLogOrderInspector implements OrderInspector {

    @Override
    public Order process(Order order) {
        if (order.getPayLog().trim().isEmpty()) {
            order.setPayLog("");
        }
        return order;
    }
}
