package taskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager{

    int nextId = 1;

    private final Map<Integer, Task> tasks     = new HashMap<>();
    private final Map<Integer, Epic>     epics     = new HashMap<>();
    private final Map<Integer, SubTask>  subTasks  = new HashMap<>();

    private HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        this.historyManager = historyManager;
    }

    @Override
    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        subTask.setId(nextId++);
        subTasks.put(subTask.getId(), subTask);

        Integer epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);

        List<Integer> subTaskIds = epic.getSubTaskIds();

        Integer subTaskId = subTask.getId();
        subTaskIds.add(subTaskId);

        updateEpicStatus(epic);
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();

        for (Task t : tasks.values()) {
            allTasks.add(t);

        }
        return allTasks;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> allEpics = new ArrayList<>();

        for (Epic t : epics.values()) {
            allEpics.add(t);

        }
        return allEpics;
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        List<SubTask> allSubTasks = new ArrayList<>();

        for (SubTask t : subTasks.values()) {
            allSubTasks.add(t);

        }
        return allSubTasks;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            historyManager.add(tasks.get(taskId));
            return tasks.get(taskId);
        }
        System.out.println("Задачи с идентификатором " + taskId + " не существует!");

        return null;
    }

    @Override
    public Epic getEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            historyManager.add(tasks.get(epicId));
            return epics.get(epicId);
        }
        System.out.println("Эпика с идентификатором " + epicId + " не существует!");
        return null;
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        if (subTasks.containsKey(subTaskId)) {
            historyManager.add(tasks.get(subTaskId));
            return subTasks.get(subTaskId);
        }
        System.out.println("Подзадачи с идентификатором " + subTaskId + " не существует!");
        return null;
    }

    @Override
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);

    }

    @Override
    public void deleteEpicById(int epicId) {
        epics.remove(epicId);
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        subTasks.remove(subTaskId);

    }


    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);

    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);

    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTask.setId(nextId++);
        subTasks.put(subTask.getId(), subTask);

        Epic epic = epics.get(subTask.getEpicId());
        updateEpicStatus(epic);
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        int statusNew = 0;
        int statusDone = 0;

        for (Integer itemId : epic.getSubTaskIds()) {
            TaskStatus anItem = subTasks.get(itemId).getStatus();
            if (anItem.equals(TaskStatus.IN_PROGRESS)) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            } else if (anItem.equals(TaskStatus.NEW)) {
                statusNew++;
            } else {
                statusDone++;
            }
        }
        if (statusDone == epic.getSubTaskIds().size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }

}
