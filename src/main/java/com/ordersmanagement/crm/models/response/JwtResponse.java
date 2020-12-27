package com.ordersmanagement.crm.models.response;

import com.ordersmanagement.crm.models.entities.EmployeeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    final private String type = "Bearer";
    private String token;
    private Integer id;
    private String username;
    private List<String> roles;
    private EmployeeEntity employee;
}