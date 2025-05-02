package taskManager;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubTasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    Task getTaskById(int taskId);

    Epic getEpicById(int epicId);

    SubTask getSubTaskById(int subTaskId);

    //f) удаление по идентификатору
    void deleteTaskById(int taskId);

    void deleteEpicById(int epicId);

    void deleteSubTaskById(int subTaskId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);
}
