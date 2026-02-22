package com.kintoh.notifiers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;

import com.kintoh.domain.Event;
import com.kintoh.domain.Notifier;

public class SlackNotifier implements Notifier {

    private final String webHookUrl;
    private final HttpClient httpClient;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SlackNotifier(String webHookUrl) {
        this.webHookUrl = webHookUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void send(Event event) {
        String time = event.timestamp().format(FORMATTER);
        String markdownText = String.format(
            "🚨 *[%s] DETECCIÓN DE ANOMALÍA* 🚨\\n• *Hora:* %s\\n• *Recurso:* %s\\n• *Ámbito:* %s\\n• *Info:* %s",
            event.severity(),
            time,
            event.resource().name(),
            event.resource().namespace(),
            event.message()
        );

        String jsonPayload = """
            {
                "text": "%s"
            }
            """.formatted(markdownText);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webHookUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() != 200 && response.statusCode() != 201) {
                        System.err.println("Advertencia: Fallo al enviar a Slack. HTTP " + response.statusCode());
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("Error de red conectando a Slack: " + ex.getMessage());
                    return null;
                });
    }
    
}
