package clementine.task;

import java.time.LocalDateTime;

import clementine.Parser;
import clementine.Priority;

/**
 * Represents a task with a deadline in the Clementine task management application.
 * A Deadline task extends the basic Task functionality by adding a specific due date and time.
 * This class handles display formatting and storage serialization for deadline-based tasks.
 *
 * @author zhiyu
 */
public class Deadline extends Task {
    protected LocalDateTime by;

    /**
     * Constructs a new Deadline task with the specified description and due date.
     * @param description the description of the task
     * @param by the deadline date and time for this task
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    public Deadline(String description, LocalDateTime by, Priority priority) {
        super(description, priority);
        this.by = by;
    }

    /**
     * Returns a string representation of the deadline task for display to users.
     * Formats the deadline date/time in a user-friendly format.
     * @return a formatted string in the format "[D][status] description (by: date)"
     */
    @Override
    public String toString() {
        String formattedDate = Parser.formatDateTime(by);
        return "[D]" + super.toString() + " (by:" + formattedDate + ")";
    }

    /**
     * Returns a string representation of the deadline task suitable for file storage.
     * Uses a consistent format that can be parsed when loading from storage.
     * @return a formatted string for storage in the format "D | status | description | /by date"
     */
    @Override
    public String storeData() {
        String dateString = Parser.formatDateTimeForStorage(by);
        if (hasPriority()) {
            return "D | " + super.storeData() + " | /by " + dateString + " | " + getPriority().getLevel();
        } else {
            return "D | " + super.storeData() + " | /by " + dateString;
        }
    }
}
