package com.kintoh.k8s;

import com.kintoh.domain.Resource;
import com.kintoh.domain.Anomaly;

import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeCondition;

import java.util.Optional;
import java.util.Map;

public class K8sNodeResource implements Resource {
    private final V1Node node;

    public K8sNodeResource(V1Node node) {
        this.node = node;
    }

    public String name() {
        return node.getMetadata() != null ? node.getMetadata().getName() : "Desconocido";
    }

    public String namespace() {
        return "cluster-scope";
    }

    public Optional<Anomaly> getCriticalAnomaly() {
        if (node.getStatus() == null || node.getStatus().getConditions() == null) {
            return Optional.empty();
        }

        for (V1NodeCondition condition : node.getStatus().getConditions()) {
            if ("Ready".equals(condition.getType())) {
                String status = condition.getStatus();
                if ("False".equals(status) || "Unknown".equals(status)) {
                    return Optional.of(new Anomaly(
                        "NodeNotReady",
                        Map.of(
                            "estado_actual", status,
                            "razon_k8s", condition.getReason() != null ? condition.getReason() : "N/A"
                        )
                    ));
                }
            }
        }
        return Optional.empty();
    }
}
