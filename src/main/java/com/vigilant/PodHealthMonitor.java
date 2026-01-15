package com.vigilant;

import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import java.util.List;

public class PodHealthMonitor implements PodListener {

    public void onEvent(V1Pod pod, String eventType) {
        if (pod == null || pod.getStatus() == null) {
            return;
        }

        List<V1ContainerStatus> statuses = pod.getStatus().getContainerStatuses();
        
        if (statuses != null) {
            for (V1ContainerStatus status : statuses) {
                if (isCrashLooping(status)) {
                    System.out.println("ALERTA: El pod '" + pod.getMetadata().getName() + 
                                       "' está en CrashLoopBackOff.");
                }
            }
        }
    }

    private boolean isCrashLooping(V1ContainerStatus status) {
        if (status.getState() != null && 
            status.getState().getWaiting() != null && 
            status.getState().getWaiting().getReason() != null) {
            
            String reason = status.getState().getWaiting().getReason();
            return "CrashLoopBackOff".equals(reason);
        }
        return false;
    }
}