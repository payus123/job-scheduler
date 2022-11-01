package com.blusalt.dbxpbackgroundservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ViewDto {
    private int pageSize;
    private int pageNo;
    private String name;

}
