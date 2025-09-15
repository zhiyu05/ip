package clementine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clementine.task.Deadline;
import clementine.task.Event;
import clementine.task.Task;
import clementine.task.Todo;
public class TaskListTest {
    // used claude to generate more test cases by feeding it my tasklist class
    private TaskList taskList;
    private Task testTask1;
    private Task testTask2;

    @BeforeEach
    public void setUp() throws ClementineException {
        taskList = new TaskList();
        testTask1 = new Todo("finish 2100 quiz");
        testTask2 = new Todo("complete assignment");
    }

    // Constructor tests
    @Test
    public void constructor_emptyList() {
        TaskList emptyTaskList = new TaskList();
        assertTrue(emptyTaskList.isEmpty());
        assertEquals(0, emptyTaskList.taskSize());
    }

    @Test
    public void constructor_withExistingTasks() throws ClementineException {
        ArrayList<Task> existingTasks = new ArrayList<>();
        existingTasks.add(testTask1);
        existingTasks.add(testTask2);

        TaskList taskListWithTasks = new TaskList(existingTasks);
        assertEquals(2, taskListWithTasks.taskSize());
        assertFalse(taskListWithTasks.isEmpty());
    }

    // Additional add task tests
    @Test
    public void addTask_multipleTasks_success() throws ClementineException {
        taskList.addTask(testTask1);
        taskList.addTask(testTask2);
        assertEquals(2, taskList.taskSize());
    }

    @Test
    public void addTask_maxCapacity_throwsException() throws ClementineException {
        // Add 100 tasks (max capacity)
        for (int i = 0; i < 100; i++) {
            taskList.addTask(new Todo("task " + i));
        }
        assertEquals(100, taskList.taskSize());

        // Adding 101st task should throw exception
        assertThrows(ClementineException.class, () -> {
            taskList.addTask(new Todo("overflow task"));
        });
    }

    // Additional get task tests
    @Test
    public void getTask_validIndex_returnsTask() throws ClementineException {
        taskList.addTask(testTask1);
        Task retrieved = taskList.getTask(1);
        assertEquals(testTask1, retrieved);
        assertEquals("finish 2100 quiz", retrieved.getDescription());
    }

    @Test
    public void getTask_indexOutOfBounds() throws ClementineException {
        taskList.addTask(testTask1);
        assertThrows(ClementineException.class, () -> taskList.getTask(2));
        assertThrows(ClementineException.class, () -> taskList.getTask(10));
    }

    // Additional mark/unmark tests
    @Test
    public void markTask_emptyList_throwsException() {
        assertThrows(ClementineException.class, () -> taskList.markTask(1));
    }

    @Test
    public void markTask_invalidIndex_throwsException() throws ClementineException {
        taskList.addTask(testTask1);
        assertThrows(ClementineException.class, () -> taskList.markTask(0));
        assertThrows(ClementineException.class, () -> taskList.markTask(2));
        assertThrows(ClementineException.class, () -> taskList.markTask(-1));
    }

    @Test
    public void unmarkTask_validIndex_marksUndone() throws ClementineException {
        taskList.addTask(testTask1);
        testTask1.taskDone(); // First mark it as done
        assertTrue(testTask1.isDone());

        taskList.unmarkTask(1);
        assertFalse(testTask1.isDone());
    }

    @Test
    public void unmarkTask_emptyList_throwsException() {
        assertThrows(ClementineException.class, () -> taskList.unmarkTask(1));
    }

    @Test
    public void unmarkTask_invalidIndex_throwsException() throws ClementineException {
        taskList.addTask(testTask1);
        assertThrows(ClementineException.class, () -> taskList.unmarkTask(0));
        assertThrows(ClementineException.class, () -> taskList.unmarkTask(2));
        assertThrows(ClementineException.class, () -> taskList.unmarkTask(-1));
    }

    // Delete tasks
    @Test
    public void deleteTask_multipleTasksValidIndex_removesCorrectTask() throws ClementineException {
        taskList.addTask(testTask1);
        taskList.addTask(testTask2);
        assertEquals(2, taskList.taskSize());

        taskList.deleteTask(1); // Delete first task
        assertEquals(1, taskList.taskSize());
        assertEquals(testTask2, taskList.getTask(1)); // Second task should now be at index 1
    }

    @Test
    public void deleteTask_emptyList_throwsException() {
        assertThrows(ClementineException.class, () -> taskList.deleteTask(1));
    }

    @Test
    public void deleteTask_invalidIndex_throwsException() throws ClementineException {
        taskList.addTask(testTask1);
        assertThrows(ClementineException.class, () -> taskList.deleteTask(0));
        assertThrows(ClementineException.class, () -> taskList.deleteTask(2));
        assertThrows(ClementineException.class, () -> taskList.deleteTask(-1));
    }

    // Find tasks
    @Test
    public void findTasks_matchingKeyword_returnsMatchingTasks() throws ClementineException {
        Todo task1 = new Todo("finish homework assignment");
        Todo task2 = new Todo("complete project homework");
        Todo task3 = new Todo("read textbook");

        taskList.addTask(task1);
        taskList.addTask(task2);
        taskList.addTask(task3);

        ArrayList<Task> results = taskList.findTasks("homework");
        assertEquals(2, results.size());
        assertTrue(results.contains(task1));
        assertTrue(results.contains(task2));
        assertFalse(results.contains(task3));
    }

