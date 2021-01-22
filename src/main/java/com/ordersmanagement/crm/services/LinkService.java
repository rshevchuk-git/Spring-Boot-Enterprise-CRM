package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.business.CELinkRepository;
import com.ordersmanagement.crm.dao.business.CPLinkRepository;
import com.ordersmanagement.crm.models.entities.CELink;
import com.ordersmanagement.crm.models.entities.CPLink;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LinkService {

    private final CELinkRepository ceLinkRepository;
    private final CPLinkRepository cpLinkRepository;

    public List<CELink> getAllCELinks() {
        return ceLinkRepository.findAll();
    }

    public List<CPLink> getAllCPLinks() {
        return cpLinkRepository.findAll();
    }

    public CELink saveCELink(CELink ceLink) {
        return ceLinkRepository.save(ceLink);
    }

    public CPLink saveCPLink(CPLink cpLink) {
        return cpLinkRepository.save(cpLink);
    }
}
