package com.blusalt.dbxpbackgroundservice.models.enums;

import com.blusalt.dbxpbackgroundservice.models.AllowedHosts;
import lombok.Data;

import java.util.HashMap;

@Data
public class KnownHosts {
    public static HashMap<String, AllowedHosts> remoteHosts = new HashMap<>();
}
