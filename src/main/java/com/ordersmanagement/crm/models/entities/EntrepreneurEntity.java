package com.ordersmanagement.crm.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "entrepreneurs")
public class EntrepreneurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entrepreneur_id")
    private int entrepreneurId;

    @Column(name = "full_name")
    private String fullName;
}

