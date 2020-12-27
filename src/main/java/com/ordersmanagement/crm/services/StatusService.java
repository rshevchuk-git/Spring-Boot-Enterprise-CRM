package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.orders.StatusRepository;
import com.ordersmanagement.crm.models.entities.StatusEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;

    public List<StatusEntity> getAllStatuses() {
        return statusRepository.findAll();
    }

    public Optional<StatusEntity> getStatusById(Integer statusId) {
        return statusRepository.findById(statusId);
    }
}
