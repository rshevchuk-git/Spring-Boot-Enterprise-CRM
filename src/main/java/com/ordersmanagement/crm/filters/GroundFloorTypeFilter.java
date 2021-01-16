package com.ordersmanagement.crm.filters;

import com.ordersmanagement.crm.auth.ERole;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.entities.OrderTypeEntity;
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
    public List<OrderTypeEntity> filterTypeList(List<OrderTypeEntity> typeList) {
        return typeList.stream()
                .filter(this::isGroundFloorType)
                .collect(toList());
    }

    @Override
    public List<OrderEntity> filterOrderList(List<OrderEntity> orderList) {
        return orderList.stream().filter(order -> isGroundFloorType(order.getOrderType())).collect(toList());
    }

    private boolean isGroundFloorType(OrderTypeEntity type) {
        return  type.getTypeName().equals("Сольвентний друк") ||
                type.getTypeName().equals("Офсетний друк") ||
                type.getTypeName().equals("Екосольвентний друк") ||
                type.getTypeName().equals("Матеріал") ||
                type.getTypeName().equals("Календарі") ||
                type.getTypeName().equals("Цифровий друк") ||
                type.getTypeName().equals("Інше");
    }
}
