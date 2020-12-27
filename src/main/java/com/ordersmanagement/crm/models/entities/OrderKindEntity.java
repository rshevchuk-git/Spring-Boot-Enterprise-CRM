package com.ordersmanagement.crm.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "order_kind")
public class OrderKindEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_kind_id")
    private int kindId;

    @Column(name = "kind")
    private String kindName;
}
