package com.ordersmanagement.crm.dao.orders;

import com.ordersmanagement.crm.models.entities.*;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer>, QuerydslPredicateExecutor<OrderEntity> {
    @Override
    List<OrderEntity> findAll(Predicate predicate, Sort sort);
    List<OrderEntity> findByOrderType(OrderTypeEntity type);
    List<OrderEntity> findByOrderKind(OrderKindEntity type);

    @Query(value = "from OrderEntity where customer = ?1 and (?2 is null or entrepreneur = ?2) and paySum < finalSum order by orderId asc")
    List<OrderEntity> getUnpaidOrdersOf(CustomerEntity customer, EntrepreneurEntity entrepreneur);

    @Query(value = "from OrderEntity where customer.customerId = ?1 order by orderId desc")
    List<OrderEntity> getOrdersMadeBy(Integer customerID);

    @Query(value = "from OrderEntity where date >= ?1 order by orderId desc")
    List<OrderEntity> getOrdersStartingFrom(LocalDateTime since);
}
