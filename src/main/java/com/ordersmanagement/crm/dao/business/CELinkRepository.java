package com.ordersmanagement.crm.dao.business;

import com.ordersmanagement.crm.models.keys.CEKey;
import com.ordersmanagement.crm.models.entities.CELink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CELinkRepository extends JpaRepository<CELink, CEKey> {
}
