package com.blusalt.dbxpbackgroundservice.service.Impl;

import com.blusalt.commons.exceptions.DbxpApplicationException;
import com.blusalt.commons.responses.CustomResponse;
import com.blusalt.dbxpbackgroundservice.dto.*;
import com.blusalt.dbxpbackgroundservice.factories.BlusaltTaskFactory;
import com.blusalt.dbxpbackgroundservice.models.AllowedHosts;
import com.blusalt.dbxpbackgroundservice.models.ApplicationKey;
import com.blusalt.dbxpbackgroundservice.models.BluTaskConfig;
import com.blusalt.dbxpbackgroundservice.models.enums.KnownHosts;
import com.blusalt.dbxpbackgroundservice.models.enums.Status;
import com.blusalt.dbxpbackgroundservice.models.enums.TaskType;
import com.blusalt.dbxpbackgroundservice.repository.AllowedHostsRepository;
import com.blusalt.dbxpbackgroundservice.repository.ApplicationKeysRepository;
import com.blusalt.dbxpbackgroundservice.repository.TaskConfigRepository;
import com.blusalt.dbxpbackgroundservice.service.BluTaskService;
import com.blusalt.dbxpbackgroundservice.tasks.BlusaltTask;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.blusalt.commons.helpers.GenericResponseCustomizer.generic200Response;
import static com.blusalt.commons.helpers.GenericResponseCustomizer.genericErrorResponse;
import static com.blusalt.dbxpbackgroundservice.constants.ResponseMessage.ERROR_MESSAGE;
import static com.blusalt.dbxpbackgroundservice.constants.ResponseMessage.SUCCESS_MESSAGE;
import static com.blusalt.dbxpbackgroundservice.models.enums.Status.*;
import static com.blusalt.dbxpbackgroundservice.util.DateUtil.getNewFrequency;
import static com.blusalt.dbxpbackgroundservice.util.DateUtil.getStartDate;
import static com.blusalt.dbxpbackgroundservice.util.EncryptionUtil.*;

@Service
@NoArgsConstructor
@Slf4j
public class BluTaskServiceImpl implements BluTaskService {

    private Map<TaskType, BlusaltTaskFactory> blusaltTaskFactoryMap;
    private TaskConfigRepository taskConfigRepository;
    private ModelMapper modelMapper;
    private ApplicationKeysRepository applicationKeysRepository;
    private AllowedHostsRepository allowedHostsRepository;
    private Gson gson = new Gson();


    @Autowired
    public BluTaskServiceImpl(TaskConfigRepository taskConfigRepository
            , ModelMapper modelMapper, Map<TaskType
            , BlusaltTaskFactory> blusaltTaskFactoryMap
            , ApplicationKeysRepository applicationKeysRepository
            , AllowedHostsRepository allowedHostsRepository) {

        this.taskConfigRepository = taskConfigRepository;
        this.modelMapper = modelMapper;
        this.blusaltTaskFactoryMap = blusaltTaskFactoryMap;
        this.applicationKeysRepository = applicationKeysRepository;
        this.allowedHostsRepository = allowedHostsRepository;
    }

