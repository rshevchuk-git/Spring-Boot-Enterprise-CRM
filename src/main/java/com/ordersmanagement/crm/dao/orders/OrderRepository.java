package com.ordersmanagement.crm.dao.orders;

import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.entities.OrderKindEntity;
import com.ordersmanagement.crm.models.entities.OrderTypeEntity;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer>, QuerydslPredicateExecutor<OrderEntity> {
    @Override
    List<OrderEntity> findAll(Predicate predicate, Sort sort);
    List<OrderEntity> findByOrderType(OrderTypeEntity type);
    List<OrderEntity> findByOrderKind(OrderKindEntity type);
}
