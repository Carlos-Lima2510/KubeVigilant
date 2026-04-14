package com.kintoh.notifiers;

import com.kintoh.domain.Event;
import com.kintoh.domain.Notifier;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class ConsoleNotifier implements Notifier {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean send(Event event) {
        try {
            String detallesFormateados = event.details().entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining(" | "));

            System.out.println("\n--------------------------------------------------");
            System.out.println(" [" + event.severity() + "] DETECCIÓN DE ANOMALÍA ");
            System.out.println("    * Hora:     " + event.timestamp().format(FORMATTER));
            System.out.println("    * Recurso:  " + event.resource().name());
            System.out.println("    * Ámbito:   " + event.resource().namespace());
            System.out.println("    * Motivo:   " + event.reason());
            System.out.println("    * Detalles: " + detallesFormateados);
            System.out.println("--------------------------------------------------");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}