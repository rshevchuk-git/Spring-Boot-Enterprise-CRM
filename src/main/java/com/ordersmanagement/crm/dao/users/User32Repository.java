package com.ordersmanagement.crm.dao.users;

import com.ordersmanagement.crm.models.entities.User32;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface User32Repository extends JpaRepository<User32, Integer> {
    Optional<User32> findByUsername(String username);
    Optional<User32> findById(Integer id);
    List<User32> findAllByCustomerID(Integer id);
    Boolean existsByUsername(String username);
}
