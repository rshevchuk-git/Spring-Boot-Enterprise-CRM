package com.ordersmanagement.crm.dao.orders;

import com.ordersmanagement.crm.models.entities.DimensionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DimensionRepository extends JpaRepository<DimensionEntity, Integer> {
}
