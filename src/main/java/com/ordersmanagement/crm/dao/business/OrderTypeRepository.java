package com.ordersmanagement.crm.dao.business;

import com.ordersmanagement.crm.models.entities.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTypeRepository extends JpaRepository<OrderType, Integer> {
    boolean existsByTypeName(String name);
}
