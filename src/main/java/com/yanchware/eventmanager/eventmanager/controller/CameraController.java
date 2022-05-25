package com.yanchware.eventmanager.eventmanager.controller;


import com.yanchware.eventmanager.eventmanager.entity.Event;
import com.yanchware.eventmanager.eventmanager.model.EventMessage;
import com.yanchware.eventmanager.eventmanager.model.EventModelRequest;
import com.yanchware.eventmanager.eventmanager.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class CameraController {

    private final EventService eventService;

    @Operation(summary = "Send camera event", description = "Send a camera event", tags = {"Camera"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "502", description = "Bad gateway")})
    @PostMapping(value = "/camera/send", produces = "application/json")
    public ResponseEntity<Void> postSendCamera(@Valid @RequestBody EventModelRequest eventModelRequest) {
        try {
            log.info("start postSendCamera with payload {} ", eventModelRequest);
            var eventMessage = new EventMessage();
            eventMessage.setOperation(eventModelRequest.getOperation());
            eventMessage.setTimestamp(System.currentTimeMillis());
            eventMessage.setType("CAMERA");
            eventMessage.setUserId(eventModelRequest.getUserId());
            eventService.sendEvent(eventMessage);
            log.info("end postSendCamera with payload {} ", eventModelRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }

    @Operation(summary = "Get user id camera events", description = "Get all user id camera events", tags = {"Camera"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "502", description = "Bad gateway")})
    @GetMapping(value = "/camera/{userId}", produces = "application/json")
    public ResponseEntity<List<Event>> getCameraEvents(@PathVariable long userId, @RequestParam(defaultValue = "CAMERA") String type) {
        try {
            log.info("start getCameraEvents for userId {} ", userId);
            var events = eventService.getEventsByUserIdAndType(userId, type);
            log.info("end getCameraEvents for userId {} ", userId);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }

}
