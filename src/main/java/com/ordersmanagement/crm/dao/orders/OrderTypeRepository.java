package com.ordersmanagement.crm.dao.orders;

import com.ordersmanagement.crm.models.entities.OrderTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTypeRepository extends JpaRepository<OrderTypeEntity, Integer> {
    boolean existsByTypeName(String name);
}
