package com.kintoh;

import com.kintoh.core.K8sClientFactory;
import com.kintoh.domain.Notifier;
import com.kintoh.domain.Watcher;
import com.kintoh.logic.NodeMonitor;
import com.kintoh.logic.PodCrashMonitor;
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


        List<Notifier> notifiers = List.of(
            new ConsoleNotifier(),
            new SlackNotifier(slackWebHook));

        List<Watcher> watchers = List.of(
            new PodWatcher(api, new PodCrashMonitor(), notifiers),
            new NodeWatcher(api, new NodeMonitor(), notifiers)
        );

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Apagando todos los vigilantes...");
            watchers.forEach(Watcher::stop);
        }));

        watchers.forEach(Watcher::start);
    }
}