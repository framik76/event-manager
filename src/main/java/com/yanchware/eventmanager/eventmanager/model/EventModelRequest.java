package com.yanchware.eventmanager.eventmanager.model;

import lombok.Data;

@Data
public class EventModelRequest {
    private long userId;
    private String operation;

}
