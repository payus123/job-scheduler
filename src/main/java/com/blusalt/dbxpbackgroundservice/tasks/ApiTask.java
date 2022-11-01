package com.blusalt.dbxpbackgroundservice.tasks;

import com.blusalt.commons.exceptions.DbxpApplicationException;
import com.blusalt.dbxpbackgroundservice.repository.TaskConfigRepository;
import com.blusalt.dbxpbackgroundservice.repository.TaskHistoryRepository;
import com.blusalt.dbxpbackgroundservice.tasks.params.ApiTaskParam;
import com.blusalt.dbxpbackgroundservice.util.HTTPHelper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import static com.blusalt.dbxpbackgroundservice.constants.TaskHistoryStatus.completed;
import static com.blusalt.dbxpbackgroundservice.util.HTTPHelper.createHeaders;
import static javax.ws.rs.HttpMethod.GET;

@Slf4j
public class ApiTask extends BlusaltTask {
    @Autowired
    RestTemplate restTemplate = new RestTemplate();

    public ApiTask() {
    }


    public ApiTask(TaskConfigRepository repository, TaskHistoryRepository historyRepository) {
        super(repository, historyRepository);
    }

    @Override
    public boolean validateTaskParam() {
        return true;

    }

    @Override
    void runTask() throws DbxpApplicationException {
        try {
            Gson g = new Gson();
            HTTPHelper helper = new HTTPHelper(restTemplate);
            ApiTaskParam param = g.fromJson(getTaskParams(), ApiTaskParam.class);
            Object response = null;

            if (param.getMethod().equalsIgnoreCase(GET)) {
                response = helper.getRequest(param.getUrl(), Object.class, createHeaders(param.getHeaders()), param.getParams());
                log.info(response.toString());
                getTaskHistory().setTimeFinished(new Date());
                getTaskHistory().setStatus(completed);
                Long timeTaken = getTaskHistory().getTimeFinished().getTime() - getTaskHistory().getTimeStarted().getTime();
                getTaskHistory().setDuration(timeTaken);
                taskHistoryRepository.save(getTaskHistory());
            } else {
                response = helper.postRequest(param.getUrl(), param.getParams(), Object.class, createHeaders(param.getHeaders()), param.getMethod());
                getTaskHistory().setTimeFinished(new Date());
                getTaskHistory().setTimeFinished(new Date());
                getTaskHistory().setStatus(completed);
                Long timeTaken = getTaskHistory().getTimeFinished().getTime() - getTaskHistory().getTimeStarted().getTime();
                getTaskHistory().setDuration(timeTaken);
                taskHistoryRepository.save(getTaskHistory());
                log.info(response.toString());
            }
        } catch (Exception e) {
            throw new DbxpApplicationException(e.getMessage());
        }

    }
}
