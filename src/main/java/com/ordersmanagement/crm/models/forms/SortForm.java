package com.ordersmanagement.crm.models.forms;

import com.ordersmanagement.crm.models.entities.*;
import java.time.LocalDate;
import lombok.Data;

@Data
public class SortForm {
    private Integer orderId;
    private CustomerEntity customer;
    private EntrepreneurEntity entrepreneur;
    private EmployeeEntity employee;
    private StatusEntity status;
    private LocalDate dateFrom;
    private LocalDate dateTill;
    private LocalDate payDateFrom;
    private LocalDate payDateTill;
    private OrderKindEntity orderKind;
    private OrderTypeEntity orderType;
    private String receiver;
    private String details;
}
