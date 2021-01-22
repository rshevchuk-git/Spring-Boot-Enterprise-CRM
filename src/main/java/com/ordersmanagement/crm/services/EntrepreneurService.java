package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.business.EntrepreneurRepository;
import com.ordersmanagement.crm.models.entities.Entrepreneur;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EntrepreneurService {

    private final EntrepreneurRepository entrepreneurRepository;

    public List<Entrepreneur> getAllEntrepreneurs() {
        return entrepreneurRepository.findAll();
    }
}
