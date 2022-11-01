package com.blusalt.dbxpbackgroundservice.tasks.params;

import com.blusalt.dbxpbackgroundservice.models.enums.SqlMetaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlQueryMeta {
    private String query;
    private SqlMetaType sqlMetaType;

}