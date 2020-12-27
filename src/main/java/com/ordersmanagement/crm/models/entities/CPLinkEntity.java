package com.ordersmanagement.crm.models.entities;

import com.ordersmanagement.crm.models.keys.CPLinkKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "price_links")
public class CPLinkEntity {

    @EmbeddedId
    private CPLinkKey id;

    @ManyToOne
    @MapsId("customer_id")
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @ManyToOne
    @MapsId("kind_id")
    @JoinColumn(name = "kind_id")
    private OrderKindEntity kind;

    @Column(name = "price")
    private double price;
}
