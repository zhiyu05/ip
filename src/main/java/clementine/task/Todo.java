package clementine.task;

import clementine.Priority;

/**
 * Represents a simple todo task without any time constraints.
 * A Todo is the most basic type of task, containing only a description and completion status.
 * This class provides the concrete implementation for tasks that don't require deadlines or time ranges.
 *
 * @author zhiyu
 */
public class Todo extends Task {
    /**
     * Constructs a new Todo task with the specified description.
     * @param description the description of the todo task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Constructs a new Todo task with the specified description and priority.
     *
     * @param description the description of the todo task
     * @param priority the priority level for this todo task
     */
    public Todo(String description, Priority priority) {
        super(description, priority);
    }

    /**
     * Returns a string representation of the todo task for display to users.
     * Adds the todo-specific type indicator to the base task format.
     * @return a formatted string in the format "[T][status_icon] description"
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns a string representation of the todo task suitable for file storage.
     * Adds the todo-specific type indicator to the base storage format.
     * @return a formatted string for storage in the format "T | completion_status | description"
     */
    @Override
    public String storeData() {
        if (hasPriority()) {
            return "T | " + super.storeData() + " | " + getPriority().getLevel();
        } else {
            return "T | " + super.storeData();
        }
    }
}
