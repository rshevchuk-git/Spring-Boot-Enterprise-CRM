package com.ordersmanagement.crm.dao.business;

import com.ordersmanagement.crm.models.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Integer> {
}
