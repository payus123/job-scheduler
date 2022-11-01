package com.blusalt.dbxpbackgroundservice.dto;

import com.blusalt.dbxpbackgroundservice.annotations.ValidateEnum;
import com.blusalt.dbxpbackgroundservice.models.enums.FrequencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Frequency {
    private Integer startTimeHour;
    private Integer startTimeMinutes;
    @ValidateEnum(enumClass = FrequencyType.class, message = "must be one of  DAILY, WEEKLY, BI_WEEKLY, MONTHLY, QUARTERLY, YEARLY")
    private String frequency;
    private Integer month;
    private Integer year;
    private Integer dayOfMonth;


}
