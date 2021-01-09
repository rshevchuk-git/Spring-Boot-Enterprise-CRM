package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.CELinkRepository;
import com.ordersmanagement.crm.dao.orders.CPLinkRepository;
import com.ordersmanagement.crm.models.entities.CELinkEntity;
import com.ordersmanagement.crm.models.entities.CPLinkEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LinkService {

    private final CELinkRepository ceLinkRepository;
    private final CPLinkRepository cpLinkRepository;

    public List<CELinkEntity> getAllCELinks() {
        return ceLinkRepository.findAll();
    }
    public List<CPLinkEntity> getAllCPLinks() {
        return cpLinkRepository.findAll();
    }

    public CELinkEntity saveCELink(CELinkEntity ceLink) {
        return ceLinkRepository.save(ceLink);
    }
    public CPLinkEntity saveCPLink(CPLinkEntity cpLink) {
        return cpLinkRepository.save(cpLink);
    }
}