    @Test
    public void findTasks_noMatches_returnsEmptyList() throws ClementineException {
        taskList.addTask(new Todo("finish assignment"));
        taskList.addTask(new Todo("complete project"));

        ArrayList<Task> results = taskList.findTasks("nonexistent");
        assertTrue(results.isEmpty());
    }

    @Test
    public void findTasks_emptyList_returnsEmptyList() {
        ArrayList<Task> results = taskList.findTasks("anything");
        assertTrue(results.isEmpty());
    }

    @Test
    public void findTasks_partialMatch_returnsMatchingTasks() throws ClementineException {
        taskList.addTask(new Todo("mathematics homework"));
        taskList.addTask(new Todo("math quiz"));
        taskList.addTask(new Todo("english essay"));

        ArrayList<Task> results = taskList.findTasks("math");
        assertEquals(2, results.size());
    }

    // Priority tasks
    @Test
    public void getTasksByPriority_withPriorityTasks_returnsSortedTasks() throws ClementineException {
        Priority highPriority = new Priority(1);
        Priority mediumPriority = new Priority(2);
        Priority lowPriority = new Priority(3);

        Todo task1 = new Todo("urgent task", highPriority);
        Todo task2 = new Todo("medium task", mediumPriority);
        Todo task3 = new Todo("low priority task", lowPriority);
        Todo task4 = new Todo("no priority task");

        taskList.addTask(task3); // Add in random order
        taskList.addTask(task1);
        taskList.addTask(task4);
        taskList.addTask(task2);

        ArrayList<Task> priorityTasks = taskList.getTasksByPriority();

        assertEquals(3, priorityTasks.size()); // Should exclude task without priority
        assertEquals(task1, priorityTasks.get(0)); // Highest priority first (1)
        assertEquals(task2, priorityTasks.get(1)); // Medium priority second (2)
        assertEquals(task3, priorityTasks.get(2)); // Lowest priority last (3)
    }

    @Test
    public void getTasksByPriority_noPriorityTasks_returnsEmptyList() throws ClementineException {
        taskList.addTask(new Todo("task 1"));
        taskList.addTask(new Todo("task 2"));

        ArrayList<Task> priorityTasks = taskList.getTasksByPriority();
        assertTrue(priorityTasks.isEmpty());
    }

    @Test
    public void getTasksByPriority_emptyList_returnsEmptyList() {
        ArrayList<Task> priorityTasks = taskList.getTasksByPriority();
        assertTrue(priorityTasks.isEmpty());
    }

    // Test with different task types
    @Test
    public void taskList_withDifferentTaskTypes_worksCorrectly() throws ClementineException {
        LocalDateTime deadline = LocalDateTime.of(2024, 12, 31, 23, 59);
        LocalDateTime start = LocalDateTime.of(2024, 12, 25, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 25, 12, 0);

        Todo todo = new Todo("complete todo task");
        Deadline deadlineTask = new Deadline("submit assignment", deadline);
        Event event = new Event("attend meeting", start, end);

        taskList.addTask(todo);
        taskList.addTask(deadlineTask);
        taskList.addTask(event);

        assertEquals(3, taskList.taskSize());
        assertEquals(todo, taskList.getTask(1));
        assertEquals(deadlineTask, taskList.getTask(2));
        assertEquals(event, taskList.getTask(3));
    }

    // Test task list operations maintain consistency
    @Test
    public void taskList_complexOperations_maintainsConsistency() throws ClementineException {
        // Add tasks
        taskList.addTask(testTask1);
        taskList.addTask(testTask2);
        assertEquals(2, taskList.taskSize());

        // Mark first task
        taskList.markTask(1);
        assertTrue(taskList.getTask(1).isDone());

        // Delete second task
        taskList.deleteTask(2);
        assertEquals(1, taskList.taskSize());

        // Unmark remaining task
        taskList.unmarkTask(1);
        assertFalse(taskList.getTask(1).isDone());

        // Add another task
        Todo newTask = new Todo("new task");
        taskList.addTask(newTask);
        assertEquals(2, taskList.taskSize());
        assertEquals(newTask, taskList.getTask(2));
    }

    // Test getTaskList method
    @Test
    public void getTaskList_returnsUnderlyingList() throws ClementineException {
        taskList.addTask(testTask1);
        taskList.addTask(testTask2);

        ArrayList<Task> retrievedList = taskList.getTaskList();
        assertEquals(2, retrievedList.size());
        assertEquals(testTask1, retrievedList.get(0));
        assertEquals(testTask2, retrievedList.get(1));
    }


    @Test
    public void getTask_invalidNumber_throwsException() {
        assertThrows(ClementineException.class, () -> taskList.getTask(1));
    }

    @Test
    public void deleteTask_validIndex_removesTask() throws ClementineException {
        Task task = new Task("finish 2100 quiz");
        taskList.addTask(task);
        assertEquals(1, taskList.taskSize());

        taskList.deleteTask(1);
        assertEquals(0, taskList.taskSize());
    }

    @Test
    public void markTask_validIndex_marksDone() throws ClementineException {
        Task task = new Task("finish lsm reading");
        taskList.addTask(task);
        taskList.markTask(1);
        assertTrue(task.isDone());
    }
}
