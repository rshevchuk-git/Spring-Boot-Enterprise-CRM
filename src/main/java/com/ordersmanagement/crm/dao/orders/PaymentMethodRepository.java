package com.ordersmanagement.crm.dao.orders;

import com.ordersmanagement.crm.models.entities.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Integer> {
    boolean existsByTypeName(String name);
}
