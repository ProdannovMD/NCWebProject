package com.netcracker.application.services;

import com.netcracker.application.model.Status;

import java.util.List;

public interface StatusService {
    Status getDefaultStatus();

    List<Status> getAllStatuses();

    Status getStatusById(Long id);
}
