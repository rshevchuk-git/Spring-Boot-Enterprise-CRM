package com.ordersmanagement.crm.filters;

import com.ordersmanagement.crm.auth.ERole;
import com.ordersmanagement.crm.models.entities.Order;
import com.ordersmanagement.crm.models.entities.OrderType;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class GroundFloorTypeFilter implements TypeFilter {

    @Override
    public ERole getRole() {
        return ERole.ROLE_GROUNDFLOOR;
    }

    @Override
    public List<OrderType> filterTypeList(List<OrderType> typeList) {
        return typeList.stream()
                .filter(this::isGroundFloorType)
                .collect(toList());
    }

    @Override
    public List<Order> filterOrderList(List<Order> orderList) {
        return orderList.stream().filter(order -> isGroundFloorType(order.getOrderType())).collect(toList());
    }

    private boolean isGroundFloorType(OrderType type) {
        return  type.getTypeName().equals("Сольвентний друк") ||
                type.getTypeName().equals("Офсетний друк") ||
                type.getTypeName().equals("Екосольвентний друк") ||
                type.getTypeName().equals("Матеріал") ||
                type.getTypeName().equals("Календарі") ||
                type.getTypeName().equals("Цифровий друк") ||
                type.getTypeName().equals("Інше");
    }
}
