package com.ordersmanagement.crm.dao.business;

import com.ordersmanagement.crm.models.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    List<Customer> findAllByCustomerIdAndPayLogContaining(Integer customerId, String receiver);
    List<Customer> findAllByPayLogContaining(String receiver);
    boolean existsByCustomerName(String name);
    boolean existsByFirstPhone(String phone);
    boolean existsBySecondPhone(String phone);
    boolean existsByThirdPhone(String phone);
}
