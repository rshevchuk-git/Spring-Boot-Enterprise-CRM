package com.ordersmanagement.crm.models.dto;

import com.ordersmanagement.crm.models.entities.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    final private String type = "Bearer";
    private String token;
    private Integer id;
    private String username;
    private List<String> roles;
    private Employee employee;
}