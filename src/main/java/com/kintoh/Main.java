package com.kintoh;

import com.kintoh.core.K8sClientFactory;
import com.kintoh.domain.Monitor;
import com.kintoh.domain.Notifier;
import com.kintoh.domain.Watcher;
import com.kintoh.logic.GenericAnomalyMonitor;
import com.kintoh.notifiers.ConsoleNotifier;
import com.kintoh.notifiers.SlackNotifier;
import com.kintoh.watcher.NodeWatcher;
import com.kintoh.watcher.PodWatcher;
import io.kubernetes.client.openapi.apis.CoreV1Api;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        CoreV1Api api = new K8sClientFactory().createApi();

        String slackWebHook = System.getenv("SLACK_WEBHOOK_URL");

        List<Notifier> notifiers = null;

        try {
            notifiers = List.of(
                new ConsoleNotifier(),
                new SlackNotifier(slackWebHook)
            );
        } catch (IllegalArgumentException e) {
            System.err.println("FATAL: No se pudo arrancar el sistema. " + e.getMessage());
            System.exit(1);
        }

        Monitor monitorUniversal = new GenericAnomalyMonitor();

        List<Watcher> watchers = List.of(
            new PodWatcher(api, monitorUniversal, notifiers),
            new NodeWatcher(api, monitorUniversal, notifiers)
        );

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nCerrando conexiones y apagando vigilantes...");
            watchers.forEach(Watcher::stop);
        }));

        System.out.println("🛡️ Kintoh: Sistema de vigilancia iniciado.");
        watchers.forEach(Watcher::start);
    }
}