package com.ordersmanagement.crm.dao.orders;

import com.ordersmanagement.crm.models.keys.CEKey;
import com.ordersmanagement.crm.models.entities.CELinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CELinkRepository extends JpaRepository<CELinkEntity, CEKey> {
}
