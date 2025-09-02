package taskmanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager{

    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();

    private Node head;
    private Node tail;

    //private static final int ARRAY_SIZE = 10;
    //private final List<Task> watchedTasks = new ArrayList<>(ARRAY_SIZE);

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

    @Override
    public void remove(int id){

    }
}