package com.ordersmanagement.crm.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "order_type")
public class OrderTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_type_id")
    private int typeId;

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "order_type_id")
    private List<OrderKindEntity> orderKinds;

    @Column(name = "type")
    private String typeName;
}
