package com.kintoh.watcher;

import com.kintoh.domain.Watcher;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractK8sWatcher implements Watcher, Runnable {
    protected final AtomicBoolean running = new AtomicBoolean(false);
    private static final int MAX_RETRIES = 5;
    private int currentRetries = 0;
    
    private final String threadName;

    public AbstractK8sWatcher(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void start() {
        System.out.println("Arrancando vigilante: " + threadName + "...");
        new Thread(this, threadName).start();
    }

    @Override
    public void stop() {
        running.set(false);
    }

    public void run() {
        running.set(true);
        System.out.println("Vigilancia Activa en " + threadName + ". Esperando Eventos...");

        while (running.get()) {
            try {
                performWatch();
            } catch (Exception e) {
                if (running.get()) {
                    System.err.println("Error de conexión en " + threadName + ": " + e.getMessage());
                }
            }

            if (running.get()) {
                if (currentRetries >= MAX_RETRIES) {
                    System.err.println("FATAL: Límite de reintentos alcanzado en " + threadName + ". Apagando.");
                    stop();
                } else {
                    applyBackoff();
                    currentRetries++;
                }
            }
        }
    }

    protected abstract void performWatch() throws Exception;

    protected void resetRetries() {
        this.currentRetries = 0;
    }

    private void applyBackoff() {
        try {
            System.out.println("Reintentando conexión para " + threadName + " en 5 segundos...");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            stop();
        }
    }
}