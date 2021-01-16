package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.filters.TypeFilter;
import com.ordersmanagement.crm.auth.ERole;
import com.ordersmanagement.crm.models.entities.OrderEntity;
import com.ordersmanagement.crm.models.entities.OrderTypeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TypeFilterService {

    private final AuthService authService;

    public Map<String, TypeFilter> roleFilters = new HashMap<>();

    public void registerFilter(ERole role, TypeFilter filter) {
        roleFilters.put(role.toString(), filter);
    }

    public List<OrderTypeEntity> filterTypesForRoles(List<OrderTypeEntity> typeList) {
        for (GrantedAuthority authority : authService.getUserRoles()) {
            TypeFilter roleFilter = roleFilters.get(authority.getAuthority());
            if (roleFilter != null ) {
                typeList = roleFilter.filterTypeList(typeList);
            }
        }
        return typeList;
    }

    public List<OrderEntity> filterOrdersForRoles(List<OrderEntity> orderList) {
        for (GrantedAuthority authority : authService.getUserRoles()) {
            TypeFilter roleFilter = roleFilters.get(authority.getAuthority());
            if (roleFilter != null ) {
                orderList = roleFilter.filterOrderList(orderList);
            }
        }
        return orderList;
    }
}
