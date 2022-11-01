package com.blusalt.dbxpbackgroundservice.util.validators;

import com.blusalt.dbxpbackgroundservice.models.enums.SqlMetaType;
import com.blusalt.dbxpbackgroundservice.tasks.params.DbTaskParam;
import com.blusalt.dbxpbackgroundservice.tasks.params.SqlQueryMeta;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SqlQueryMetaDataValidation implements ConstraintValidator<ValidSqlQueryMeta, SqlQueryMeta> {

    @Override
    public void initialize(ValidSqlQueryMeta constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(SqlQueryMeta value, ConstraintValidatorContext context) {
        return isValid(value);
    }

    public static boolean isValid(SqlQueryMeta value) {
        SqlMetaType type = value.getSqlMetaType();
        switch (type) {
            case RAW_QUERY:
                if (validateScript(value.getQuery())) {
                    return true;
                }
            case SQL_FUNCTION:
                if (validateScript(value.getQuery())) {
                    return true;
                }
            case SQL_PROCEDURE:
                if (validateScript(value.getQuery())) {
                    return true;
                }
        }
        return false;

    }

    public static int getOccurrence(String s) {
        Matcher m = Pattern.compile("(?=(;))").matcher(s);
        List<Integer> pos = new ArrayList<Integer>();
        while (m.find()) {
            pos.add(m.start());
        }
        return pos.size();
    }

    public static boolean validateScript(String s) {
        s.toLowerCase();
        List<String> keyWords = new ArrayList<>();
        keyWords.add("create ");
        keyWords.add("drop ");
        keyWords.add("alter ");
        boolean match = keyWords.stream().anyMatch(s::contains);
        if (getOccurrence(s) < 2 && match == false) {
            return true;
        }
        return false;

    }

    public static boolean checkIfDbTaskParamIsNull(DbTaskParam param) {
        return Stream.of(param.getConnectionUrl()
                , param.getSqlQueryMeta()
                , param.getPassword()
                , param.getUsername()
                , param.getDriverClassName()).noneMatch(Objects::isNull);

    }


}
