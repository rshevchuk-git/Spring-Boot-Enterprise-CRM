package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.EmployeeRepository;
import com.ordersmanagement.crm.models.entities.EmployeeEntity;
import com.ordersmanagement.crm.models.entities.User32;
import com.ordersmanagement.crm.models.forms.LoginForm;
import com.ordersmanagement.crm.models.response.JwtResponse;
import com.ordersmanagement.crm.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final EmployeeRepository employeeRepository;
    private final AuthenticationManager authenticationManager;

    public Collection<? extends GrantedAuthority> getUserRoles() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    private Authentication setAuthentication(LoginForm credentials) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    public List<String> getUserRoles(UserDetailsImpl userDetails) {
        return userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public JwtResponse trySignIn(LoginForm credentials) {
        Authentication authentication = setAuthentication(credentials);
        String jwtToken = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = getUserRoles(userDetails);

        EmployeeEntity loggedEmployee = null;
        if(!roles.isEmpty()) {
            loggedEmployee = employeeRepository.findByUserID(userDetails.getId()).orElse(null);
        } else {
            roles.add("ROLE_CUSTOMER");
        }

        return new JwtResponse(jwtToken, userDetails.getId(), userDetails.getUsername(), roles, loggedEmployee);
    }
}
