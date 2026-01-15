package com.vigilant;

import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Watch;
import com.google.gson.reflect.TypeToken;

public class EventWatcher {
    private final CoreV1Api api;

    public EventWatcher(CoreV1Api api) {
        this.api = api;
    }

    public void start(PodListener listener) {
        System.out.println("👀 Iniciando vigilancia continua... (Pulsa Ctrl+C para salir)");
        try {
            Watch<V1Pod> watch = Watch.createWatch(
                api.getApiClient(),
                api.listPodForAllNamespacesCall(
                    null, 
                    null,
                    null,
                    null, 
                    null, 
                    null, 
                    null, 
                    null, 
                    null,
                    null, 
                    Boolean.TRUE, 
                    null
                ),
                new TypeToken<Watch.Response<V1Pod>>(){}.getType()
            );

            for (Watch.Response<V1Pod> item : watch) {
                if (item.object != null) {
                    listener.onEvent(item.object, item.type);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en el Watcher: " + e.getMessage());
            e.printStackTrace();
        }
    }
}