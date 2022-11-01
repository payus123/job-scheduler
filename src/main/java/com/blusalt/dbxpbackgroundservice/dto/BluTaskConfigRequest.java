package com.blusalt.dbxpbackgroundservice.dto;

import com.blusalt.dbxpbackgroundservice.annotations.ValidateEnum;
import com.blusalt.dbxpbackgroundservice.models.enums.Status;
import com.blusalt.dbxpbackgroundservice.models.enums.TaskType;
import com.blusalt.dbxpbackgroundservice.util.validators.OneNotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@OneNotNull(
        fields = {"taskCron", "frequency"},
        message = "Either taskCron or frequency must be set"
)
public class BluTaskConfigRequest {
    @NotBlank(message = "please taskType is required")
    @ValidateEnum(enumClass = TaskType.class)
    private String taskType;
    @ValidateEnum(enumClass = Status.class, message = "must be DELETED,ACTIVE ,INACTIVE")
    @NotBlank(message = "please provide Status")
    private String status;
    @NotBlank(message = "please taskParams is required")
    private String taskParams;
    private String taskCron;
    private Frequency frequency;
    @NotBlank(message = "please serviceName is required")
    private String serviceName;

}
