package com.blusalt.dbxpbackgroundservice.tasks.params;

import com.blusalt.dbxpbackgroundservice.annotations.ValidateEnum;
import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.HashMap;

@Data
public class ApiTaskParam {
    private String url;
    private HashMap<String, String> headers;
    private HashMap<String, String> params;
    private Object requestBody;
    @ValidateEnum(enumClass = HttpMethod.class)
    private String method;
}
