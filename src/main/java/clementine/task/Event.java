package clementine.task;

import java.time.LocalDateTime;

import clementine.Parser;
import clementine.Priority;

/**
 * Represents a task with a specific start and end time in the Clementine task management application.
 * An Event task extends the basic Task functionality by adding time range information.
 * This class handles display formatting and storage serialization for time-bound events.
 *
 * @author zhiyu
 */
public class Event extends Task {
    protected LocalDateTime start;
    protected LocalDateTime end;

    /**
     * Constructs a new Event task with the specified description, start time, and end time.
     * @param description the description of the event
     * @param start the start date and time of the event
     * @param end the end date and time of the event
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    public Event(String description, LocalDateTime start, LocalDateTime end, Priority priority) {
        super(description, priority);
        this.start = start;
        this.end = end;
    }
    /**
     * Returns a string representation of the event task for display to users.
     * Formats both start and end times in a user-friendly format.
     * @return a formatted string in the format "[E][status] description (from: start_time to: end_time)"
     */
    @Override
    public String toString() {
        String started = Parser.formatDateTime(this.start);
        String ended = Parser.formatDateTime(this.end);
        return "[E]" + super.toString() + " (from:" + started + " to:" + ended + ")";
    }

    /**
     * Returns a string representation of the event task suitable for file storage.
     * Uses a consistent format that can be parsed when loading from storage.
     * @return a formatted string for storage in the format "E | status | description | /from start_date/to end_date"
     */
    @Override
    public String storeData() {
        String started = Parser.formatDateTimeForStorage(this.start);
        String ended = Parser.formatDateTimeForStorage(this.end);
        if (hasPriority()) {
            return "E | " + super.storeData() + " | /from " + started + "/to " + ended + " | " + getPriority().getLevel();
        } else {
            return "E | " + super.storeData() + " | /from " + started + "/to " + ended;
        }
    }
}
