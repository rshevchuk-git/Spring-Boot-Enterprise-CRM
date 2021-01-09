package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.OrderKindRepository;
import com.ordersmanagement.crm.dao.orders.OrderRepository;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.entities.OrderKindEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderKindService {

    private final OrderRepository orderRepository;
    private final OrderKindRepository orderKindRepository;

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
        List<OrderEntity> byOrderKind = orderRepository.findByOrderKind(replaceKind);
        byOrderKind.forEach(order -> order.setOrderKind(newKind));
        orderRepository.saveAll(byOrderKind);

        deleteOrderKind(replaceKind.getKindId());
    }
}
