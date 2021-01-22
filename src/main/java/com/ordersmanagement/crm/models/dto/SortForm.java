package com.ordersmanagement.crm.models.dto;

import com.ordersmanagement.crm.models.entities.*;
import java.time.LocalDate;
import lombok.Data;

@Data
public class SortForm {
    private Integer orderId;
    private Customer customer;
    private Entrepreneur entrepreneur;
    private Employee employee;
    private Status status;
    private LocalDate dateFrom;
    private LocalDate dateTill;
    private LocalDate payDateFrom;
    private LocalDate payDateTill;
    private OrderKind orderKind;
    private OrderType orderType;
    private String receiver;
    private String details;
}
