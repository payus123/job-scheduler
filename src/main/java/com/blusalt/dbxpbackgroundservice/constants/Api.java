package com.blusalt.dbxpbackgroundservice.constants;

import static com.blusalt.commons.config.Routes.ADMIN_SECURED;

public interface Api {
    String TASK_CONFIG_ENDPOINT = ADMIN_SECURED + "/task-config";
    String TASK_HISTORY_ENDPOINT = ADMIN_SECURED + "/task-history";
}
