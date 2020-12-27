package com.ordersmanagement.crm.dao.orders;

import com.ordersmanagement.crm.models.entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {
    EmployeeEntity findByName(String name);
    Optional<EmployeeEntity> findByUserID(Integer id);
}
