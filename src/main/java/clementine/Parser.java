package clementine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import clementine.task.Deadline;
import clementine.task.Event;
import clementine.task.Task;
import clementine.task.Todo;

/**
 * The Parser class provides static utility methods for parsing user input and handling data conversion.
 * This class handles command interpretation, task creation from user input, file I/O operations,
 * and date/time formatting for the Clementine chatbot.
 *
 * @author zhiyu
 */
public class Parser {

    public static CommandType getCommandType(String input) throws ClementineException {
        if (input == null || input.trim().isEmpty()) {
            throw new ClementineException("oh quack! i can't read empty commands!");
        }

        String commandWord = input.trim().split(" ")[0];
        return CommandType.fromString(commandWord);
    }

    public static Priority parsePriority(String input) throws ClementineException {
        if (input.contains("/priority")) {
            String[] parts = input.split("/priority", 2);
            String numberPart = parts[1].trim();
            try {
                int level = Integer.parseInt(numberPart);
                if (level < 1) {
                    throw new ClementineException("Priority must be a positive number!");
                }
                return new Priority(level);
            } catch (NumberFormatException e) {
                throw new ClementineException("Please provide a valid numberic priority");
            }
        }
        return null;
    }

    /**
     * Parses a stored task line and reconstructs the corresponding Task object.
     * Handles Todo, Deadline, and Event task types from their stored format.
     * @param line the stored task data as a string
     * @return the reconstructed Task object, or null if the line format is invalid
     */
    public static Task parseTask(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }

        String taskType = parts[0];
        assert taskType != null : "Task type should not be null";
        int isDone = Integer.parseInt(parts[1]);
        assert isDone == 0 || isDone == 1 : "isDone should be 0 or 1";
        String description = parts[2];
        assert description != null : "Task description should not be null";

        Task task = null;

        switch (taskType) {
        case "T":
            task = new Todo(description);
            break;

        case "D":
            if (parts.length >= 4) {
                String deadline = parts[3].replace("/by ", "");
                LocalDateTime timeDeadline = parseDateTime(deadline);
                task = new Deadline(description, timeDeadline);
            }
            break;

        case "E":
            if (parts.length >= 4) {
                String timeInfo = parts[3];

                if (timeInfo.contains("/from") && timeInfo.contains("/to")) {
                    String[] timeParts = timeInfo.split("/from", 2)[1].split("/to", 2);
                    if (timeParts.length == 2) {
                        String startTime = timeParts[0].trim();
                        LocalDateTime start = parseDateTime(startTime);
                        String endTime = timeParts[1].trim();
                        LocalDateTime end = parseDateTime(endTime);
                        task = new Event(description, start, end);
                    }
                }
            }
            break;
        default:
            return null;
        }

