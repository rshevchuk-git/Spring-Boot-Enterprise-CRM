package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.DimensionRepository;
import com.ordersmanagement.crm.dao.orders.OrderKindRepository;
import com.ordersmanagement.crm.dao.orders.OrderRepository;
import com.ordersmanagement.crm.models.entities.DimensionEntity;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.entities.OrderKindEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderKindService {

    private final DimensionRepository dimensionRepository;
    private final OrderKindRepository orderKindRepository;
    private final OrderRepository orderRepository;

    public List<OrderKindEntity> getAllOrderKinds() {
        return orderKindRepository.findAll();
    }

    public Optional<OrderKindEntity> saveOrderKind(OrderKindEntity newOrderKind) {
        if (orderKindRepository.existsByKindName(newOrderKind.getKindName())) {
            return Optional.empty();
        } else {
            return Optional.of(orderKindRepository.save(newOrderKind));
        }
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

    public DimensionEntity getOrderKindDimensions(Integer orderKindId) {
        return dimensionRepository.findById(orderKindId)
                                  .orElse(new DimensionEntity(orderKindId, 0,0));
    }

    public DimensionEntity saveOrderKindDimensions(Integer orderKindId, Integer width, Integer height) {
        return dimensionRepository.save(new DimensionEntity(orderKindId, width, height));
    }
}
