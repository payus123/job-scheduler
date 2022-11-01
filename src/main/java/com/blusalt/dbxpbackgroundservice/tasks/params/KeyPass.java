package com.blusalt.dbxpbackgroundservice.tasks.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeyPass {
    private String authMode;
    private String key;
}
