package com.blusalt.dbxpbackgroundservice.dto;

import com.blusalt.dbxpbackgroundservice.annotations.ValidateEnum;
import com.blusalt.dbxpbackgroundservice.models.enums.TaskType;
import com.blusalt.dbxpbackgroundservice.util.validators.OneNotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@NoArgsConstructor
@OneNotNull(
        fields = {"taskCron", "frequency"},
        message = "Either taskCron or frequency must be set"
)
public class UpdateTaskConfigRequest {
    private Long id;
    @NotBlank(message = "please taskType is required")
    @ValidateEnum(enumClass = TaskType.class)
    private String taskType;
    @NotBlank(message = "please taskParams is required")
    private String taskParams;
    private String taskCron;
    private Frequency frequency;
    @NotBlank(message = "please appName is required")
    private String serviceName;

}
