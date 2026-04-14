package com.kintoh.domain;

import java.time.LocalDateTime;
import java.util.Map;

public class Event {
    private final LocalDateTime timestamp;
    private final String severity;
    private final String reason;
    private final Map<String, String> details;
    private final Resource resource;

    public Event(String severity, Anomaly anomaly, Resource resource) {
        this.timestamp = LocalDateTime.now();
        this.severity = severity;
        this.reason = anomaly.reason();
        this.details = anomaly.details();
        this.resource = resource;
    }

    public LocalDateTime timestamp() { return timestamp; }
    public String severity() { return severity; }
    public String reason() { return reason; }
    public Map<String, String> details() { return details; }
    public Resource resource() { return resource; }
}