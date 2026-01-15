package com.vigilant;

import io.kubernetes.client.openapi.apis.CoreV1Api;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Iniciando Vigilante de K8s ---");

        CoreV1Api api = K8sConnection.connect();
        if (api == null) return;

        EventWatcher watcher = new EventWatcher(api);
        PodHealthMonitor monitor = new PodHealthMonitor();

        watcher.start(monitor);
    }
}