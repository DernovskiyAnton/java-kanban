package main.java.taskmanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) {
                    continue;
                }

                Task task = manager.fromString(line);

                if (task instanceof SubTask) {
                    manager.subTasks.put(task.getId(), (SubTask) task);
                } else if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else {
                    manager.tasks.put(task.getId(), task);
                }

                if (task.getId() >= manager.nextId) {
                    manager.nextId = task.getId() + 1;
                }
            }

            for (SubTask subTask : manager.getAllSubTasks()) {
                Epic epic = manager.epics.get(subTask.getEpicId());
                if (epic != null) {
                    epic.getSubTaskIds().add(subTask.getId());
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла: " + file.getAbsolutePath(), e);
        }

        return manager;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return super.getAllSubTasks();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        super.deleteSubTaskById(subTaskId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public Task getTaskById(int taskId) {
        return super.getTaskById(taskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        return super.getEpicById(epicId);
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        return super.getSubTaskById(subTaskId);
    }

    protected void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (SubTask subTask : getAllSubTasks()) {
                writer.write(toString(subTask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + file.getAbsolutePath(), e);
        }

    }

    private String toString(Task task) {
        TaskType type;
        String epicId = "";

        if (task instanceof SubTask) {
            type = TaskType.SUBTASK;
            epicId = String.valueOf(((SubTask) task).getEpicId());
        } else if (task instanceof Epic) {
            type = TaskType.EPIC;
        } else {
            type = TaskType.TASK;
        }

        return String.format("%d,%s,%s,%s,%s,%s", task.getId(), type, task.getName(), task.getStatus(), task.getDescription(), epicId);
    }

    private Task fromString(String value) {
        String[] parts = value.split(",");

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        Task task;

        switch (type) {
            case TASK:
                task = new Task(id, name, description, status);
                task.setId(id);
                break;
            case EPIC:
                Epic epic = new Epic(id, name, description, status);
                epic.setId(id);
                epic.setStatus(status);
                task = epic;
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                SubTask subTask = new SubTask(epicId, id, name, description, status);
                subTask.setId(id);
                task = subTask;
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }

        return task;
    }
}