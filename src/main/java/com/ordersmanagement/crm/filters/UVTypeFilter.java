package com.ordersmanagement.crm.filters;

import com.ordersmanagement.crm.auth.ERole;
import com.ordersmanagement.crm.models.entities.Order;
import com.ordersmanagement.crm.models.entities.OrderType;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class UVTypeFilter implements TypeFilter {

    @Override
    public ERole getRole() {
        return ERole.ROLE_UVPRINT;
    }

    @Override
    public List<Order> filterOrderList(List<Order> orderList) {
        return orderList.stream().filter(order -> isUVType(order.getOrderType())).collect(toList());
    }

    @Override
    public List<OrderType> filterTypeList(List<OrderType> typeList) {
        return typeList.stream()
                .filter(this::isUVType)
                .collect(toList());
    }

    private boolean isUVType(OrderType type) {
        return !(type.getTypeName().equals("Борг") || type.getTypeName().equals("Дизайн"));
    }

}
