package com.ordersmanagement.crm.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
public class InterceptorRegistry implements WebMvcConfigurer {

    private final IPAddressInterceptor ipAddressInterceptor;

    @Autowired
    public InterceptorRegistry(IPAddressInterceptor ipAddressInterceptor) {
        this.ipAddressInterceptor = ipAddressInterceptor;
    }

    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        registry.addInterceptor(ipAddressInterceptor).addPathPatterns(List.of(
                "/api/orders/**",
                "/api/ce-links/**",
                "/api/cp-links/**",
                "/api/order-types/**",
                "/api/customers/**",
                "/api/entrepreneurs/**",
                "/api/order-kinds/**",
                "/api/payments/**",
                "/api/statuses/**"));
    }
}
