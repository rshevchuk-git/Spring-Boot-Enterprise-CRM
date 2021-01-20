package com.ordersmanagement.crm.models.pojos;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    public static Payment EMPTY = new Payment(0, LocalDateTime.MIN, "");

    private int sum = 0;
    private LocalDateTime dateTime;
    private String receiver;

    @Override
    public String toString() {
        return "Дата : " + dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                "  Сума : " + sum + "  Отримувач : " + receiver + "\n";
    }
}
