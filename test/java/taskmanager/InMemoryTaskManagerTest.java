package java.taskmanager;

import main.java.taskmanager.Epic;
import main.java.taskmanager.InMemoryTaskManager;
import main.java.taskmanager.SubTask;
import main.java.taskmanager.Task;
import main.java.taskmanager.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    @DisplayName("Добавление задачи должно присвоить ID и сохранить в коллекции")
    void addTask_ShouldAssignIdAndStore() {
        Task task = new Task("Тестовая задача", "Описание задачи");
        task.setStatus(TaskStatus.NEW);

        taskManager.addTask(task);

        assertEquals(1, task.getId());
        assertEquals(1, taskManager.getAllTasks().size());
        assertTrue(taskManager.getAllTasks().contains(task));
    }

    @Test
    @DisplayName("Добавление эпика должно присвоить ID и сохранить в коллекции")
    void addEpic_ShouldAssignIdAndStore() {
        Epic epic = new Epic(0, "Тестовый эпик", "Описание эпика", TaskStatus.NEW);

        taskManager.addEpic(epic);

        assertEquals(1, epic.getId());
        assertEquals(1, taskManager.getAllEpics().size());
        assertTrue(taskManager.getAllEpics().contains(epic));
    }

    @Test
    @DisplayName("Добавление подзадачи должно присвоить ID, сохранить и обновить эпик")
    void addSubTask_ShouldAssignIdStoreAndUpdateEpic() {
        Epic epic = new Epic(0, "Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask(epic.getId(), 0, "Подзадача", "Описание подзадачи", TaskStatus.NEW);

        taskManager.addSubTask(subTask);

        assertEquals(2, subTask.getId());
        assertEquals(1, taskManager.getAllSubTasks().size());
        assertTrue(taskManager.getAllSubTasks().contains(subTask));

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertTrue(updatedEpic.getSubTaskIds().contains(subTask.getId()));
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus());
    }

    @Test
    @DisplayName("ID должны присваиваться последовательно")
    void idAssignment_ShouldBeSequential() {
        Task task1 = new Task("Задача 1", "Описание 1");
        task1.setStatus(TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание 2");
        task2.setStatus(TaskStatus.NEW);
        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика", TaskStatus.NEW);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);

        assertEquals(1, task1.getId());
        assertEquals(2, task2.getId());
        assertEquals(3, epic1.getId());
    }

    @Test
    @DisplayName("Получение всех задач должно возвращать все добавленные задачи")
    void getAllTasks_ShouldReturnAllTasks() {
        Task task1 = new Task("Задача 1", "Описание 1");
        task1.setStatus(TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание 2");
        task2.setStatus(TaskStatus.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        List<Task> allTasks = taskManager.getAllTasks();

        assertEquals(2, allTasks.size());
        assertTrue(allTasks.contains(task1));
        assertTrue(allTasks.contains(task2));
    }

    @Test
    @DisplayName("Получение всех эпиков должно возвращать все добавленные эпики")
    void getAllEpics_ShouldReturnAllEpics() {
        Epic epic1 = new Epic(0, "Эпик 1", "Описание 1", TaskStatus.NEW);
        Epic epic2 = new Epic(0, "Эпик 2", "Описание 2", TaskStatus.NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        List<Epic> allEpics = taskManager.getAllEpics();

        assertEquals(2, allEpics.size());
        assertTrue(allEpics.contains(epic1));
        assertTrue(allEpics.contains(epic2));
    }

    @Test
    @DisplayName("Получение всех подзадач должно возвращать все добавленные подзадачи")
    void getAllSubTasks_ShouldReturnAllSubTasks() {
        Epic epic = new Epic(0, "Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask(epic.getId(), 0, "Подзадача 1", "Описание 1", TaskStatus.NEW);
        SubTask subTask2 = new SubTask(epic.getId(), 0, "Подзадача 2", "Описание 2", TaskStatus.NEW);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        List<SubTask> allSubTasks = taskManager.getAllSubTasks();

        assertEquals(2, allSubTasks.size());
        assertTrue(allSubTasks.contains(subTask1));
        assertTrue(allSubTasks.contains(subTask2));
    }

    @Test
    @DisplayName("Удаление всех задач должно очистить коллекцию")
    void deleteAllTasks_ShouldClearTasksCollection() {
        Task task1 = new Task("Задача 1", "Описание 1");
        task1.setStatus(TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание 2");
        task2.setStatus(TaskStatus.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.deleteAllTasks();

        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    @DisplayName("Удаление всех эпиков должно очистить коллекцию")
    void deleteAllEpics_ShouldClearEpicsCollection() {
        Epic epic1 = new Epic(0, "Эпик 1", "Описание 1", TaskStatus.NEW);
        Epic epic2 = new Epic(0, "Эпик 2", "Описание 2", TaskStatus.NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    @DisplayName("Удаление всех подзадач должно очистить коллекцию")
    void deleteAllSubTasks_ShouldClearSubTasksCollection() {
        Epic epic = new Epic(0, "Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask(epic.getId(), 0, "Подзадача 1", "Описание 1", TaskStatus.NEW);
        SubTask subTask2 = new SubTask(epic.getId(), 0, "Подзадача 2", "Описание 2", TaskStatus.NEW);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.deleteAllSubTasks();

        assertEquals(0, taskManager.getAllSubTasks().size());
    }

    @Test
    @DisplayName("Получение задачи по существующему ID должно возвращать задачу и добавлять в историю")
    void getTaskById_ExistingId_ShouldReturnTaskAndAddToHistory() {
        Task task = new Task("Задача", "Описание");
        task.setStatus(TaskStatus.NEW);
        taskManager.addTask(task);
        int taskId = task.getId();

        Task retrievedTask = taskManager.getTaskById(taskId);

        assertNotNull(retrievedTask);
        assertEquals(task, retrievedTask);
        assertEquals("Задача", retrievedTask.getName());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.contains(task));
    }

    @Test
    @DisplayName("Получение задачи по несуществующему ID должно возвращать null и выводить сообщение")
    void getTaskById_NonExistingId_ShouldReturnNullAndPrintMessage() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            Task result = taskManager.getTaskById(999);

            assertNull(result);
            String output = outputStream.toString();
            assertTrue(output.contains("Задачи с идентификатором 999 не существует!"));

            assertEquals(0, taskManager.getHistory().size());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Получение эпика по существующему ID должно возвращать эпик")
    void getEpicById_ExistingId_ShouldReturnEpic() {
        Epic epic = new Epic(0, "Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        Epic retrievedEpic = taskManager.getEpicById(epicId);

        assertNotNull(retrievedEpic);
        assertEquals(epic, retrievedEpic);
        assertEquals("Эпик", retrievedEpic.getName());
    }

    @Test
    @DisplayName("Получение эпика по несуществующему ID должно возвращать null")
    void getEpicById_NonExistingId_ShouldReturnNull() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            Epic result = taskManager.getEpicById(999);

            assertNull(result);
            String output = outputStream.toString();
            assertTrue(output.contains("Эпика с идентификатором 999 не существует!"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Получение подзадачи по существующему ID должно возвращать подзадачу")
    void getSubTaskById_ExistingId_ShouldReturnSubTask() {
        Epic epic = new Epic(0, "Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask(epic.getId(), 0, "Подзадача", "Описание подзадачи", TaskStatus.NEW);
        taskManager.addSubTask(subTask);

        SubTask retrievedSubTask = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(retrievedSubTask);
        assertEquals(subTask, retrievedSubTask);
        assertEquals("Подзадача", retrievedSubTask.getName());
        assertEquals(epic.getId(), retrievedSubTask.getEpicId());
    }

    @Test
    @DisplayName("Получение подзадачи по несуществующему ID должно возвращать null")
    void getSubTaskById_NonExistingId_ShouldReturnNull() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            SubTask result = taskManager.getSubTaskById(999);

            assertNull(result);
            String output = outputStream.toString();
            assertTrue(output.contains("Подзадачи с идентификатором 999 не существует!"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Удаление задачи по ID должно удалить задачу из коллекции")
    void deleteTaskById_ShouldRemoveTask() {
        Task task1 = new Task("Задача 1", "Описание 1");
        task1.setStatus(TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание 2");
        task2.setStatus(TaskStatus.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.deleteTaskById(task1.getId());

        assertEquals(1, taskManager.getAllTasks().size());
        assertFalse(taskManager.getAllTasks().contains(task1));
        assertTrue(taskManager.getAllTasks().contains(task2));
        assertNull(taskManager.getTaskById(task1.getId()));
    }

    @Test
    @DisplayName("Удаление эпика по ID должно удалить эпик из коллекции")
    void deleteEpicById_ShouldRemoveEpic() {
        Epic epic1 = new Epic(0, "Эпик 1", "Описание 1", TaskStatus.NEW);
        Epic epic2 = new Epic(0, "Эпик 2", "Описание 2", TaskStatus.NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        taskManager.deleteEpicById(epic1.getId());

        assertEquals(1, taskManager.getAllEpics().size());
        assertFalse(taskManager.getAllEpics().contains(epic1));
        assertTrue(taskManager.getAllEpics().contains(epic2));
    }

    @Test
    @DisplayName("Удаление подзадачи по ID должно удалить подзадачу из коллекции")
    void deleteSubTaskById_ShouldRemoveSubTask() {
        Epic epic = new Epic(0, "Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask(epic.getId(), 0, "Подзадача 1", "Описание 1", TaskStatus.NEW);
        SubTask subTask2 = new SubTask(epic.getId(), 0, "Подзадача 2", "Описание 2", TaskStatus.NEW);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.deleteSubTaskById(subTask1.getId());

        assertEquals(1, taskManager.getAllSubTasks().size());
        assertFalse(taskManager.getAllSubTasks().contains(subTask1));
        assertTrue(taskManager.getAllSubTasks().contains(subTask2));

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(2, updatedEpic.getSubTaskIds().size());
    }

    @Test
    @DisplayName("Обновление задачи должно заменить существующую задачу")
    void updateTask_ShouldReplaceExistingTask() {
        Task originalTask = new Task("Оригинальная задача", "Оригинальное описание");
        originalTask.setStatus(TaskStatus.NEW);
        taskManager.addTask(originalTask);

        Task updatedTask = new Task(originalTask.getId(), "Обновленная задача", "Обновленное описание", TaskStatus.IN_PROGRESS);

        taskManager.updateTask(updatedTask);

        Task retrievedTask = taskManager.getTaskById(originalTask.getId());
        assertEquals("Обновленная задача", retrievedTask.getName());
        assertEquals("Обновленное описание", retrievedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, retrievedTask.getStatus());
        assertEquals(1, taskManager.getAllTasks().size());
    }

    @Test
    @DisplayName("Обновление эпика должно заменить существующий эпик и обновить статус")
    void updateEpic_ShouldReplaceExistingEpicAndUpdateStatus() {
        Epic originalEpic = new Epic(0, "Оригинальный эпик", "Оригинальное описание", TaskStatus.NEW);
        taskManager.addEpic(originalEpic);

        Epic updatedEpic = new Epic(originalEpic.getId(), "Обновленный эпик", "Обновленное описание", TaskStatus.NEW);

        taskManager.updateEpic(updatedEpic);

        Epic retrievedEpic = taskManager.getEpicById(originalEpic.getId());
        assertEquals("Обновленный эпик", retrievedEpic.getName());
        assertEquals("Обновленное описание", retrievedEpic.getDescription());
        assertEquals(TaskStatus.NEW, retrievedEpic.getStatus());
    }

    @Test
    @DisplayName("Обновление несуществующей подзадачи должно выбросить исключение")
    void updateSubTask_NonExistingSubTask_ShouldThrowException() {
        SubTask nonExistingSubTask = new SubTask(1, 999, "Несуществующая", "Описание", TaskStatus.NEW);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.updateSubTask(nonExistingSubTask)
        );
        assertEquals("Subtask не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Обновление подзадачи должно обновить подзадачу и статус эпика")
    void updateSubTask_ShouldUpdateSubTaskAndEpicStatus() {
        Epic epic = new Epic(0, "Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask(epic.getId(), 0, "Подзадача", "Описание", TaskStatus.NEW);
        taskManager.addSubTask(subTask);
    }
}