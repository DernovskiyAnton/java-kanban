package main.java.taskmanager;

public class Main {
    public static void main(String[] args) {


        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task(0, "Купить гараж", "Купить гараж в Новосибирске", TaskStatus.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task(0, "Повесить полку", "Повесить полку в гостиной", TaskStatus.NEW);
        taskManager.addTask(task2);

        Epic epic1 = new Epic(0, "Построить дом", "Построить дом в пригороде Новосибирска", TaskStatus.NEW);
        taskManager.addEpic(epic1);

        SubTask subTask1 = new SubTask(3, 0, "Найти риэлтора", "Нужен хороший риэлтор", TaskStatus.NEW);
        taskManager.addSubTask(subTask1);

    }
}
