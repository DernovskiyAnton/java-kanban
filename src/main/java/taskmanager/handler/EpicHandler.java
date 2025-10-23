package main.java.taskmanager.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import main.java.taskmanager.Epic;
import main.java.taskmanager.TaskManager;
import main.java.taskmanager.exception.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
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
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            List<Epic> epics = taskManager.getAllEpics();
            String response = gson.toJson(epics);
            sendText(exchange, response);
        } else if (pathParts.length == 3) {
            int epicId = Integer.parseInt(pathParts[2]);
            Epic epic = taskManager.getEpicById(epicId);
            String response = gson.toJson(epic);
            sendText(exchange, response);
        } else if (pathParts.length == 4 && "subtasks".equals(pathParts[3])) {
            int epicId = Integer.parseInt(pathParts[2]);
            Epic epic = taskManager.getEpicById(epicId);
            List<Integer> subtaskIds = epic.getSubTaskIds();
            String response = gson.toJson(subtaskIds);
            sendText(exchange, response);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        try {
            Epic epic = gson.fromJson(body, Epic.class);

            if (epic.getId() == 0) {
                taskManager.addEpic(epic);
                String response = gson.toJson(epic);
                exchange.sendResponseHeaders(201, response.getBytes(StandardCharsets.UTF_8).length);
                exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
                exchange.close();
            } else {
                taskManager.updateEpic(epic);
                String response = gson.toJson(epic);
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
            int epicId = Integer.parseInt(pathParts[2]);
            taskManager.deleteEpicById(epicId);
            sendText(exchange, "Эпик удален");
        } else {
            sendNotFound(exchange);
        }
    }
}
