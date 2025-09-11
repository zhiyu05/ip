package clementine.task;

/**
 * Represents a generic task with a description and a completion status.
 * @author zhiyu
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a new Task with the specified description.
     * Tasks are initialised as not completed by default.
     * @param description the description of the task
     */
    public Task(String description) {
        this.description = description;
        assert description != null && !description.trim().isEmpty()
                : "Task description cannot be null or empty";
        this.isDone = false;
    }

    /**
     * Returns the status icon for display purposes.
     * @return "X" if the task is completed, " " (space) if not completed
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Returns the completion status as a numeric value for storage purposes.
     * @return 1 if the task is completed, 0 if not completed
     */
    public int getCompletion() {
        return (isDone ? 1 : 0);
    }

    /**
     * Returns the description of the task
     * @return the task description as a String
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns a string representation of the task for display to users.
     * Shows the completion status and description in a formatted manner.
     * @return a formatted string in the format "[status_icon] description"
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + this.description;
    }

    /**
     * Marks the task as completed.
     */
    public void taskDone() {
        this.isDone = true;
        assert this.isDone : "Task should be marked as done after calling taskDone()";
    }

    /**
     * Marks the task as not completed.
     */
    public void taskUndone() {
        this.isDone = false;
        assert !this.isDone : "Task should be marked as undone after calling taskUndone()";
    }

    /**
     * Returns a string representation of the task suitable for file storage.
     * Provides the basic format that subclasses can extend with additional information.
     * @return a formatted string for storage in the format "completion_status | description"
     */
    public String storeData() {
        return getCompletion() + " | " + this.description;
    }

    /**
     * Checks whether the task is completed.
     * @return true if the task is completed, false otherwise
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Checks if the task's description contains the given keyword,
     * ignoring case sensitivity.
     * @param keyword the keyword to search for in the task description
     * @return {@code true} if the description contains the keyword,
     *         {@code false} otherwise
     */
    public boolean containsKeyword(String keyword) {
        return this.description.toLowerCase().contains(keyword.toLowerCase());
    }

}

