package com.kintoh.watcher;

import com.google.gson.reflect.TypeToken;
import com.kintoh.domain.Event;
import com.kintoh.domain.Monitor;
import com.kintoh.domain.Notifier;
import com.kintoh.k8s.K8sPodResource;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Watch;

import java.util.List;
import java.util.Optional;

public class PodWatcher extends AbstractK8sWatcher {
    private final CoreV1Api api;
    private final Monitor monitor;
    private final List<Notifier> notifiers;

    public PodWatcher(CoreV1Api api, Monitor monitor, List<Notifier> notifiers) {
        super("K8s-Pod-Watcher");
        this.api = api;
        this.monitor = monitor;
        this.notifiers = notifiers;
    }

    protected void performWatch() throws Exception {
        try (Watch<V1Pod> watch = Watch.createWatch(
                api.getApiClient(),
                api.listPodForAllNamespacesCall(null, null, null, null, null, null, null, null, null, null, Boolean.TRUE, null),
                new TypeToken<Watch.Response<V1Pod>>() {}.getType())) {

            resetRetries();

            for (Watch.Response<V1Pod> item : watch) {
                if (!running.get()) break;

                if (item.object != null) {
                    K8sPodResource resource = new K8sPodResource(item.object);
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