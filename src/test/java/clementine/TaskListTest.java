package clementine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clementine.task.Task;


public class TaskListTest {
    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
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
