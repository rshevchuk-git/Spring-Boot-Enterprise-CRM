package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.StatusRepository;
import com.ordersmanagement.crm.models.entities.StatusEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;

    public List<StatusEntity> getAllStatuses() {
        return statusRepository.findAll();
    }
}
