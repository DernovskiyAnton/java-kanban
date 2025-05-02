package taskManager;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subTaskIds = new ArrayList<>();

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }
}
