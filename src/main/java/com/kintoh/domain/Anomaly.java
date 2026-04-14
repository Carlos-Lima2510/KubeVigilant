package com.kintoh.domain;

import java.util.Map;
import java.util.Optional;

public record Anomaly(String severity, String reason, Map<String, String> details) {}