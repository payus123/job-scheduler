package com.blusalt.dbxpbackgroundservice.controllers;

import com.blusalt.commons.responses.CustomResponse;
import com.blusalt.dbxpbackgroundservice.dto.*;
import com.blusalt.dbxpbackgroundservice.service.BluTaskService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static com.blusalt.dbxpbackgroundservice.constants.Api.TASK_CONFIG_ENDPOINT;
import static com.blusalt.dbxpbackgroundservice.constants.ResponseCode.SUCCESS;

@RestController
@RequestMapping(TASK_CONFIG_ENDPOINT)
@RequiredArgsConstructor
@EnableTransactionManagement
public class BluTaskConfigController {
    private final BluTaskService bluTaskService;


    @PostMapping("createTask")
    @Operation
    public ResponseEntity<CustomResponse> createTask(@RequestBody @Validated BluTaskConfigRequest request) {
        CustomResponse response = null;
        response = bluTaskService.createNewTask(request);

        if (Objects.equals(response.getCode(), SUCCESS)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @Operation
    @GetMapping("getTasks")
    public ResponseEntity<CustomResponse> getTasks(@ModelAttribute("viewDto") ViewDto viewDto) {
        CustomResponse response = null;
        response = bluTaskService.getAllTasks(viewDto);

        if (Objects.equals(response.getCode(), SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PatchMapping("updateTask")
    public ResponseEntity<CustomResponse> updateTask(@RequestBody @Validated UpdateTaskConfigRequest request) {
        CustomResponse response = null;
        response = bluTaskService.updateTask(request);

        if (Objects.equals(response.getCode(), SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @PostMapping("deleteTask")
    public ResponseEntity<CustomResponse> deleteTask(@RequestParam(name = "id") Long id) {
        CustomResponse response = null;
        response = bluTaskService.deleteTask(id);

        if (Objects.equals(response.getCode(), SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("deactivateTask")
    public ResponseEntity<CustomResponse> deactivateTask(@RequestParam(name = "id") Long id) {
        CustomResponse response = null;
        response = bluTaskService.deactivateTask(id);

        if (Objects.equals(response.getCode(), SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("activateTask")
    public ResponseEntity<CustomResponse> activateTask(@RequestParam(name = "id") Long id) {


        CustomResponse response = null;
        response = bluTaskService.activateTask(id);

        if (Objects.equals(response.getCode(), SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("createApp")
    public ResponseEntity<CustomResponse> createApp(@RequestBody CreateServiceRequest request, HttpServletRequest httpServletRequest) {
        String requestURI = httpServletRequest.getRequestURI();
        CustomResponse response = null;
        response = bluTaskService.createApp(request);

        if (Objects.equals(response.getCode(), SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("addKnownHost")
    public ResponseEntity<CustomResponse> addKnownHost(@RequestBody @Validated AllowedHostsRequest request) {
        CustomResponse response = null;
        response = bluTaskService.addKnownHost(request);

        if (Objects.equals(response.getCode(), SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
