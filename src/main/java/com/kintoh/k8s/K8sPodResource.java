package com.kintoh.k8s;

import com.kintoh.domain.Resource;
import com.kintoh.domain.Anomaly;

import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1ContainerState;
import io.kubernetes.client.openapi.models.V1ContainerStatus;

import java.util.Optional;
import java.util.Map;

public class K8sPodResource implements Resource {

    private final V1Pod pod;

    public K8sPodResource(V1Pod pod) {
        this.pod = pod;
    }

    @Override
    public String name() {
        return pod.getMetadata() != null ? pod.getMetadata().getName() : "Desconocido";
    }

    @Override
    public String namespace() {
        return pod.getMetadata() != null ? pod.getMetadata().getNamespace() : "Desconocido";
    }

    @Override
    public Optional<Anomaly> getCriticalAnomaly() {
        if (pod.getStatus() == null || pod.getStatus().getContainerStatuses() == null) {
            return Optional.empty();
        }

        for (V1ContainerStatus status : pod.getStatus().getContainerStatuses()) {
            V1ContainerState state = status.getState();
            
            if (state != null && state.getWaiting() != null) {
                String reason = state.getWaiting().getReason();
                
                if ("CrashLoopBackOff".equals(reason) || 
                    "ImagePullBackOff".equals(reason) || 
                    "ErrImagePull".equals(reason) ||
                    "CreateContainerConfigError".equals(reason)) {
                    
                    return Optional.of(new Anomaly("CRÍTICO", reason,
                        Map.of("contenedor_afectado", status.getName())
                    ));
                }
            }
        }
        return Optional.empty();
    }
}
