package com.ordersmanagement.crm.filters;

import com.ordersmanagement.crm.auth.ERole;
import com.ordersmanagement.crm.models.entities.Order;
import com.ordersmanagement.crm.models.entities.OrderType;
import com.ordersmanagement.crm.services.TypeFilterService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface TypeFilter {
    ERole getRole();
    List<OrderType> filterTypeList(List<OrderType> typeList);
    List<Order> filterOrderList(List<Order> orderList);

    @Autowired
    default void registerMyself(TypeFilterService filterService) {
        filterService.registerFilter(getRole(), this);
    }
}
