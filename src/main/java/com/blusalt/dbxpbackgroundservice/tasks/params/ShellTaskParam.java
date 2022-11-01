package com.blusalt.dbxpbackgroundservice.tasks.params;

import lombok.Data;

@Data
public class ShellTaskParam {
    private String username;
    private Integer port;
    private String severIp;
    private String scriptDirectory;
    private KeyPass keyPass;

}
