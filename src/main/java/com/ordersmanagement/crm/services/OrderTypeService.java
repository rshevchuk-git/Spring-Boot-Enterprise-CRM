package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.auth.ERole;
import com.ordersmanagement.crm.dao.orders.OrderRepository;
import com.ordersmanagement.crm.dao.orders.OrderTypeRepository;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.entities.OrderTypeEntity;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class OrderTypeService {

    public final HashMap<String, Function<OrderTypeEntity, Boolean>> typeFilterByRole = new HashMap<>() {{
        put(ERole.ROLE_GROUNDFLOOR.toString(), OrderTypeService.this::isGroundFloorType);
        put(ERole.ROLE_UVPRINT.toString(),     OrderTypeService.this::isUVType);
    }};

    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderTypeRepository orderTypeRepository;

    public List<OrderTypeEntity> getAllOrderTypes() {
        List<OrderTypeEntity> orderTypes = orderTypeRepository.findAll();
        return filterAllowedOrderTypesForRoles(orderTypes);
    }

    public Optional<OrderTypeEntity> saveOrderType(OrderTypeEntity orderType) {
        if (orderTypeRepository.existsByTypeName(orderType.getTypeName())) return Optional.empty();
        return Optional.of(orderTypeRepository.save(orderType));
    }

    public boolean deleteOrderType(Integer orderTypeId) {
        if (orderTypeRepository.existsById(orderTypeId)) {
            orderTypeRepository.deleteById(orderTypeId);
            return true;
        } else {
            return false;
        }
    }

    public void replaceOrderType(OrderTypeEntity replaceType, OrderTypeEntity newType) {
        List<OrderEntity> byOrderType = orderRepository.findByOrderType(replaceType);
        byOrderType.forEach(order -> order.setOrderType(newType));
        orderRepository.saveAll(byOrderType);

        orderTypeRepository.deleteById(replaceType.getTypeId());
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

    private boolean isUVType(OrderTypeEntity type) {
        return !(type.getTypeName().equals("Борг") || type.getTypeName().equals("Дизайн"));
    }

    public List<OrderTypeEntity> filterAllowedOrderTypesForRoles(List<OrderTypeEntity> typeList) {
        for (GrantedAuthority grantedAuthority : authService.getUserRoles()) {
            typeList = typeList.stream()
                    .filter(orderType -> typeFilterByRole
                            .getOrDefault(grantedAuthority.getAuthority(), (val) -> true)
                            .apply(orderType))
                    .collect(toList());
        }
        return typeList;
    }
}
