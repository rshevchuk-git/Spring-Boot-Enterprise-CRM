package com.ordersmanagement.crm.exceptions;

public class CustomerNotFoundException extends Exception {
    public CustomerNotFoundException() {
        super("Customer with the specified ID does not exist.");
    }
}
