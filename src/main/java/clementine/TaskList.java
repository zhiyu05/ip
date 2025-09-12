package clementine;

import java.util.ArrayList;

import clementine.task.Task;

/**
 * The TaskList class manages a collection of tasks for the Clementine chatbot.
 * This class provides operations to add, delete, mark, unmark, and retrieve tasks.
 *
 * @author zhiyu
 */
public class TaskList {
    private static final int MAX_TASKS = 100;
    private ArrayList<Task> tasks;

    /**
     * Constructs a TaskList with an existing list of tasks.
     * @param tasks an ArrayList of Task objects to initialise the TaskList with
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Tasks list should not be null";
        this.tasks = tasks;
        assert this.tasks != null : "TaskList should have initialised tasks";
    }

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Returns the underlying ArrayList of tasks.
     * @return the ArrayList containing all tasks
     */
    public ArrayList<Task> getTaskList() {
        return tasks;
    }

    /**
     * Returns the number of tasks in the list.
     * @return the total number of tasks
     */
    public int taskSize() {
        return tasks.size();
    }

    /**
     * Retrieves a task at the specified position (1-indexed).
     * @param index the position of the task (1-indexed)
     * @return the Task object at the specified position
     * @throws ClementineException if the index is out of bounds
     */
    public Task getTask(int index) throws ClementineException {
        if (index < 1 || index > tasks.size()) {
            throw new ClementineException("invalid task number!");
        }
        Task result = tasks.get(index - 1);
        assert result != null : "Retrieved task should not be null";
        return result;
    }

    /**
     * Checks whether the task list is empty.
     * @return true if the tasklist contains no tasks, false otherwise.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Adds a new task to the list.
     * Enforces a maximum limit of 100 tasks
     * @param task the Task object to add to the list
     * @throws ClementineException if the task list has reached its maximum capacity of 100 tasks
     */
    public void addTask(Task task) throws ClementineException {
        if (tasks.size() >= MAX_TASKS) {
            throw new ClementineException("oh quack! the task list is full,"
                    + "please complete some tasks before adding extra!");
        }
        int sizeBefore = tasks.size();
        tasks.add(task);
        assert tasks.size() == sizeBefore + 1 : "Task list size should increase by 1";
        assert tasks.contains(task) : "Added task should be in the list";
    }

    /**
     * Marks a task as completed at the specified position (1-indexed).
     * @param index the position of the task to mark as done (1-indexed)
     * @throws ClementineException if the tasklist is empty or the index is out of bounds
     */
    public void markTask(int index) throws ClementineException {
        if (tasks.isEmpty()) {
            throw new ClementineException("quack! u dont have any tasks yet!");
        }

        if (index < 1 || index > tasks.size()) {
            throw new ClementineException("invalid task number!");
        }

        Task taskToMark = tasks.get(index - 1);
        assert taskToMark != null : "Task to mark should not be null";
        taskToMark.taskDone();
        assert taskToMark.isDone() : "Task should be marked as done after marking";
    }

    /**
     * Marks a task as not completed at the specified position (1-indexed).
     * @param index the position of the task to mark as not done (1-indexed)
     * @throws ClementineException if the tasklist is empty or the index is out of bounds
     */
    public void unmarkTask(int index) throws ClementineException {
        if (tasks.isEmpty()) {
            throw new ClementineException("quack! u dont have any tasks yet!");
        }

        if (index < 1 || index > tasks.size()) {
            throw new ClementineException("invalid task number");
        }

        tasks.get(index - 1).taskUndone();
    }

    /**
     * Removes a task from the list at the specified position (1-indexed)
     * @param index the position of the task to delete (1-indexed)
     * @throws ClementineException if the task list is empty or the index is out of bounds
     */
    public void deleteTask(int index) throws ClementineException {
        if (tasks.isEmpty()) {
            throw new ClementineException("quack! you dont have any tasks to delete!");
        }

        if (index < 1 || index > tasks.size()) {
            throw new ClementineException("invalid task number");
        }

        int sizeBefore = tasks.size();
        tasks.remove(index - 1);

        assert tasks.size() == sizeBefore - 1 : "Task list size should decrease by 1";
    }

    /**
     * Finds all tasks in the list that contain the given keyword.
     * @param keyword the word or phrase to search for in task descriptions
     * @return a list of tasks whose descriptions contain the given keyword;
     *         the list will be empty if no matches are found
     */
    public ArrayList<Task> findTasks(String keyword) {
        ArrayList<Task> matchingTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.containsKeyword(keyword)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }

    public ArrayList<Task> getTasksByPriority() {
        ArrayList<Task> result = new ArrayList<>();
        tasks.stream()
                .filter(Task::hasPriority)
                .sorted((t1, t2) -> Integer.compare(t1.getPriority().getLevel(), t2.getPriority().getLevel()))
                .forEach(result::add);
        return result;
    }
}
