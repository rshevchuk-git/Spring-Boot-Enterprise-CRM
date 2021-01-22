package com.ordersmanagement.crm.utils;

import com.ordersmanagement.crm.models.entities.Order;

import java.util.Arrays;
import java.util.List;

public final class OrderUtils {

    public static double totalOrdersM2(List<Order> orders) {
        return orders.stream().reduce(0.0, (a, b) -> (double) Math.round((a + b.getM2()) * 1000d) / 1000d, Double::sum);
    }

    public static int totalOrdersFees(List<Order> orders) {
        return orders.stream().reduce(0, (a, b) -> a + b.getFees(), Integer::sum);
    }

    public static int totalOrdersAmount(List<Order> orders) {
        return orders.stream().reduce(0, (a, b) -> a + b.getAmount(), Integer::sum);
    }

    public static double calculateM2(Order order) {
        double m2Val = (order.getWidth() * order.getHeight() / 1000000.00) * order.getAmount();
        return (double) Math.round(m2Val * 1000d) / 1000d;
    }

    public static int totalOrdersPaid(List<Order> orders, String receiver) {
        return orders.stream().reduce(0, (sum, order) -> {
            if (receiver != null && !receiver.isEmpty()) {
                int paymentSum = Arrays.stream(order.getPayLog().split("\n"))
                        .filter(payment -> payment.contains(receiver))
                        .reduce(0, (preVal, log) -> preVal + Integer.parseInt(log.substring(log.indexOf("ма : ") + 5, log.indexOf(" Отр") - 1)), Integer::sum);
                return sum + paymentSum;
            } else {
                return sum + order.getPaySum();
            }
        }, Integer::sum);
    }
}
