package com.ordersmanagement.crm.dao.business;

import com.ordersmanagement.crm.models.entities.CPLink;
import com.ordersmanagement.crm.models.keys.CPLinkKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CPLinkRepository extends JpaRepository<CPLink, CPLinkKey> {
}
