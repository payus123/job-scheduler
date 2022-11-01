package com.blusalt.dbxpbackgroundservice.constants;

public interface ResponseMessage {
    String SUCCESS_MESSAGE= "Operation Successful";
    String ERROR_MESSAGE = "Operation Failed with cause: ";
    String INVALID_SQL_QUERY = "Invalid Sql Script Syntax, Please confirm that query contains only one statement and no malicious queries";
}
