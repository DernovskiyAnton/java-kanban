package taskManager;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private static final int ARRAY_SIZE = 10;
    private final List<Task> watchedTasks = new ArrayList<>(ARRAY_SIZE);

    @Override
    public List<Task> getHistory() {
        return watchedTasks;
    }
    @Override
    public void add(Task task) {
        if (watchedTasks.size() >= ARRAY_SIZE) {
            watchedTasks.remove(0);
            watchedTasks.add(task);
        } else {
            watchedTasks.add(task);
        }
    }
}