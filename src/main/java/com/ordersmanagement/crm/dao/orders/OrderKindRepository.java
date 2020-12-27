package com.ordersmanagement.crm.dao.orders;

import com.ordersmanagement.crm.models.entities.OrderKindEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderKindRepository extends JpaRepository<OrderKindEntity, Integer> {
    boolean existsByKindName(String name);
    boolean existsByKindId(Integer id);
}
