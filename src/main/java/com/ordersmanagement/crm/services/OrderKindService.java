package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.OrderKindRepository;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.entities.OrderKindEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderKindService {

    private final OrderKindRepository orderKindRepository;
    private final OrderService orderService;

    public List<OrderKindEntity> getAllOrderKinds() {
        return orderKindRepository.findAll();
    }
    
    public OrderKindEntity saveOrderKind(OrderKindEntity newOrderKind) {
        return orderKindRepository.save(newOrderKind);
    }

    public boolean deleteOrderKind(Integer orderKindId) {
        if (orderKindRepository.existsByKindId(orderKindId)) {
            orderKindRepository.deleteById(orderKindId);
            return true;
        } else {
            return false;
        }
    }

    public void replaceOrderKinds(OrderKindEntity replaceKind, OrderKindEntity newKind) {
        List<OrderEntity> ordersByKind = orderService.getAllByOrderKind(replaceKind);
        ordersByKind.forEach(order -> order.setOrderKind(newKind));
        orderService.saveAll(ordersByKind);
    }
}
