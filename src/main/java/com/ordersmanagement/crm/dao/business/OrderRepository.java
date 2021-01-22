package com.ordersmanagement.crm.dao.business;

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
public interface OrderRepository extends JpaRepository<Order, Integer>, QuerydslPredicateExecutor<Order> {
    @Override
    List<Order> findAll(Predicate predicate, Sort sort);
    List<Order> findByOrderType(OrderType type);
    List<Order> findByOrderKind(OrderKind type);

    @Query(value = "from Order where customer = ?1 and (?2 is null or entrepreneur = ?2) and paySum < finalSum order by orderId asc")
    List<Order> getUnpaidOrdersOf(Customer customer, Entrepreneur entrepreneur);

    @Query(value = "from Order where customer.customerId = ?1 order by orderId desc")
    List<Order> getOrdersMadeBy(Integer customerID);

    @Query(value = "from Order where date >= ?1 order by orderId desc")
    List<Order> getOrdersStartingFrom(LocalDateTime since);
}
