package main.java.taskmanager.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.java.taskmanager.Task;
import main.java.taskmanager.TaskManager;
import main.java.taskmanager.handler.BaseHttpHandler;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equals(method)) {
                List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String response = gson.toJson(prioritizedTasks);
                sendText(exchange, response);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
        }
    }
}
