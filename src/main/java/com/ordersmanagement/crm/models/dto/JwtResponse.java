package com.ordersmanagement.crm.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ordersmanagement.crm.models.entities.Customer;
import com.ordersmanagement.crm.models.entities.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({ "money" })
public class JwtResponse {
    final private String type = "Bearer";
    private String token;
    private Integer id;
    private String username;
    private String fullName;
    private List<String> roles;
    private Employee employee;
    private Customer customer;
}