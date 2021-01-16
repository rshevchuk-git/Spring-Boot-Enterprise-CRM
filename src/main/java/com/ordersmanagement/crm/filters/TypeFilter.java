package com.ordersmanagement.crm.filters;

import com.ordersmanagement.crm.auth.ERole;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.entities.OrderTypeEntity;
import com.ordersmanagement.crm.services.TypeFilterService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface TypeFilter {
    ERole getRole();
    List<OrderTypeEntity> filterTypeList(List<OrderTypeEntity> typeList);
    List<OrderEntity> filterOrderList(List<OrderEntity> orderList);

    @Autowired
    default void registerMyself(TypeFilterService filterService) {
        filterService.registerFilter(getRole(), this);
    }
}
