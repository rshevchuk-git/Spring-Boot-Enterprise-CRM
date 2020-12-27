package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.OrderRepositoryCustom;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    @PersistenceContext
    private EntityManager ordersEntityManager;

    @Override
    public List<OrderEntity> getRecentOrders() {
        LocalDateTime last14Days = LocalDateTime.now(ZoneId.of("Europe/Kiev")).minusDays(14);
        TypedQuery<OrderEntity> query = ordersEntityManager.createQuery("from OrderEntity where date >= :last14Days order by id desc", OrderEntity.class);
        query.setParameter("last14Days", last14Days);
        return query.getResultList();
    }

    @Override
    public List<OrderEntity> getOrdersMadeBy(Integer customerId) {
        TypedQuery<OrderEntity> query = ordersEntityManager.createQuery("from OrderEntity where customer.customerId = :id order by id desc", OrderEntity.class);
        query.setParameter("id", customerId);
        return query.getResultList();
    }
}
