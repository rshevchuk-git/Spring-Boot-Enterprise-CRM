package com.ordersmanagement.crm.exceptions;

public class EmployeeNotFoundException extends Exception {
    public EmployeeNotFoundException() {
        super("Employee with specified ID does not exist.");
    }
}

