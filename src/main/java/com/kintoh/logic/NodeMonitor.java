package com.kintoh.logic;

import com.kintoh.domain.Event;
import com.kintoh.domain.Monitor;
import com.kintoh.domain.Resource;
import com.kintoh.k8s.K8sNodeResource;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeCondition;

import java.util.Optional;

public class NodeMonitor implements Monitor {

    public Optional<Event> check(K8sNodeResource resource) {

        V1Node node = ((K8sNodeResource) resource).rawNode();

        if (node.getStatus() == null || node.getStatus().getConditions() == null) {
            return Optional.empty();
        }

        for (V1NodeCondition condition : node.getStatus().getConditions()) {
            if ("Ready".equals(condition.getType())) {
                String status = condition.getStatus();
                
                if ("False".equals(status) || "Unknown".equals(status)) {
                    String estadoLegible = status.equals("False") ? "Caído (Not Ready)" : "Inalcanzable (Unknown)";
                    
                    String msg = "¡El Nodo principal ha fallado! Estado: " + estadoLegible + ". Razón K8s: " + condition.getReason();
                    return Optional.of(new Event("CRÍTICO", msg, resource));
                }
            }
        }
        return Optional.empty();
    }
}