package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.EntrepreneurRepository;
import com.ordersmanagement.crm.models.entities.EntrepreneurEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EntrepreneurService {

    private final EntrepreneurRepository entrepreneurRepository;

    public List<EntrepreneurEntity> getAllEntrepreneurs() {
        return entrepreneurRepository.findAll();
    }
}
