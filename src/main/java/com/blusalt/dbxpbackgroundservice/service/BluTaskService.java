package com.blusalt.dbxpbackgroundservice.service;

import com.blusalt.commons.responses.CustomResponse;
import com.blusalt.dbxpbackgroundservice.dto.*;

public interface BluTaskService {
    CustomResponse createNewTask(BluTaskConfigRequest config);

    CustomResponse getAllTasks(ViewDto viewDto);

    CustomResponse updateTask(UpdateTaskConfigRequest config);

    CustomResponse deleteTask(Long id);

    CustomResponse deactivateTask(Long id);

    CustomResponse activateTask(Long id);

    CustomResponse createApp(CreateServiceRequest request);

    CustomResponse addKnownHost(AllowedHostsRequest request);
}
