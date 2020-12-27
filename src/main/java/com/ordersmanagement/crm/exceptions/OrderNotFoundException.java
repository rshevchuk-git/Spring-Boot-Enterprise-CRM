package com.ordersmanagement.crm.exceptions;

public class OrderNotFoundException extends Exception {
    public OrderNotFoundException() {
        super("Order with the specified ID does not exist.");
    }
}
