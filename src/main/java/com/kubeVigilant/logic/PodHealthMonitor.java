package com.kubeVigilant.logic;

import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1ContainerState;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import java.util.List;

import com.kubeVigilant.watcher.PodListener;

public class PodHealthMonitor implements PodListener {

    private static final String ERROR_REASON = "CrashLoopBackOff";

    public void onEvent(V1Pod pod, String eventType) {
        if (isPodCrashing(pod)) {
            reportCrash(pod);
        }
    }

    private boolean isPodCrashing(V1Pod pod) {
        if (pod.getStatus() == null || pod.getStatus().getContainerStatuses() == null) {
            return false;
        }

        List<V1ContainerStatus> statuses = pod.getStatus().getContainerStatuses();
        
        for (V1ContainerStatus status : statuses) {
            V1ContainerState state = status.getState();
            if (state != null 
                && state.getWaiting() != null 
                && ERROR_REASON.equals(state.getWaiting().getReason())) {
                return true;
            }
        }
        return false;
    }

    private void reportCrash(V1Pod pod) {
        String podName = pod.getMetadata().getName();
        String namespace = pod.getMetadata().getNamespace();
        
        System.out.println("\n--------------------------------------------------");
        System.out.println(" -- DETECCIÓN DE FALLO CRÍTICO -- ");
        System.out.println("    * Pod: " + podName);
        System.out.println("    * NS:  " + namespace);
        System.out.println("    * Estado: " + ERROR_REASON);
        System.out.println("--------------------------------------------------");
    }
}