package com.ordersmanagement.crm.models.keys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CEKey implements Serializable {

    @Column(name = "customer_id")
    public int customerId;

    @Column(name = "entrepreneur_id")
    public int entrepreneurId;
}
