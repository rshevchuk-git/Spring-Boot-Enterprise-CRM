package com.ordersmanagement.crm.models.keys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CPLinkKey implements Serializable {

    @Column(name = "customer_id")
    public int customerId;

    @Column(name = "kind_id")
    public int kindId;
}
