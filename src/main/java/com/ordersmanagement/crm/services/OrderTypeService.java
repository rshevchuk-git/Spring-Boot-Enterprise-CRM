package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.business.OrderTypeRepository;
import com.ordersmanagement.crm.models.entities.Order;
import com.ordersmanagement.crm.models.entities.OrderType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderTypeService {

    private final OrderTypeRepository orderTypeRepository;
    private final OrderService orderService;
    private final TypeFilterService typeFilterService;

    public List<OrderType> getAllOrderTypes() {
        List<OrderType> orderTypes = orderTypeRepository.findAll();
        return typeFilterService.filterTypesForRoles(orderTypes);
    }

    public Optional<OrderType> saveOrderType(OrderType orderType) {
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

    public void replaceOrderType(OrderType replaceType, OrderType newType) {
        List<Order> byOrderType = orderService.getAllByOrderType(replaceType);
        byOrderType.forEach(order -> order.setOrderType(newType));
        orderService.saveAll(byOrderType);
    }
}
