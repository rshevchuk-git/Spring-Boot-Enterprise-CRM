package com.ordersmanagement.crm.chains.inspectors;

import com.ordersmanagement.crm.exceptions.CustomerNotFoundException;
import com.ordersmanagement.crm.exceptions.OrderNotFoundException;
import com.ordersmanagement.crm.models.entities.Order;

public interface OrderInspector {
    Order process(Order order) throws OrderNotFoundException, CustomerNotFoundException;
}
