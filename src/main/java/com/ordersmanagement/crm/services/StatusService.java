package com.ordersmanagement.crm.services;

import com.ordersmanagement.crm.dao.business.StatusRepository;
import com.ordersmanagement.crm.models.entities.Status;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;

    public List<Status> getAllStatuses() {
        return statusRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
}
