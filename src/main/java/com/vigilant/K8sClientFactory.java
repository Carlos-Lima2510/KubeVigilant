package com.vigilant;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import java.io.IOException;

public class K8sClientFactory {

    public CoreV1Api createApi(){
        try {
            ApiClient client = Config.defaultClient();
            client.setReadTimeout(0);

            return new CoreV1Api(client);

        } catch (IOException e) {
            throw new RuntimeException("Error fatal: No se pudo conectar al clúster Kubernetes.", e);
        }
    }
}