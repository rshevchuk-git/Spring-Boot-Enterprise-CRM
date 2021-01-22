package com.ordersmanagement.crm.models.dto;

import com.ordersmanagement.crm.models.entities.Customer;
import com.ordersmanagement.crm.models.entities.Entrepreneur;
import com.ordersmanagement.crm.models.pojos.Payment;
import com.ordersmanagement.crm.utils.AuthUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentForm {
    private Entrepreneur entrepreneur = null;
    private Customer customer;
    private LocalDateTime paymentDate;
    private int sum;
    private double percentage = 0;
    private String receiver;

    public PaymentForm(Customer customer, Payment payment) {
        this.customer = customer;
        this.sum = payment.getSum();
        this.paymentDate = payment.getDateTime();
        this.receiver = payment.getReceiver();
    }

    public void setPaymentDate(LocalDateTime paymentDateTime) {
        this.paymentDate = Objects.requireNonNullElseGet(paymentDateTime, () -> LocalDateTime.now(ZoneId.of("Europe/Kiev")));
    }

    public Integer getCustomerId() {
        return this.customer.getCustomerId();
    }

    public void setReceiver(String receiver) {
        if (receiver.equals("Готівка")) {
            this.receiver = AuthUtils.LOGGED_EMPLOYEE.getName();
        } else {
            this.receiver = receiver;
        }
    }
}
