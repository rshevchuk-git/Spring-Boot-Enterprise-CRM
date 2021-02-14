package com.ordersmanagement.crm.auth;

import com.ordersmanagement.crm.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class IPAddressInterceptor implements HandlerInterceptor {

    @Value(value = "${allowed.addresses}")
    private List<String> allowedAddresses;

    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (authService.getUserRoles().isEmpty()) {
            return true;
        }

        for (GrantedAuthority grantedAuthority : authService.getUserRoles()) {
            if (ERole.ROLE_ADMIN.toString().equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }

        String clientAddress = request.getHeader("x-forwarded-for");
        if(clientAddress == null) {
            clientAddress = request.getRemoteAddr();
        }
        clientAddress = clientAddress.contains(",") ? clientAddress.split(",")[0] : clientAddress;

        return allowedAddresses.contains(clientAddress);
    }
}
