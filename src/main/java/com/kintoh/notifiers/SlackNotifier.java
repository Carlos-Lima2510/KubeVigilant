package com.kintoh.notifiers;

import com.kintoh.domain.Event;
import com.kintoh.domain.Notifier;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class SlackNotifier implements Notifier {
    private final String webHookUrl;
    private final HttpClient httpClient;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SlackNotifier(String webHookUrl) {
        if (webHookUrl == null || webHookUrl.isBlank()) {
            throw new IllegalArgumentException("Webhook URL no puede ser nula");
        }
        this.webHookUrl = webHookUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public boolean send(Event event) {
        try {
            String detalles = event.details().entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining(", "));
                
            String infoLine = event.reason() + (detalles.isEmpty() ? "" : " | " + detalles);

            String jsonPayload = """
                {
                    "text": "🚨 *[%s] DETECCIÓN DE ANOMALÍA*\\n• *Hora:* %s\\n• *Recurso:* %s\\n• *Ámbito:* %s\\n• *Info:* %s"
                }
                """.formatted(
                    event.severity(),
                    event.timestamp().format(FORMATTER),
                    event.resourceName(),
                    event.resourceNamespace(),
                    infoLine
                );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webHookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 || response.statusCode() == 201;

        } catch (Exception e) {
            return false; 
        }
    }
}