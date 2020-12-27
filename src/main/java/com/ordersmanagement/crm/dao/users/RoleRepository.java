package com.ordersmanagement.crm.dao.users;

import com.ordersmanagement.crm.auth.ERole;
import com.ordersmanagement.crm.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}