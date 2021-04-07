package com.ordersmanagement.crm.utils;

import com.ordersmanagement.crm.dao.business.CustomerRepository;
import com.ordersmanagement.crm.dao.business.EmployeeRepository;
import com.ordersmanagement.crm.events.AuthorizationEvent;
import com.ordersmanagement.crm.models.entities.Customer;
import com.ordersmanagement.crm.models.entities.Employee;
import com.ordersmanagement.crm.models.entities.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class AuthUtils implements ApplicationListener<AuthorizationEvent> {

    public static Employee LOGGED_EMPLOYEE = null;
    public static Customer LOGGED_CUSTOMER = null;
    public static UserDetailsImpl LOGGED_USER = null;

    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;

    @SneakyThrows
    @Override
    public void onApplicationEvent(AuthorizationEvent authorizationEvent) {
        LOGGED_USER = authorizationEvent.currentUser;
        LOGGED_EMPLOYEE = employeeRepository.findByUserID(LOGGED_USER.getId()).orElse(null);
        LOGGED_CUSTOMER = customerRepository.findById(LOGGED_USER.getCustomerID()).orElse(null);
    }
}
