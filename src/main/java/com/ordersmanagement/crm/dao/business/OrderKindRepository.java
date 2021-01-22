package com.ordersmanagement.crm.dao.business;

import com.ordersmanagement.crm.models.entities.OrderKind;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderKindRepository extends JpaRepository<OrderKind, Integer> {
    boolean existsByKindName(String name);
    boolean existsByKindId(Integer id);
}
