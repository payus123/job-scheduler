package com.blusalt.dbxpbackgroundservice.repository;

import com.blusalt.dbxpbackgroundservice.BlusaltDbxpBackgroundServiceApplicationTests;
import com.blusalt.dbxpbackgroundservice.models.BluTaskConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BlusaltDbxpBackgroundServiceApplicationTests.class)
class TaskConfigRepositoryTest {
    @Autowired
    private TaskConfigRepository taskConfigRepository;

    @Test
    void testFindAllByStatusNotContainingIgnoreCase() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());


        BluTaskConfig bluTaskConfig = new BluTaskConfig();
        bluTaskConfig.setLastUpdate(new Date());
        bluTaskConfig.setStatus("ACTIVE");
        bluTaskConfig.setTaskCron("Task Cron");
        bluTaskConfig.setTaskParams("Task Params");
        bluTaskConfig.setTaskType("Task Type");

        BluTaskConfig bluTaskConfig1 = new BluTaskConfig();
        bluTaskConfig.setLastUpdate(new Date());
        bluTaskConfig1.setStatus("INACTIVE");
        bluTaskConfig1.setTaskCron("Task Cron");
        bluTaskConfig1.setTaskParams("Task Params");
        bluTaskConfig1.setTaskType("Task Type");
        taskConfigRepository.save(bluTaskConfig);
        taskConfigRepository.save(bluTaskConfig1);
        List<BluTaskConfig> list = taskConfigRepository.findAllByStatusNotContainingIgnoreCase("DELETE", pageable);
        assertTrue(list.size() == 2);
    }




    @Test
    void testFindBluTaskConfigByIdAndStatus() {
        BluTaskConfig bluTaskConfig = new BluTaskConfig();
        bluTaskConfig.setLastUpdate(new Date());
        bluTaskConfig.setStatus("ACTIVE");
        bluTaskConfig.setTaskCron("Task Cron");
        bluTaskConfig.setTaskParams("Task Params");
        bluTaskConfig.setTaskType("Task Type");

        bluTaskConfig = taskConfigRepository.save(bluTaskConfig);

        assertTrue(taskConfigRepository.findBluTaskConfigByIdAndStatus(bluTaskConfig.getId(), "ACTIVE").isPresent());
    }
}

