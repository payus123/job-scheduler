package com.blusalt.dbxpbackgroundservice.controllers;

import com.blusalt.commons.responses.CustomResponse;
import com.blusalt.dbxpbackgroundservice.dto.ViewDto;
import com.blusalt.dbxpbackgroundservice.service.Impl.TaskHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.blusalt.dbxpbackgroundservice.constants.Api.TASK_HISTORY_ENDPOINT;

@RestController
@RequestMapping(TASK_HISTORY_ENDPOINT)
public class TaskHistoryController {
    private final TaskHistoryService taskHistoryService;

    public TaskHistoryController(TaskHistoryService taskHistoryService) {
        this.taskHistoryService = taskHistoryService;
    }

    @GetMapping("getTaskHistory")
    public ResponseEntity<CustomResponse> getTasks(@ModelAttribute("viewDto") ViewDto viewDto, @RequestParam(name = "taskId") Long taskId) {
        CustomResponse response = null;
        response = taskHistoryService.getHistoryByTaskId(viewDto, taskId);
        if (Objects.equals(response.getCode(), HttpStatus.OK.value())) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
