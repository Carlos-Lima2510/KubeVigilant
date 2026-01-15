package com.vigilant;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import java.io.IOException;

public class K8sConnection {
    public static CoreV1Api connect() {
        System.out.println("-> Intentando conectar con Maven...");
        try {
            ApiClient client = Config.defaultClient();
            client.setReadTimeout(0);
            return new CoreV1Api(client);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}