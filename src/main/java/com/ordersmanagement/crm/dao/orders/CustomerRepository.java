package com.ordersmanagement.crm.dao.orders;

import com.ordersmanagement.crm.models.entities.CustomerEntity;
import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer>, QuerydslPredicateExecutor<CustomerEntity> {
    @Override
    List<CustomerEntity> findAll(Predicate predicate);
    boolean existsByCustomerName(String name);
    boolean existsByFirstPhoneAndFirstPhoneIsNotNull(String firstPhone);
    boolean existsBySecondPhoneAndSecondPhoneIsNotNull(String secondPhone);
    boolean existsByThirdPhoneAndThirdPhoneIsNotNull(String thirdPhone);
}
