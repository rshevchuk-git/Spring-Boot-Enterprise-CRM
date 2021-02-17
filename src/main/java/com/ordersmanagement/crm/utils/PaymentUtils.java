package com.ordersmanagement.crm.utils;

import com.ordersmanagement.crm.models.pojos.Payment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;

public final class PaymentUtils {

    public static String getTypeNameFromLog(String log) {
        return log.substring(log.indexOf("ач :") + 5);
    }

    public static int calculateOperatingSum(int paymentSum, double paymentPercentage) {
        return (int) Math.ceil(paymentSum - paymentSum * paymentPercentage / 100);
    }

    public static int getSumFromLog(String log) {
        return Integer.parseInt(log.substring(log.indexOf("ма : ") + 5, log.indexOf(" Отр") - 1));
    }

    public static String replaceSumInLog(String log, Integer newSum) {
        return log.substring(0, log.indexOf("ма : ") + 5) + newSum + log.substring(log.indexOf(" Отр") - 1);
    }

    public static String getReceiverFromLog(String log) {
        return log.substring(log.indexOf("ач : ") + 5);
    }

    public static String formatPayLog(LocalDateTime paymentDateTime, int paySum, String receiver) {
        return "Дата : " + paymentDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
               "  Сума : " + paySum + "  Отримувач : " + receiver + "\n";
    }

    public static Payment getLastPayment(String fullLog) {
        String log = "";
        if (fullLog.chars().filter(ch -> ch == '\n').count() > 1) {
            int idx = fullLog.trim().lastIndexOf('\n');
            log = fullLog.substring(idx).trim();
        } else {
            log = fullLog.trim();
        }
        return new Payment(getSumFromLog(log), getLocalDateTimeFromLog(log), getReceiverFromLog(log));
    }

    public static LocalDateTime getLocalDateTimeFromLog(String log) {
        DateTimeFormatter DATE_FORMAT =
                new DateTimeFormatterBuilder().appendPattern("dd/MM/yyyy[ [HH][:mm][:ss][.SSS]]")
                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                        .toFormatter();
        return LocalDateTime.parse(log.substring(log.indexOf("та : ") + 5, log.indexOf(" Сум") - 1), DATE_FORMAT);
    }

    public static int calculatePaymentSum(String payLog) {
        if ( payLog == null || payLog.trim().isEmpty()) return 0;

        String[] payments = payLog.split("\\n");
        if (payments.length > 0) {
            return Arrays.stream(payments).map(PaymentUtils::getSumFromLog).reduce(0, Integer::sum);
        }
        return 0;
    }
}
