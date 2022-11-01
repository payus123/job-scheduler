package com.blusalt.dbxpbackgroundservice.factories;

import com.blusalt.dbxpbackgroundservice.models.enums.TaskType;
import com.blusalt.dbxpbackgroundservice.repository.TaskConfigRepository;
import com.blusalt.dbxpbackgroundservice.repository.TaskHistoryRepository;
import com.blusalt.dbxpbackgroundservice.tasks.ApiTask;
import com.blusalt.dbxpbackgroundservice.tasks.BlusaltTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlusaltApiTaskFactory extends BlusaltTaskFactory{

    @Autowired
    public BlusaltApiTaskFactory(TaskConfigRepository taskConfigRepository, TaskHistoryRepository historyRepository) {
        super(taskConfigRepository, historyRepository);
    }

    @Override
    public TaskType supportedTaskType() {
        return TaskType.API_TASK;
    }

    @Override
    public BlusaltTask createTask() {
        BlusaltTask task = new ApiTask(taskConfigRepository, historyRepository);
        return task.sameTask((i)-> createTask());
    }
}
