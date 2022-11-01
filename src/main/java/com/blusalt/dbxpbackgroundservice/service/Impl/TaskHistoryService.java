package com.blusalt.dbxpbackgroundservice.service.Impl;

import com.blusalt.commons.responses.CustomResponse;
import com.blusalt.dbxpbackgroundservice.dto.ViewDto;

public interface TaskHistoryService {
    CustomResponse getHistoryByTaskId(ViewDto viewDto, Long Id);
}
