package com.blusalt.dbxpbackgroundservice.service.Impl;

import com.blusalt.commons.exceptions.DbxpApplicationException;
import com.blusalt.commons.responses.CustomResponse;
import com.blusalt.dbxpbackgroundservice.dto.ViewDto;
import com.blusalt.dbxpbackgroundservice.models.ApplicationKey;
import com.blusalt.dbxpbackgroundservice.models.BluTaskConfig;
import com.blusalt.dbxpbackgroundservice.models.TaskHistory;
import com.blusalt.dbxpbackgroundservice.repository.ApplicationKeysRepository;
import com.blusalt.dbxpbackgroundservice.repository.TaskConfigRepository;
import com.blusalt.dbxpbackgroundservice.repository.TaskHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.blusalt.commons.helpers.GenericResponseCustomizer.generic200Response;
import static com.blusalt.commons.helpers.GenericResponseCustomizer.genericErrorResponse;
import static com.blusalt.dbxpbackgroundservice.constants.ResponseMessage.ERROR_MESSAGE;
import static com.blusalt.dbxpbackgroundservice.constants.ResponseMessage.SUCCESS_MESSAGE;
import static com.blusalt.dbxpbackgroundservice.util.EncryptionUtil.decrypt;
import static com.blusalt.dbxpbackgroundservice.util.EncryptionUtil.decryptSecureSecret;

@Service
@Slf4j
public class TaskHistoryServiceImpl implements TaskHistoryService {
    private final TaskHistoryRepository historyRepository;
    private final TaskConfigRepository taskConfigRepository;
    private final ApplicationKeysRepository applicationKeysRepository;

    public TaskHistoryServiceImpl(TaskHistoryRepository historyRepository, TaskConfigRepository taskConfigRepository
            , ApplicationKeysRepository applicationKeysRepository) {
        this.historyRepository = historyRepository;
        this.taskConfigRepository = taskConfigRepository;
        this.applicationKeysRepository = applicationKeysRepository;
    }

    @Override
    public CustomResponse getHistoryByTaskId(ViewDto viewDto, Long taskId) {
        try {
            Pageable pageable = PageRequest.of(viewDto.getPageNo(), viewDto.getPageSize(), Sort.by("id").descending());
            List<ApplicationKey> applicationKeys = applicationKeysRepository.findAll();
            Map<String, ApplicationKey> applicationKeyMap = applicationKeys.stream()
                    .collect(Collectors.toMap(ApplicationKey::getServiceName, Function.identity()));

            BluTaskConfig taskConfig = taskConfigRepository.findById(taskId).orElseThrow(() -> {
                throw new DbxpApplicationException("Task not Found");
            });
            List<TaskHistory> taskHistories = historyRepository.findAllByTaskId(taskConfig, pageable).orElseThrow(() -> {
                throw new DbxpApplicationException("Task id passed does not have any runtime history found");
            });

            List<TaskHistory> responseList = new ArrayList<>();

            taskHistories.stream().forEach(x -> {
                        x.setTaskParam(decrypt(x.getTaskParam()
                                , decryptSecureSecret(applicationKeyMap.get(x.getServiceName()).getSecreteKey())));
                        responseList.add(x);
                    }
            );


            log.info("fetching all task histories with taskId {} .......", taskId);
            return generic200Response(SUCCESS_MESSAGE, responseList);
        } catch (Exception e) {
            return genericErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE + e.getLocalizedMessage());

        }
    }
}
