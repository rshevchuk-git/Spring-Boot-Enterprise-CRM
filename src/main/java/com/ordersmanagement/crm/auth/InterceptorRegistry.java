package com.ordersmanagement.crm.auth;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
@AllArgsConstructor
public class InterceptorRegistry implements WebMvcConfigurer {

    private final IPAddressInterceptor ipAddressInterceptor;

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
