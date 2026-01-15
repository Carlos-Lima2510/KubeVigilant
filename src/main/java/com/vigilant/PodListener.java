package com.vigilant;

import io.kubernetes.client.openapi.models.V1Pod;

public interface PodListener {
    void onEvent(V1Pod pod, String eventType);
}
