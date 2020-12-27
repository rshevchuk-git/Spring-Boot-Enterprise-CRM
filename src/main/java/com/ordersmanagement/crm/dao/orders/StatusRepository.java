package com.ordersmanagement.crm.dao.orders;

import com.ordersmanagement.crm.models.entities.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<StatusEntity, Integer> {
}
