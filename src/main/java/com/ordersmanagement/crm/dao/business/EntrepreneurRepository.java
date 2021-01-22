package com.ordersmanagement.crm.dao.business;

import com.ordersmanagement.crm.models.entities.Entrepreneur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntrepreneurRepository extends JpaRepository<Entrepreneur, Integer> {
}
