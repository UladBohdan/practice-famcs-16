package by.bsu.up.chat.logging.impl;

import by.bsu.up.chat.client.Client;
import by.bsu.up.chat.logging.Logger;
import by.bsu.up.chat.server.ServerHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Log implements Logger {

    private static final String TEMPLATE = "[%s] %s";
    private static final String SERVER_LOG_FILE_NAME = "serverlog.txt";
    private static final String CLIENT_LOG_FILE_NAME = "clientlog.txt";
    private static final String DEFAULT_LOG_FILE_NAME = "launcherlog.txt";
    private String className;

    private String tag;

    private Log(Class<?> cls) {
        tag = String.format(TEMPLATE, cls.getName(), "%s");
        if (cls.getSimpleName().equals(ServerHandler.class.getSimpleName()))
            className = SERVER_LOG_FILE_NAME;
        else if (cls.getSimpleName().equals(Client.class.getSimpleName()))
            className = CLIENT_LOG_FILE_NAME;
        else
            className = DEFAULT_LOG_FILE_NAME;
    }

    @Override
    public void info(String message) {
        System.out.println(String.format(tag, message));
        logFile(message);
    }

    @Override
    public void error(String message, Throwable e) {
        System.err.println(String.format(tag, message));
        e.printStackTrace(System.err);
        logFile(message);
    }

    private void logFile(String logMessage) {
        try(FileWriter fileLogging = new FileWriter(className, true)) {
            fileLogging.write(LocalDateTime.now() + " " + logMessage + "\n");
        } catch (IOException exc) {
            System.out.println("Logging failed");
        }
    }

    public static Log create(Class<?> cls) {
        return new Log(cls);
    }
}
