package com.vigilant;

import io.kubernetes.client.openapi.apis.CoreV1Api;

public class Main {
    public static void main(String[] args) {
        CoreV1Api api = new K8sClientFactory().createApi();
        PodHealthMonitor monitor = new PodHealthMonitor();
        new EventWatcher(api, monitor).start();
    }
}