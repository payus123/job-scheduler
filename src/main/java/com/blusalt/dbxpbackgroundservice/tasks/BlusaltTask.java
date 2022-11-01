package com.blusalt.dbxpbackgroundservice.tasks;

import com.blusalt.commons.exceptions.DbxpApplicationException;
import com.blusalt.dbxpbackgroundservice.dto.Frequency;
import com.blusalt.dbxpbackgroundservice.models.BluTaskConfig;
import com.blusalt.dbxpbackgroundservice.models.TaskHistory;
import com.blusalt.dbxpbackgroundservice.models.enums.Status;
import com.blusalt.dbxpbackgroundservice.repository.TaskConfigRepository;
import com.blusalt.dbxpbackgroundservice.repository.TaskHistoryRepository;
import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Null;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

import static com.blusalt.dbxpbackgroundservice.constants.TaskHistoryStatus.failed;
import static com.blusalt.dbxpbackgroundservice.models.enums.FrequencyType.ONCE;
import static com.blusalt.dbxpbackgroundservice.models.enums.Status.ACTIVE;
import static com.blusalt.dbxpbackgroundservice.models.enums.Status.INACTIVE;
import static com.blusalt.dbxpbackgroundservice.service.Impl.BluTaskServiceImpl.getNextRunTimeByCron;
import static com.blusalt.dbxpbackgroundservice.util.DateUtil.*;
import static com.blusalt.dbxpbackgroundservice.util.EncryptionUtil.decrypt;
import static com.blusalt.dbxpbackgroundservice.util.EncryptionUtil.encrypt;

@Data
@Slf4j
public abstract class BlusaltTask extends TimerTask {
    private Status status;
    private String taskParams;
    private Long taskId;
    private String taskCron;
    private Date lastUpdated;
    private Frequency frequency;
    private Timer previousTimer;
    private String secret;


    protected Function<Null, BlusaltTask> getSameTask;

    protected TaskConfigRepository taskConfigRepository;
    protected TaskHistoryRepository taskHistoryRepository;

    private TaskHistory taskHistory = new TaskHistory();
    private Timer timer = new Timer();
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    public BlusaltTask(TaskConfigRepository repository, TaskHistoryRepository taskHistoryRepository) {
        this.taskConfigRepository = repository;
        this.taskHistoryRepository = taskHistoryRepository;
    }

    public BlusaltTask() {
    }

    public BlusaltTask sameTask(Function getSameTask) {
        this.getSameTask = getSameTask;
        return this;
    }

    public boolean validateTaskParam() {
        return true;
    }

    abstract void runTask() throws DbxpApplicationException;

    @Override
    public void run() {
        //cancel the previous timer set below to free memory
        previousTimer.cancel();
        taskHistory.setTimeStarted(new Date());
        BluTaskConfig taskConfig = taskConfigRepository.findById(taskId).orElse(null);
        Gson gson = new Gson();
        if (taskConfigRepository.findBluTaskConfigByIdAndStatus(taskId, String.valueOf(ACTIVE)).isPresent()) {
            try {
                taskHistory.setTaskParam(encrypt(taskParams, secret));
                taskHistory.setTaskId(taskConfig);
                taskHistory.setServiceName(taskConfig.getServiceName());
                runTask();

            } catch (DbxpApplicationException ex) {
                taskHistory.setTimeFinished(new Date());
                taskHistory.setStatus(failed);
                taskHistory.setExceptionMessage(ex.getMessage());
                taskHistory.setDuration(0l);
                taskHistoryRepository.save(taskHistory);
                log.error("An error occurred: " + ex.getLocalizedMessage());
            } finally {
                try {
                    if (!(taskConfig.getLastUpdate().getTime() == lastUpdated.getTime())) {
                        taskCron = taskConfig.getTaskCron();
                        frequency = gson.fromJson(taskConfig.getFrequency(), Frequency.class);
                        taskParams = decrypt(taskConfig.getTaskParams(), secret);
                        status = Status.valueOf(taskConfig.getStatus());
                    }
                    //Because the frequency might have changed on update
                    if (!Objects.isNull(frequency)) {
                        //Check if it's a one off task
                        if (frequency.getFrequency().equals(String.valueOf(ONCE))) {
                            taskConfig.setStatus(String.valueOf(INACTIVE));
                            taskConfigRepository.save(taskConfig);
                            return;
                        }
                        Date runDate = getNextRunDate(frequency);
                        frequency = getNewFrequency(runDate, frequency);
                        taskConfig.setFrequency(gson.toJson(frequency));
                        taskConfigRepository.save(taskConfig);
                    }
                    BlusaltTask sameTask = getSameTask.apply(null);
                    sameTask.setTaskCron(taskCron);
                    sameTask.setTaskParams(taskParams);
                    sameTask.setTaskId(taskId);
                    sameTask.setSecret(secret);
                    sameTask.setLastUpdated(lastUpdated);
                    sameTask.setFrequency(frequency);
                    sameTask.setPreviousTimer(timer);//keep this timer, so it can be canceled later as shown above...
                    timer.schedule(sameTask, Objects.isNull(frequency) ? getNextRunTimeByCron(taskCron) : getDateByFrequency(frequency));
                } catch (Exception e) {
                    log.error("An error occurred: " + e.getLocalizedMessage());
                }

            }
        }
    }

}
