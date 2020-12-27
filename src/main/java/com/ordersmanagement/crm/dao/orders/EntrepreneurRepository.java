package com.ordersmanagement.crm.dao.orders;

import com.ordersmanagement.crm.models.entities.EntrepreneurEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntrepreneurRepository extends JpaRepository<EntrepreneurEntity, Integer> {
}
