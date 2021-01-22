package com.ordersmanagement.crm.dao.business;

import com.ordersmanagement.crm.models.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee findByName(String name);
    Optional<Employee> findByUserID(Integer id);
}
