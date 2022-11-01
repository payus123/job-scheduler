package com.blusalt.dbxpbackgroundservice.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AllowedHostsRequest {
    @NotBlank(message = "please provide IpAddress")
    private String ipAddress;
    @NotBlank(message = "please provide serviceName")
    private String serviceName;

}
