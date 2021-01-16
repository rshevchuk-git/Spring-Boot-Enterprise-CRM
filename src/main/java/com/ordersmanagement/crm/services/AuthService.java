package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.EmployeeRepository;
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
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public JwtResponse trySignIn(LoginForm credentials) {
        JwtResponse jwtResponse = new JwtResponse();
        Authentication authentication = setAuthentication(credentials);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        jwtResponse.setToken(jwtUtils.generateJwtToken(authentication));
        jwtResponse.setId(userDetails.getId());
        jwtResponse.setUsername(userDetails.getUsername());
        jwtResponse.setRoles(getUserRoles(userDetails));
        jwtResponse.setEmployee(employeeRepository.findByUserID(userDetails.getId()).orElse(null));

        return jwtResponse;
    }
}
