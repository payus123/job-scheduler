package com.blusalt.dbxpbackgroundservice.service.Impl;

import com.blusalt.dbxpbackgroundservice.dto.BluTaskConfigRequest;
import com.blusalt.dbxpbackgroundservice.dto.UpdateTaskConfigRequest;
import com.blusalt.dbxpbackgroundservice.dto.ViewDto;
import com.blusalt.dbxpbackgroundservice.factories.BlusaltTaskFactory;
import com.blusalt.dbxpbackgroundservice.models.ApplicationKey;
import com.blusalt.dbxpbackgroundservice.models.BluTaskConfig;
import com.blusalt.dbxpbackgroundservice.models.enums.TaskType;
import com.blusalt.dbxpbackgroundservice.repository.AllowedHostsRepository;
import com.blusalt.dbxpbackgroundservice.repository.ApplicationKeysRepository;
import com.blusalt.dbxpbackgroundservice.repository.TaskConfigRepository;
import com.blusalt.dbxpbackgroundservice.tasks.ApiTask;
import com.blusalt.dbxpbackgroundservice.tasks.BlusaltTask;
import com.blusalt.dbxpbackgroundservice.tasks.DbTask;
import com.blusalt.dbxpbackgroundservice.util.EncryptionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static com.blusalt.dbxpbackgroundservice.models.enums.Status.ACTIVE;
import static com.blusalt.dbxpbackgroundservice.models.enums.Status.INACTIVE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class BluTaskServiceImplTest {


    @Spy
    @InjectMocks
    BluTaskServiceImpl bluTaskService;
    @MockBean
    private TaskConfigRepository taskConfigRepository;
    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private Map<TaskType, BlusaltTaskFactory> blusaltTaskFactoryMap;

    @MockBean
    private ApplicationKeysRepository applicationKeysRepository;
    @MockBean
    private AllowedHostsRepository allowedHostsRepository;
    MockedStatic<EncryptionUtil> mockedStatics;


    @BeforeEach
    public void setUp() {
        bluTaskService = new BluTaskServiceImpl(taskConfigRepository, modelMapper, blusaltTaskFactoryMap, applicationKeysRepository, allowedHostsRepository);
        MockitoAnnotations.initMocks(this);
        mockedStatics = Mockito.mockStatic(EncryptionUtil.class);

    }

    @AfterEach
    public void tearDown() {
        mockedStatics.close();
    }

    @Test
    public void createNewTask() {
        //given
        BluTaskConfigRequest request = new BluTaskConfigRequest("DBTASK", "ACTIVE",
                "Params", "*/30    *    *    *    *    *", null, "test");
        BluTaskConfig taskConfig = new BluTaskConfig();
        taskConfig.setTaskType(request.getTaskType());
        taskConfig.setStatus(String.valueOf(ACTIVE));
        taskConfig.setLastUpdate(new Date());
        taskConfig.setId(1L);

        BlusaltTask task = new ApiTask();
        task.setTaskId(1l);
        task.setStatus(INACTIVE);
        task.setTaskParams("Test");
        ApplicationKey key = new ApplicationKey();
        key.setSecreteKey("4tZRSTygdQm4ldPTXm8N0VEy7cSDaCN1n1x5jXNNa5HWkmVtPsZWkFDfqN9GHaM3");

        Optional<ApplicationKey> applicationKey = Optional.of(key);


        //when

        mockedStatics.when(() -> EncryptionUtil.decryptSecureSecret(any(String.class)))
                .thenReturn("akskajkadkakakaiiiikaka");

        Mockito.doReturn(task).when(bluTaskService).getTaskType(any(TaskType.class));
        Mockito.doReturn(applicationKey).when(applicationKeysRepository).findApplicationKeysByServiceName(any(String.class));
        Mockito.doReturn(taskConfig).when(modelMapper).map(any(), any());
        Mockito.doReturn(taskConfig).when(taskConfigRepository).save(any(BluTaskConfig.class));
        Mockito.doNothing().when(bluTaskService).addNewTask(any(Timer.class), any(TimerTask.class), any(Date.class));

        bluTaskService.createNewTask(request);
        verify(taskConfigRepository).save(taskConfig);





    }

    @Test
    void getTaskType() {
    }

    @Test
    void getAllTasks() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());

        //when
        ViewDto build = ViewDto.builder().pageNo(0).pageSize(5).build();
        bluTaskService.getAllTasks(build);
        //then
        verify(taskConfigRepository).findAllByStatusNotContainingIgnoreCase("DELETED", pageable);
    }

    @Test
    void updateTask() {
        //Given
        UpdateTaskConfigRequest request = new UpdateTaskConfigRequest(1L, "API_TASK"
                , "TEST", "*****", null, "test");

        ApplicationKey key = new ApplicationKey();
        key.setSecreteKey("4tZRSTygdQm4ldPTXm8N0VEy7cSDaCN1n1x5jXNNa5HWkmVtPsZWkFDfqN9GHaM3");

        Optional<ApplicationKey> applicationKey = Optional.of(key);


        BluTaskConfig bluTaskConfig = new BluTaskConfig();
        bluTaskConfig.setLastUpdate(new Date());
        bluTaskConfig.setTaskCron(request.getTaskCron());
        bluTaskConfig.setTaskParams(request.getTaskParams());
        bluTaskConfig.setTaskType(request.getTaskType());
        bluTaskConfig.setId(request.getId());
        bluTaskConfig.setServiceName("test");
        //when
        mockedStatics.when(() -> EncryptionUtil.decryptSecureSecret(any(String.class)))
                .thenReturn("akskajkadkakakaiiiikaka");

        when(taskConfigRepository.findById(1L)).thenReturn(Optional.of(bluTaskConfig));
        Mockito.doReturn(applicationKey).when(applicationKeysRepository).findApplicationKeysByServiceName(any(String.class));
        Mockito.doReturn(new ApiTask()).when(bluTaskService).getTaskType(any(TaskType.class));


        bluTaskService.updateTask(request);
        //then
        verify(taskConfigRepository).save(bluTaskConfig);


    }

    @Test
    void activateTask() {
        //Given
        BluTaskConfig bluTaskConfig = new BluTaskConfig();
        bluTaskConfig.setId(1l);
        bluTaskConfig.setLastUpdate(new Date());
        bluTaskConfig.setStatus("INACTIVE");
        bluTaskConfig.setTaskCron("*/30    *    *    *    *    *");
        bluTaskConfig.setTaskParams("TEST");
        bluTaskConfig.setTaskType(String.valueOf(TaskType.API_TASK));
        bluTaskConfig.setServiceName("test");
        BlusaltTask task = new DbTask();
        task.setTaskId(1l);
        task.setStatus(INACTIVE);
        task.setTaskParams("Test");

        ApplicationKey key = new ApplicationKey();
        key.setSecreteKey("4tZRSTygdQm4ldPTXm8N0VEy7cSDaCN1n1x5jXNNa5HWkmVtPsZWkFDfqN9GHaM3");
        Optional<ApplicationKey> applicationKey = Optional.of(key);

        //when
        mockedStatics.when(() -> EncryptionUtil.decrypt(any(String.class), any(String.class))).thenReturn("TEST");
        mockedStatics.when(() -> EncryptionUtil.decryptSecureSecret(any(String.class)))
                .thenReturn("akskajkadkakakaiiiikaka");
        Mockito.doReturn(task).when(bluTaskService).getTaskType(any(TaskType.class));
        Mockito.doReturn(applicationKey).when(applicationKeysRepository).findApplicationKeysByServiceName(any(String.class));
        Mockito.doReturn(Optional.of(bluTaskConfig)).when(taskConfigRepository).findById(1L);
        Mockito.doNothing().when(bluTaskService).addNewTask(any(Timer.class), any(TimerTask.class), any(Date.class));

        bluTaskService.activateTask(1L);
        verify(taskConfigRepository).save(bluTaskConfig);
    }

    @Test
    void deleteTask() {
        BluTaskConfig bluTaskConfig = new BluTaskConfig();
        bluTaskConfig.setLastUpdate(new Date());
        bluTaskConfig.setStatus("DELETE");
        bluTaskConfig.setTaskCron("Task Cron");
        bluTaskConfig.setTaskParams("Task Params");
        bluTaskConfig.setTaskType(String.valueOf(TaskType.DBTASK));
        Mockito.doReturn(Optional.of(bluTaskConfig)).when(taskConfigRepository).findById(1L);
        //  when(taskConfigRepository.findById(1L)).thenReturn(Optional.of(bluTaskConfig));
        bluTaskService.deactivateTask(1L);
        verify(taskConfigRepository).save(bluTaskConfig);

    }

    @Test
    void deactivateTask() {
        //given
        BluTaskConfig bluTaskConfig = new BluTaskConfig();
        bluTaskConfig.setLastUpdate(new Date());
        bluTaskConfig.setStatus("INACTIVE");
        bluTaskConfig.setTaskCron("Task Cron");
        bluTaskConfig.setTaskParams("Task Params");
        bluTaskConfig.setTaskType(String.valueOf(TaskType.API_TASK));
        when(taskConfigRepository.findById(1L)).thenReturn(Optional.of(bluTaskConfig));
        bluTaskService.deactivateTask(1L);
        verify(taskConfigRepository).save(bluTaskConfig);

    }


    @Test
    void getNextRunTimeByCron() {
        //given
        String cron = "*/30    *    *    *    *    *";

        Long d= new Date().getTime()+(30000);
        Assertions.assertTrue(bluTaskService.getNextRunTimeByCron(cron).getTime()-d<10);


    }
}