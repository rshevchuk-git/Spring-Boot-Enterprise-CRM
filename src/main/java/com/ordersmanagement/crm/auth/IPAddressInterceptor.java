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

    private final List<String> allowedRoles = List.of(ERole.ROLE_ADMIN.toString(), ERole.ROLE_CUSTOMER.toString());

    @Value(value = "${allowed.addresses}")
    private List<String> allowedAddresses;

    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        for (GrantedAuthority authority : authService.getUserRoles()) {
            if (isAllowed(authority)) {
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

    private boolean isAllowed(GrantedAuthority grantedAuthority) {
        return allowedRoles.contains(grantedAuthority.getAuthority());
    }
}
