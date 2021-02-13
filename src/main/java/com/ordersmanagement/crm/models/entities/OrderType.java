package com.ordersmanagement.crm.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "order_type")
public class OrderType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_type_id")
    private int typeId;

    @ToString.Exclude
    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "order_type_id")
    private List<OrderKind> orderKinds;

    @Column(name = "type")
    private String typeName;
}
