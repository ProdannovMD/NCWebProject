package com.netcracker.application.services.impl;

import com.netcracker.application.controllers.exceptions.ResourceNotFoundException;
import com.netcracker.application.model.Status;
import com.netcracker.application.repository.StatusRepository;
import com.netcracker.application.services.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class StatusServiceImpl implements StatusService {
    private final Long DEFAULT_STATUS_ID = 1L;
    private final StatusRepository statusRepository;

    @Autowired
    public StatusServiceImpl(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Override
    public Status getDefaultStatus() {
        Status defaultStatus = statusRepository.getById(DEFAULT_STATUS_ID);
        if (Objects.isNull(defaultStatus))
            throw new IllegalStateException("No default status is present");
        return defaultStatus;
    }

    @Override
    public List<Status> getAllStatuses() {
        return statusRepository.findAll();
    }

    @Override
    public Status getStatusById(Long id) {
        Status status = statusRepository.getById(id);
        if (Objects.isNull(status))
            throw new ResourceNotFoundException("Status not found");
        return status;
    }
}
