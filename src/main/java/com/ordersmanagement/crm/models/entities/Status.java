package com.ordersmanagement.crm.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@Table(name = "status")
public class Status {

    @Id
    @Column(name = "status_id")
    private Integer id;

    @Column(name = "status_name")
    private String name;
}
