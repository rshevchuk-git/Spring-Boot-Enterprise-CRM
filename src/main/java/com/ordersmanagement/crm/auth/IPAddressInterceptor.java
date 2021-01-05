package com.ordersmanagement.crm.auth;
import com.ordersmanagement.crm.models.entities.OrderTypeEntity;
import com.ordersmanagement.crm.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

@Component
public class IPAddressInterceptor implements HandlerInterceptor {

    final List<String> allowedAddresses = List.of("195.114.145.25");

    public boolean isAllowedAddress(String ip) {
        return allowedAddresses.contains(ip);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            if ("ROLE_ADMIN".equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        String ipAddress = request.getHeader("x-forwarded-for");
        if(ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        String clientAddress = ipAddress.contains(",") ? ipAddress.split(",")[0] : ipAddress;
        if(isAllowedAddress(clientAddress)) {
            System.out.println("[SUCCESS] Trying to connect from: " + clientAddress);
            return true;
        } else {
            System.out.println("[ERROR] Trying to connect from: " + clientAddress);
            return false;
        }
    }
}
