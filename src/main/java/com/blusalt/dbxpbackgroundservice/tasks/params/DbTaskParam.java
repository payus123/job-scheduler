package com.blusalt.dbxpbackgroundservice.tasks.params;

import com.blusalt.dbxpbackgroundservice.util.validators.ValidSqlQueryMeta;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DbTaskParam {
    private String connectionUrl;
    private String username;
    private String password;
    @ValidSqlQueryMeta
    private SqlQueryMeta sqlQueryMeta;
    @NotBlank(message = "must provide driver class name")
    private String driverClassName;


}
