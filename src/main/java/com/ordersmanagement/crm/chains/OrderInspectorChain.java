package com.ordersmanagement.crm.chains;

import com.ordersmanagement.crm.chains.inspectors.OrderInspector;
import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.exceptions.OrderNotFoundException;
import com.ordersmanagement.crm.models.entities.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderInspectorChain {

    @Autowired
    private List<OrderInspector> inspectors;

    public Order inspect(Order order) throws OrderNotFoundException, CustomerNotFoundException {
        for (OrderInspector inspector : inspectors) {
            inspector.process(order);
        }
        return order;
    }
}
