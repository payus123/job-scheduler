package com.blusalt.dbxpbackgroundservice.tasks;

import com.blusalt.commons.exceptions.DbxpApplicationException;
import com.blusalt.dbxpbackgroundservice.repository.TaskConfigRepository;
import com.blusalt.dbxpbackgroundservice.repository.TaskHistoryRepository;
import com.blusalt.dbxpbackgroundservice.tasks.params.DbTaskParam;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Date;

import static com.blusalt.dbxpbackgroundservice.constants.TaskHistoryStatus.completed;
import static com.blusalt.dbxpbackgroundservice.util.validators.SqlQueryMetaDataValidation.checkIfDbTaskParamIsNull;
import static com.blusalt.dbxpbackgroundservice.util.validators.SqlQueryMetaDataValidation.isValid;

public class DbTask extends BlusaltTask {

    Gson g = new Gson();

    public DbTask() {
    }


    @Autowired
    public DbTask(TaskConfigRepository repository, TaskHistoryRepository historyRepository) {
        super(repository, historyRepository);
    }

    @Override
    public boolean validateTaskParam() {
        DbTaskParam param = g.fromJson(getTaskParams(), DbTaskParam.class);
        if (checkIfDbTaskParamIsNull(param)) {
            return isValid(param.getSqlQueryMeta());
        }
        return false;
    }

    @Override
    void runTask() throws DbxpApplicationException {
        try {
            DbTaskParam param = g.fromJson(getTaskParams(), DbTaskParam.class);
            DriverManagerDataSource source = new DriverManagerDataSource();
            source.setDriverClassName(param.getDriverClassName());
            source.setUrl(param.getConnectionUrl());
            source.setUsername(param.getUsername());
            source.setPassword(param.getPassword());
            DataSource dataSource = source;
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.execute(param.getSqlQueryMeta().getQuery());
            getTaskHistory().setTimeFinished(new Date());
            getTaskHistory().setStatus(completed);
            Long timeTaken = getTaskHistory().getTimeFinished().getTime() - getTaskHistory().getTimeStarted().getTime();
            getTaskHistory().setDuration(timeTaken);
            taskHistoryRepository.save(getTaskHistory());
        } catch (Exception e) {
            throw new DbxpApplicationException(e.getMessage());
        }
    }
}
