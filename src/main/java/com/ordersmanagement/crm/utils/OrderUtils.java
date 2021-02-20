package com.ordersmanagement.crm.utils;

import com.ordersmanagement.crm.models.dto.SortForm;
import com.ordersmanagement.crm.models.entities.Customer;
import com.ordersmanagement.crm.models.entities.Order;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public static int totalOrdersSum(List<Order> orders) {
        return orders.stream().reduce(0, (a, b) -> a + b.getFinalSum(), Integer::sum);
    }

    public static double calculateM2(Order order) {
        double m2Val = (order.getWidth() * order.getHeight() / 1000000.00) * order.getAmount();
        return (double) Math.round(m2Val * 1000d) / 1000d;
    }

    public static List<Order> filterByPaymentDates(List<Order> orderList, LocalDate from, LocalDate till) {
        return orderList.stream()
                .filter(o -> Arrays.stream(o.getPayLog().split("\n"))
                        .filter(log -> log.trim().length() > 0)
                        .anyMatch(log -> from == null || PaymentUtils.getLocalDateTimeFromLog(log).toLocalDate().isAfter(from.minusDays(1))))
                .filter(o -> Arrays.stream(o.getPayLog().split("\n"))
                        .filter(log -> log.trim().length() > 0)
                        .anyMatch(log -> till == null || PaymentUtils.getLocalDateTimeFromLog(log).toLocalDate().isBefore(till.plusDays(1))))
                .collect(Collectors.toList());
    }

    public static int totalOrdersPaid(List<Order> orders, SortForm selections) {
        return orders.stream()
                .map(Order::getPayLog)
                .map(log -> log.split("\n"))
                .flatMap(Arrays::stream)
                .filter(log -> selections.getPayDateFrom() == null || PaymentUtils.getLocalDateTimeFromLog(log).toLocalDate().isAfter(selections.getPayDateFrom().minusDays(1)))
                .filter(log -> selections.getPayDateTill() == null || PaymentUtils.getLocalDateTimeFromLog(log).toLocalDate().isBefore(selections.getPayDateTill().plusDays(1)))
                .filter(log -> selections.getReceiver() == null || selections.getReceiver().isEmpty() || log.contains(selections.getReceiver()))
                .reduce(0, (currentVal, log) -> currentVal + PaymentUtils.getSumFromLog(log), Integer::sum);
    }
}