    @Override
    public CustomResponse createNewTask(BluTaskConfigRequest config) {
        try {
            //get secret and Decrypt it
            ApplicationKey applicationKey = applicationKeysRepository.findApplicationKeysByServiceName(config.getServiceName()).orElseThrow(() -> {
                throw new DbxpApplicationException("this serviceName is not registered");
            });
            String secret = decryptSecureSecret(applicationKey.getSecreteKey());
            //Save the Configuration to Db
            BluTaskConfig taskConfig = modelMapper.map(config, BluTaskConfig.class);
            taskConfig.setTaskType(config.getTaskType());
            taskConfig.setStatus(String.valueOf(Status.ACTIVE));
            taskConfig.setLastUpdate(new Date());
            taskConfig.setServiceName(config.getServiceName());
            taskConfig.setTaskParams(encrypt(config.getTaskParams(), secret));//encrypt params with application specific secret

            BlusaltTask task = getTaskType(TaskType.valueOf(config.getTaskType()));
            if (!task.validateTaskParam()) {
                throw new DbxpApplicationException("task param is not valid");
            }

            Date nextRunTimeByCron = null;
            if (!Objects.isNull(config.getTaskCron())) {
                nextRunTimeByCron = getNextRunTimeByCron(config.getTaskCron());
            }
            if (!Objects.isNull(config.getFrequency())) {
                config.setFrequency(getNewFrequency(getStartDate(config.getFrequency()), config.getFrequency()));
                taskConfig.setFrequency(gson.toJson(config.getFrequency()));
            }

            BluTaskConfig savedConfig = taskConfigRepository.save(taskConfig);
            log.info("saved new task to db:", savedConfig);

            //Use Saved Config to create new Task

            task.setTaskId(savedConfig.getId());
            task.setTaskCron(config.getTaskCron());
            task.setTaskParams(config.getTaskParams());
            task.setLastUpdated(taskConfig.getLastUpdate());
            task.setFrequency(config.getFrequency());
            task.setSecret(secret);

            Timer timer = new Timer();
            task.setPreviousTimer(timer);
            addNewTask(timer, task, Objects.isNull(nextRunTimeByCron) ? getStartDate(config.getFrequency()) : nextRunTimeByCron);

            log.info("task added to scheduler:");
            return generic200Response(SUCCESS_MESSAGE, savedConfig);
        } catch (Exception e) {
            throw new DbxpApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null, new ArrayList<>());

        }

    }

    public BlusaltTask getTaskType(TaskType taskType) {
        BlusaltTaskFactory factory = blusaltTaskFactoryMap.get(taskType);
        return factory.createTask();
    }


    @Override
    public CustomResponse getAllTasks(ViewDto viewDto) {
        try {
            Pageable pageable = PageRequest.of(viewDto.getPageNo(), viewDto.getPageSize(), Sort.by("id").descending());
            List<BluTaskConfig> taskConfigs = taskConfigRepository.findAllByStatusNotContainingIgnoreCase(String.valueOf(DELETED), pageable);
            List<ApplicationKey> applicationKeys = applicationKeysRepository.findAll();
            Map<String, ApplicationKey> applicationKeyMap = applicationKeys.stream()
                    .collect(Collectors.toMap(ApplicationKey::getServiceName, Function.identity()));


            List<BluTaskConfig> responseList = new ArrayList<>();

            taskConfigs.stream().forEach(x -> {
                        x.setTaskParams(decrypt(x.getTaskParams()
                                , decryptSecureSecret(applicationKeyMap.get(x.getServiceName()).getSecreteKey())));
                        responseList.add(x);
                    }
            );


            if (!taskConfigs.isEmpty()) {
                log.info("fetching all tasks.......");
                return generic200Response(SUCCESS_MESSAGE, responseList);
            }
            return genericErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE + "No Tasks to display");
        } catch (Exception e) {
            return genericErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE + e.getLocalizedMessage());
        }
    }

    @Override
    public CustomResponse updateTask(UpdateTaskConfigRequest config) {
        try {
            //get secret and Decrypt it
            ApplicationKey applicationKey = applicationKeysRepository.findApplicationKeysByServiceName(config.getServiceName()).orElseThrow(() -> {
                throw new DbxpApplicationException("this serviceName is not registered");
            });
            String secret = decryptSecureSecret(applicationKey.getSecreteKey());

            BluTaskConfig taskConfig = taskConfigRepository.findById(config.getId()).orElseThrow(() -> {
                throw new DbxpApplicationException("Task not Found");
            });

            if (!Objects.isNull(config.getFrequency()) && Objects.isNull(taskConfig.getFrequency())) {
                taskConfig.setTaskCron(null);
                taskConfig.setFrequency(gson.toJson(config.getFrequency()));
            } else {
                taskConfig.setFrequency(null);
                taskConfig.setTaskCron(config.getTaskCron());
            }

            taskConfig.setTaskType(config.getTaskType());
            taskConfig.setTaskCron(config.getTaskCron());
            taskConfig.setTaskParams(encrypt(config.getTaskParams(), secret));
            taskConfig.setLastUpdate(new Date());
            taskConfig.setServiceName(config.getServiceName());

            BlusaltTask task = getTaskType(TaskType.valueOf(config.getTaskType()));//get taskType param validator to validate params
            task.setTaskParams(config.getTaskParams());
            if (!task.validateTaskParam()) {
                throw new DbxpApplicationException("task param is not valid");
            }
            task = null; //remove unused task
            taskConfigRepository.save(taskConfig);
            return generic200Response(SUCCESS_MESSAGE, taskConfig);
        } catch (Exception e) {
            return genericErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE + e.getLocalizedMessage());
        }
    }

    @Override
    public CustomResponse activateTask(Long id) {
        try {
            BluTaskConfig taskConfig = taskConfigRepository.findById(id).orElseThrow(() -> {
                throw new DbxpApplicationException("Task not Found");
            });

            ApplicationKey applicationKey = applicationKeysRepository.findApplicationKeysByServiceName(taskConfig.getServiceName()).orElseThrow(() -> {
                throw new DbxpApplicationException("this serviceName is not registered");
            });
            String secret = decryptSecureSecret(applicationKey.getSecreteKey());


            if (taskConfig.getStatus().equalsIgnoreCase(String.valueOf(ACTIVE))) {
                return genericErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE + "Task is already Active");
            }
            Date nextRunTimeByCron = null;
            if (!Objects.isNull(taskConfig.getTaskCron())) {
                nextRunTimeByCron = getNextRunTimeByCron(taskConfig.getTaskCron());
            }
            taskConfig.setStatus(String.valueOf(ACTIVE));
            BlusaltTask task = getTaskType(TaskType.valueOf(taskConfig.getTaskType()));
            task.setTaskId(taskConfig.getId());
            task.setTaskCron(taskConfig.getTaskCron());
            task.setTaskParams(decrypt(taskConfig.getTaskParams(), secret));
            task.setStatus(ACTIVE);
            task.setLastUpdated(new Date());
            Timer timer = new Timer();
            task.setPreviousTimer(timer);
            addNewTask(timer, task, Objects.isNull(taskConfig.getFrequency()) ? nextRunTimeByCron :
                    getStartDate(gson.fromJson(taskConfig.getFrequency(), Frequency.class)));
            taskConfigRepository.save(taskConfig);
            return generic200Response(SUCCESS_MESSAGE, taskConfig);
        } catch (Exception e) {
            return genericErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE + e.getLocalizedMessage());
        }
    }

    @Override
    public CustomResponse createApp(CreateServiceRequest request) {
        try {
            ApplicationKey applicationKey = ApplicationKey.builder()
                    .serviceName(request.getServiceName())
                    .dateCreated(new Date())
                    .secreteKey(encryptSecureSecret(generateSecureKey()))
                    .build();
            applicationKeysRepository.save(applicationKey);
            return generic200Response(SUCCESS_MESSAGE, applicationKey);
        } catch (Exception e) {
            return genericErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE + e.getLocalizedMessage());

        }
    }


    @Override
    public CustomResponse addKnownHost(AllowedHostsRequest request) {
        try {
            AllowedHosts allowedHosts = AllowedHosts.builder().IpAddress(request.getIpAddress()).serviceName(request.getServiceName()).build();
            KnownHosts.remoteHosts.put(request.getIpAddress(), allowedHosts);
            allowedHostsRepository.save(allowedHosts);
            return generic200Response(SUCCESS_MESSAGE, allowedHosts);
        } catch (Exception e) {
            return genericErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE + e.getLocalizedMessage());
        }
    }


    public void scheduleExistingTasks(BluTaskConfig taskConfig) {
        try {
            ApplicationKey applicationKey = applicationKeysRepository.findApplicationKeysByServiceName(taskConfig.getServiceName()).orElseThrow(() -> {
                throw new DbxpApplicationException("this serviceName is not registered");
            });
            String secret = decryptSecureSecret(applicationKey.getSecreteKey());

            BlusaltTask task = getTaskType(TaskType.valueOf(taskConfig.getTaskType()));
            task.setTaskId(taskConfig.getId());
            task.setTaskCron(taskConfig.getTaskCron());
            task.setTaskParams(decrypt(taskConfig.getTaskParams(), secret));
            task.setLastUpdated(taskConfig.getLastUpdate());
            task.setStatus(ACTIVE);
            task.setSecret(secret);
            Date nextRunTimeByCron = null;
            if (!Objects.isNull(taskConfig.getTaskCron())) {
                nextRunTimeByCron = getNextRunTimeByCron(taskConfig.getTaskCron());
            }
            Timer timer = new Timer();
            task.setPreviousTimer(timer);
            addNewTask(timer, task, Objects.isNull(nextRunTimeByCron) ?
                    getStartDate(gson.fromJson(taskConfig.getFrequency(), Frequency.class)) : nextRunTimeByCron);
            taskConfigRepository.save(taskConfig);

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }


    }

    public CustomResponse deleteTask(Long id) {
        return getCustomResponse(id, DELETED);

    }

    @Override
    public CustomResponse deactivateTask(Long id) {
        return getCustomResponse(id, INACTIVE);
    }


    private CustomResponse getCustomResponse(Long id, Status inactive) {
        try {
            BluTaskConfig taskConfig = taskConfigRepository.findById(id).orElseThrow(() -> {
                throw new DbxpApplicationException("Task not Found");
            });
            taskConfig.setStatus(String.valueOf(inactive));
            taskConfigRepository.save(taskConfig);
            return generic200Response(SUCCESS_MESSAGE, taskConfig);
        } catch (Exception e) {
            return genericErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE + e.getLocalizedMessage());
        }
    }


    public void addNewTask(Timer timer, TimerTask task, Date time) {
        timer.schedule(task, time);

    }

    public static Date getNextRunTimeByCron(String cron) {
        try {
            CronExpression cronTrigger = CronExpression.parse(cron);
            LocalDateTime next = cronTrigger.next(LocalDateTime.now());
            return Date.from(next.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            throw new DbxpApplicationException(e.getMessage());
        }
    }

    @PostConstruct
    public void initTasks() {
        try {
            List<BluTaskConfig> configs = taskConfigRepository.findAllByStatus(String.valueOf(ACTIVE));
            if (!configs.isEmpty()) {
                configs.forEach(t -> {
                    scheduleExistingTasks(t);
                    log.info("starting existing  Task with Id:" + t.getId());
                });
            }
        } catch (Exception e) {
            log.error("An Error Occurred " + e.getLocalizedMessage());
        }

    }

    @PostConstruct
    public void initKnownHosts() {
        try {
            List<AllowedHosts> allowedHosts = allowedHostsRepository.findAll();
            if (!allowedHosts.isEmpty()) {
                allowedHosts.forEach(t -> {
                    KnownHosts.remoteHosts.put(t.getIpAddress(), t);
                    log.info("adding known host: " + t.getIpAddress());
                });
            }
        } catch (Exception e) {
            log.error("An Error Occurred " + e.getLocalizedMessage());
        }

    }


}