        if (task != null && isDone == 1) {
            assert task.getDescription() != null : "Created task should have description";
            assert task.isDone() : "Task should be marked as done if isDone was 1";
            task.taskDone();
        }
        return task;
    }

    /**
     * Extracts and parses the task number from a user command.
     * @param input the full user inout string
     * @param commandType the command type to remove from the input
     * @return the parsed task number as an integer
     * @throws ClementineException if the input doesn't contain a valid number
     */
    public static int parseTaskNumber(String input, String commandType) throws ClementineException {
        try {
            String numberPart = input.substring(commandType.length()).trim();
            int result = Integer.parseInt(numberPart);
            assert result > 0 : "Parsed task number should be positive";
            return result;
        } catch (NumberFormatException e) {
            throw new ClementineException("please provide a valid number!");
        }
    }

    /**
     * Parses user input to create a Deadline task.
     * Expected format: "{@code deadline <description> /by <date>}"
     * @param input the user's deadline command input
     * @return a new Deadline object
     * @throws ClementineException if the input format is invalid or required fields are missing
     */
    public static Deadline parseDeadlineTask(String input) throws ClementineException {
        if (input.equals("deadline")) {
            throw new ClementineException("the description of a deadline cannot be empty");
        }
        if (!input.contains("/by")) {
            throw new ClementineException("quack! please use the format: deadline <description> /by <date>");
        }
        String[] parts = input.split("/by", 2);
        if (parts.length < 2) {
            throw new ClementineException("quack! please use the format: deadline <description> /by <date>");
        }

        String description = parts[0].substring(9).trim();
        String deadlineStr = parts[1].split("/priority", 2)[0].trim();

        if (description.isEmpty()) {
            throw new ClementineException("the description of a deadline cannot be empty");
        }

        if (deadlineStr.isEmpty()) {
            throw new ClementineException("the deadline date cannot be empty!");
        }

        try {
            LocalDateTime deadlineTime = parseDateTime(deadlineStr);
            Priority priority = parsePriority(input);
            if (priority != null) {
                return new Deadline(description, deadlineTime, priority);
            } else {
                return new Deadline(description, deadlineTime);
            }
        } catch (DateTimeParseException e) {
            throw new ClementineException("quack!" + e.getMessage());
        }
    }

    /**
     * Parses user input to create an Event task.
     * Expected format: "{@code event <description> /from <time> /to <time>}"
     * @param input the user's event command input
     * @return a new Event object
     * @throws ClementineException if the input format is invalid or required fields are missing
     */
    public static Event parseEventTask(String input) throws ClementineException {
        if (input.equals("event")) {
            throw new ClementineException("quack! the description of the event cannot be empty");
        }

        if (!input.contains("/from") || !input.contains("/to")) {
            throw new ClementineException("quack! please use the format: event <description> /from <time> /to <time>");
        }
        String[] parts = input.split("/from", 2);
        if (parts.length != 2) {
            throw new ClementineException("please use correct format for event task!");
        }

        String description = parts[0].substring(6).trim();
        if (description.isEmpty()) {
            throw new ClementineException("quack! the description of the event cannot be empty");
        }

        String [] timeline = parts[1].split("/to", 2);
        if (timeline.length != 2) {
            throw new ClementineException("quack! please use the format: event <description> /from <time> /to <time>");
        }

        String startTime = timeline[0].trim();
        // String endTime = timeline[1].trim();
        String endTime = timeline[1].split("/priority", 2)[0].trim();

        if (startTime.isEmpty() || endTime.isEmpty()) {
            throw new ClementineException("both start time and end time must be specified!");
        }

        try {
            LocalDateTime start = parseDateTime(startTime);
            LocalDateTime end = parseDateTime(endTime);
            Priority priority = Parser.parsePriority(input);
            if (priority != null) {
                return new Event(description, start, end, priority);
            } else {
                return new Event(description, start, end);
            }
        } catch (DateTimeParseException e) {
            throw new ClementineException("quack! invalid date/time format");
        }
    }

    /**
     * Parses user input to create a Todo task.
     * Expected format: "{@code todo <description>}"
     * @param input the user's todo command input
     * @return a new Todo object
     * @throws ClementineException if the description is empty
     */
    public static Todo parseTodoTask(String input) throws ClementineException {
        if (input.equals("todo")) {
            throw new ClementineException("quack! the description of a todo cannot be empty!");
        }

        String[] parts = input.split("/priority", 2);
        String cleanDescription = parts[0].substring(5).trim();
        if (cleanDescription.isEmpty()) {
            throw new ClementineException("quack! the description of a todo cannot be empty!");
        }
        Priority priority = parsePriority(input);
        if (priority != null) {
            return new Todo(cleanDescription, priority);
        } else {
            return new Todo(cleanDescription);
        }
    }

    /**
     * Parses a date/time string into a LocalDateTime object.
     * Supports multiple formats including dates with and without times.
     * If only a date is provided, sets the time to start of day (00:00).
     * @param input the date/time string to parse
     * @return a LocalDateTime object representing the parsed date/time
     * @throws DateTimeParseException if the input doesn't match any supported formats
     */
    public static LocalDateTime parseDateTime(String input) throws DateTimeParseException {
        DateTimeFormatter[] dateTimeFormatters = {
                DateTimeFormatter.ofPattern("dd/MM/yyyy HHmm"),
                DateTimeFormatter.ofPattern("d/MM/yyyy HHmm"),
                DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
                DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm"),
                DateTimeFormatter.ofPattern("d/M/yyyy HH:mm")
        };

        DateTimeFormatter[] dateFormatters = {
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("d/M/yyyy")
        };

        for (DateTimeFormatter f : dateTimeFormatters) {
            try {
                return LocalDateTime.parse(input, f);
            } catch (DateTimeParseException e) {
                // try next format
            }
        }

        for (DateTimeFormatter f : dateFormatters) {
            try {
                LocalDate date = LocalDate.parse(input, f);
                return date.atStartOfDay();
            } catch (DateTimeParseException e) {
                // try next
            }
        }

        throw new DateTimeParseException("invalid date format!use dd/MM/yyyy or dd/MM/yyyy HHmm", input, 0);
    }

    /**
     * Formats a LocalDateTime object for user-friendly display.
     * If the time is 00:00, displays only the date.
     * Otherwise, displays both date and time in 12-hour format.
     * @param dateTime the LocalDateTime to format
     * @return a formatted string suitable for display to users
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        // if no time specified
        if (dateTime.getHour() == 0 && dateTime.getMinute() == 0) {
            return dateTime.format(DateTimeFormatter.ofPattern("d MMM yyyy"));
        } else {
            return dateTime.format(DateTimeFormatter.ofPattern("d MMM yyyy h:mm a"));
        }
    }

    /**
     * Formats a LocalDateTime object for storage in files.
     * Uses a consistent format for reliable parsing when loading data.
     * @param dateTime the LocalDateTime to format for storage
     * @return a formatted string suitable for file storage
     */
    public static String formatDateTimeForStorage(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HHmm"));
    }

    /**
     * Extracts a keyword from the {@code find} command input.
     * @param input the full user input string starting with {@code find}
     * @return the keyword to search for, trimmed of leading and trailing whitespace
     * @throws ClementineException if no keyword is specified after {@code find}
     */
    public static String parseFindKeyword(String input) throws ClementineException {
        if (input.equals("find")) {
            throw new ClementineException("quack! please specify the keyword to search for!");
        }

        String keyword = input.substring(4).trim();

        if (keyword.isEmpty()) {
            throw new ClementineException("quack! please specify the keyword to search for!");
        }

        return keyword;
    }
}
