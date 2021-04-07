package com.ordersmanagement.crm.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ordersmanagement.crm.auth.ERole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String username;
    private String fullName;
    private Collection<? extends GrantedAuthority> authorities;
    private Integer customerID;
    @JsonIgnore
    private String password;

    public UserDetailsImpl(Integer id, String username, String fullName, String password,
                           Collection<? extends GrantedAuthority> authorities,
                           Integer customerID) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.authorities = authorities;
        this.customerID = customerID;
    }

    public static UserDetailsImpl build(User32 user) {
        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority(ERole.ROLE_CUSTOMER.toString()));
            authorities.add(new SimpleGrantedAuthority(ERole.ROLE_ORDERS_EXPORTER.toString()));
        }

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getPassword(),
                authorities,
                user.getCustomerID());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}