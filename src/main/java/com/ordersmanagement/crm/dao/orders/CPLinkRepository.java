package com.ordersmanagement.crm.dao.orders;

import com.ordersmanagement.crm.models.entities.CPLinkEntity;
import com.ordersmanagement.crm.models.keys.CPLinkKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CPLinkRepository extends JpaRepository<CPLinkEntity, CPLinkKey> {
}
