package com.blusalt.dbxpbackgroundservice.dto;

import com.blusalt.dbxpbackgroundservice.models.BluTaskConfig;
import lombok.Data;

import java.util.List;

@Data
public class GetAllTaskResponse {
    private List<BluTaskConfig> bluTaskConfigs;
}
