package com.blusalt.dbxpbackgroundservice.config;

import com.blusalt.dbxpbackgroundservice.factories.BlusaltTaskFactory;
import com.blusalt.dbxpbackgroundservice.models.enums.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class BeanConfig {

    @Autowired
    private List<BlusaltTaskFactory> blusaltTaskFactoryList;


    @Bean("blusaltTaskFactoryMap")
    public Map<TaskType, BlusaltTaskFactory> blusaltTaskFactoryMap() {
        return blusaltTaskFactoryList.stream().collect(Collectors.toMap((i) -> i.supportedTaskType(), (i) -> i));
    }


}
