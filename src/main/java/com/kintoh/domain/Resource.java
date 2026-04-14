package com.kintoh.domain;

import java.util.Optional;

public interface Resource {
    String name();
    String namespace();

    Optional<Anomaly> getCriticalAnomaly();
}
