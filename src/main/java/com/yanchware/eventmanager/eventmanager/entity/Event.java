package com.yanchware.eventmanager.eventmanager.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Data
@Table("events")
public class Event {
    @Id
    private long id;
    private long userId;
    private String type;
    private Timestamp timestamp;
    private String operation;
}
