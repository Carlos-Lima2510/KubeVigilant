package com.kintoh.watcher;

import com.google.gson.reflect.TypeToken;
import com.kintoh.domain.Event;
import com.kintoh.domain.Monitor;
import com.kintoh.domain.Notifier;
import com.kintoh.k8s.K8sNodeResource;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.util.Watch;

import java.util.List;
import java.util.Optional;

public class NodeWatcher extends AbstractK8sWatcher {
    private final CoreV1Api api;
    private final Monitor monitor;
    private final List<Notifier> notifiers;

    public NodeWatcher(CoreV1Api api, Monitor monitor, List<Notifier> notifiers) {
        super("K8s-Node-Watcher");
        this.api = api;
        this.monitor = monitor;
        this.notifiers = notifiers;
    }

    protected void performWatch() throws Exception {
        try (Watch<V1Node> watch = Watch.createWatch(
                api.getApiClient(),
                api.listNodeCall(null, null, null, null, null, null, null, null, null, null, Boolean.TRUE, null),
                new TypeToken<Watch.Response<V1Node>>() {}.getType())) {

            resetRetries();

            for (Watch.Response<V1Node> item : watch) {
                if (!running.get()) break;

                if (item.object != null) {
                    K8sNodeResource resource = new K8sNodeResource(item.object);
                    Optional<Event> potentialEvent = monitor.check(resource);
                    potentialEvent.ifPresent(event -> {
                        for (Notifier notifier : notifiers) {
                            boolean entregado = notifier.send(event);
                            if (!entregado) {
                                System.err.println("ERROR: El notificador " + notifier.getClass().getSimpleName() + " no pudo entregar la alerta.");
                            }
                        }
                    });
                }
            }
        }
    }
}