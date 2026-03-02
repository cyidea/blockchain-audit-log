package com.auditlog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditEvent {

    private String username;
    private String action;
    private String resource;
    private String timestamp;

    public AuditEvent(String username, String action, String resource) {
        this.username = username;
        this.action = action;
        this.resource = resource;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // Convert to a simple string for hashing
    public String toLogString() {
        return String.format("[%s] USER=%s ACTION=%s RESOURCE=%s",
                timestamp, username, action, resource);
    }

    @Override
    public String toString() {
        return toLogString();
    }
}