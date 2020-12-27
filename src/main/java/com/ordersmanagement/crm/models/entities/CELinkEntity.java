package com.ordersmanagement.crm.models.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.ordersmanagement.crm.models.keys.CEKey;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_entrepreneur")
public class CELinkEntity {

    @EmbeddedId
    private CEKey id;

    @ManyToOne
    @MapsId("customer_id")
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @ManyToOne
    @MapsId("entrepreneur_id")
    @JoinColumn(name = "entrepreneur_id")
    private EntrepreneurEntity entrepreneur;
}
