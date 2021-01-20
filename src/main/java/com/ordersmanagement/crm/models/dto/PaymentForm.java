package com.ordersmanagement.crm.models.dto;

import com.ordersmanagement.crm.models.entities.CustomerEntity;
import com.ordersmanagement.crm.models.entities.EntrepreneurEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import com.ordersmanagement.crm.utils.AuthUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentForm {
    private EntrepreneurEntity entrepreneur = null;
    private CustomerEntity customer;
    private LocalDateTime paymentDate;
    private int sum;
    private double percentage = 0;
    private String receiver;

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
