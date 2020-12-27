package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.users.User32Repository;
import com.ordersmanagement.crm.models.entities.User32;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    final User32Repository userRepository;

    @Override
    @Transactional("userTransactionManager")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User32 user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return UserDetailsImpl.build(user);
    }
}