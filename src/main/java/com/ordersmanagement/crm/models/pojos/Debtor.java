package com.ordersmanagement.crm.models.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Debtor {
    private String name;
    private LocalDateTime lastOrderDate;
    private LocalDateTime lastPaymentDate;
    private long debt;
}
