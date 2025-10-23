package main.java.taskmanager.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import main.java.taskmanager.SubTask;
import main.java.taskmanager.TaskManager;
import main.java.taskmanager.exception.NotFoundException;
import main.java.taskmanager.exception.TaskValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, pathParts);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, pathParts);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (TaskValidationException e) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            List<SubTask> subtasks = taskManager.getAllSubTasks();
            String response = gson.toJson(subtasks);
            sendText(exchange, response);
        } else if (pathParts.length == 3) {
            int subtaskId = Integer.parseInt(pathParts[2]);
            SubTask subtask = taskManager.getSubTaskById(subtaskId);
            String response = gson.toJson(subtask);
            sendText(exchange, response);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        try {
            SubTask subtask = gson.fromJson(body, SubTask.class);

            if (subtask.getId() == 0) {
                taskManager.addSubTask(subtask);
                String response = gson.toJson(subtask);
                exchange.sendResponseHeaders(201, response.getBytes(StandardCharsets.UTF_8).length);
                exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
                exchange.close();
            } else {
                taskManager.updateSubTask(subtask);
                String response = gson.toJson(subtask);
                exchange.sendResponseHeaders(201, response.getBytes(StandardCharsets.UTF_8).length);
                exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
                exchange.close();
            }
        } catch (JsonSyntaxException e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) {
            int subtaskId = Integer.parseInt(pathParts[2]);
            taskManager.deleteSubTaskById(subtaskId);
            sendText(exchange, "Подзадача удалена");
        } else {
            sendNotFound(exchange);
        }
    }
}
