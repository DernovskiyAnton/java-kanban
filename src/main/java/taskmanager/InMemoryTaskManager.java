package main.java.taskmanager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    int nextId = 1;

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();

    protected final Set<Task> prioritizedTasks = new TreeSet<>((t1, t2) -> {
        if (t1.getStartTime() == null && t2.getStartTime() == null) {
            return Integer.compare(t1.getId(), t2.getId());
        }
        if (t1.getStartTime() == null) {
            return 1;
        }
        if (t2.getStartTime() == null) {
            return -1;
        }
        int timeCompare = t1.getStartTime().compareTo(t2.getStartTime());
        if (timeCompare != 0) {
            return timeCompare;
        }
        return Integer.compare(t1.getId(), t2.getId());
    });

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
        if (!subTasks.containsKey(subTask.getId())) {
            throw new IllegalArgumentException("Subtask не найден");
        }
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        updateEpicStatus(epic);
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void updateEpicTime(Epic epic) {
        List<SubTask> epicSubTasks = epic.getSubTaskIds().stream()
                .map(subTasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (epicSubTasks.isEmpty()) {
            epic.setDuration(null);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }


        LocalDateTime epicStart = epicSubTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);


        LocalDateTime epicEnd = epicSubTasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);


        Duration totalDuration = epicSubTasks.stream()
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(epicStart);
        epic.setEndTime(epicEnd);
        epic.setDuration(totalDuration.isZero() ? null : totalDuration);
    }

    protected void updateEpicStatus(Epic epic) {
        List<Integer> subTaskIds = epic.getSubTaskIds();

        if (subTaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean hasInProgress = false;
        boolean hasNew = false;

        for (Integer itemId : subTaskIds) {
            SubTask subTask = subTasks.get(itemId);
            if (subTask == null) continue;

            TaskStatus status = subTask.getStatus();
            switch (status) {
                case IN_PROGRESS:
                    hasInProgress = true;
                    break;
                case NEW:
                    hasNew = true;
                    break;
                case DONE:
                    break;
            }
        }

        if (hasInProgress) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else if (hasNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.DONE);
        }
    }


    private void addToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }


    private void removeFromPrioritized(Task task) {
        prioritizedTasks.remove(task);
    }


    private boolean isTasksOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        if (task1.getEndTime() == null || task2.getEndTime() == null) {
            return false;
        }


        return !(task1.getEndTime().isBefore(task2.getStartTime()) ||
                task1.getEndTime().isEqual(task2.getStartTime()) ||
                task2.getEndTime().isBefore(task1.getStartTime()) ||
                task2.getEndTime().isEqual(task1.getStartTime()));
    }


    private void validateTaskTime(Task task) {
        if (task.getStartTime() == null) {
            return;
        }

        boolean hasOverlap = prioritizedTasks.stream()
                .filter(t -> t.getId() != task.getId())
                .anyMatch(t -> isTasksOverlap(task, t));

        if (hasOverlap) {
            throw new IllegalArgumentException(
                    "Задача пересекается по времени с существующей задачей"
            );
        }
    }
}