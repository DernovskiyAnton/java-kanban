package main.java.taskmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import main.java.taskmanager.adapter.DurationAdapter;
import main.java.taskmanager.adapter.LocalDateTimeAdapter;
import main.java.taskmanager.handler.TaskHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;


public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer httpServer;
    private TaskManager taskManager;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();


    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new TaskHandler(taskManager, gson));
        httpServer.createContext("/history", new TaskHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new TaskHandler(taskManager, gson));
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    public static void main(String[] args) throws IOException {
        try {
            TaskManager manager = Managers.getDefault();
            HttpTaskServer taskServer = new HttpTaskServer(manager);
            taskServer.start();
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
        }
    }
}
