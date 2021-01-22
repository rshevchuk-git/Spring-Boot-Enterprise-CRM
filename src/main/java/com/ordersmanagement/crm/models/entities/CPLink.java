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
public class CPLink {

    @EmbeddedId
    private CPLinkKey id;

    @ManyToOne
    @MapsId("customer_id")
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @MapsId("kind_id")
    @JoinColumn(name = "kind_id")
    private OrderKind orderKind;

    @Column(name = "price")
    private double price;
}
