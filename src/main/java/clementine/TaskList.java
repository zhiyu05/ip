package clementine;
import clementine.task.Task;

import java.util.ArrayList;

public class TaskList {
    private ArrayList<Task> tasks;
    private static final int MAX_TASKS = 100;

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public ArrayList<Task> getTaskList() {
        return tasks;
    }

    public int taskSize() {
        return tasks.size();
    }

    public Task getTask(int index) throws ClementineException {
        if (index < 1 || index > tasks.size()) {
            throw new ClementineException("invalid task number!");
        }
        return tasks.get(index - 1);
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    public void addTask (Task task) throws ClementineException {
        if (tasks.size() >= MAX_TASKS) {
            throw new ClementineException("oh quack! the task list is full, please complete some tasks before adding extra!");
        }
        tasks.add(task);
    }

    public void markTask(int index) throws ClementineException{
        if (tasks.isEmpty()) {
            throw new ClementineException ("quack! u dont have any tasks yet!");
        }

        if (index < 1 || index > tasks.size()) {
            throw new ClementineException("invalid task number!");
        }

        tasks.get(index - 1).taskDone();
    }

    public void unmarkTask(int index) throws ClementineException{
        if (tasks.isEmpty()) {
            throw new ClementineException ("quack! u dont have any tasks yet!");
        }

        if (index < 1 || index > tasks.size()) {
            throw new ClementineException("invalid task number");
        }

        tasks.get(index - 1).taskUndone();
    }

    public void deleteTask (int index) throws ClementineException{
        if (tasks.isEmpty()) {
            throw new ClementineException("quack! you dont have any tasks to delete!");
        }

        if (index < 1 || index > tasks.size()) {
            throw new ClementineException("invalid task number");
        }

        tasks.remove(index - 1);
    }

    public ArrayList<Task> findTasks(String keyword) {
        ArrayList<Task> matchingTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.containsKeyword(keyword)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }
}
