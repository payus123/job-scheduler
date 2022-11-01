package com.blusalt.dbxpbackgroundservice.factories;

import com.blusalt.dbxpbackgroundservice.models.enums.TaskType;
import com.blusalt.dbxpbackgroundservice.repository.TaskConfigRepository;
import com.blusalt.dbxpbackgroundservice.repository.TaskHistoryRepository;
import com.blusalt.dbxpbackgroundservice.tasks.BlusaltTask;
import com.blusalt.dbxpbackgroundservice.tasks.ShellTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlusaltShellTaskFactory extends BlusaltTaskFactory {

    @Autowired
    public BlusaltShellTaskFactory(TaskConfigRepository taskConfigRepository, TaskHistoryRepository historyRepository) {
        super(taskConfigRepository, historyRepository);
    }

    @Override
    public TaskType supportedTaskType() {
        return TaskType.SHELL_TASK;
    }

    @Override
    public BlusaltTask createTask() {
        BlusaltTask task = new ShellTask(taskConfigRepository, historyRepository);
        return task.sameTask((i)-> createTask());
    }
}
