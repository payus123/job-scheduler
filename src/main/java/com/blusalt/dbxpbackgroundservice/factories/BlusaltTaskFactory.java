package com.blusalt.dbxpbackgroundservice.factories;

import com.blusalt.dbxpbackgroundservice.models.enums.TaskType;
import com.blusalt.dbxpbackgroundservice.repository.TaskConfigRepository;
import com.blusalt.dbxpbackgroundservice.repository.TaskHistoryRepository;
import com.blusalt.dbxpbackgroundservice.tasks.BlusaltTask;

public abstract class BlusaltTaskFactory {
    TaskConfigRepository taskConfigRepository;
    TaskHistoryRepository historyRepository;


    public BlusaltTaskFactory(TaskConfigRepository taskConfigRepository, TaskHistoryRepository historyRepository) {
        this.taskConfigRepository = taskConfigRepository;
        this.historyRepository = historyRepository;
    }

    public abstract TaskType supportedTaskType();

    public abstract BlusaltTask createTask();
}