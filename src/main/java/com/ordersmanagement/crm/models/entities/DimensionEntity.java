package com.ordersmanagement.crm.models.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "kind_dimensions")
public class DimensionEntity {

    @Id
    @Column(name = "kind_id")
    private Integer orderKindEntity;

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;
}
