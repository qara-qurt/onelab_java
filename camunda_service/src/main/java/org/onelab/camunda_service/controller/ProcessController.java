package org.onelab.camunda_service.controller;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.onelab.common_lib.dto.OrderRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(ProcessController.BASE_URL)
public class ProcessController {
    public static final String BASE_URL = "api/process/start";
    public static final String CREATE_ORDER = "/create-order";

    private final RuntimeService runtimeService;

    @Autowired
    public ProcessController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping(CREATE_ORDER)
    public ResponseEntity<String> startProcess(@RequestHeader("Authorization") String token,
                                               @RequestBody OrderRequestDto orderRequest) {

        if (token == null) {
            return ResponseEntity.badRequest().body("Authorization header is missing");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("userId", orderRequest.getCustomerId());
        variables.put("dishIds", orderRequest.getDishIds());
        variables.put("bearerToken", token);


        String tmpBusinessKey = "ORDER-" + java.util.UUID.randomUUID();

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                "create_order", tmpBusinessKey, variables
        );

        return ResponseEntity.ok("Process started with ID: " + instance.getId() + " and temporary businessKey: " + tmpBusinessKey);
    }
}