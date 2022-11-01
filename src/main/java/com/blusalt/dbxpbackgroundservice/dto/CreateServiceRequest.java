package com.blusalt.dbxpbackgroundservice.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateServiceRequest {
    @NotBlank(message = "please provide serviceName")
    private String serviceName;
}
