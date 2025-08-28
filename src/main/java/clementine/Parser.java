package clementine;

import clementine.task.Deadline;
import clementine.task.Event;
import clementine.task.Task;
import clementine.task.Todo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Parser {

    public static String getCommandType(String input) throws ClementineException {
        if (input.isEmpty()) {
            throw new ClementineException("oh quack! i cant read empty commands!");
        } else if (input.equals("list")) {
            return "list";
        } else if (input.startsWith("mark")) {
            return "mark";
        } else if (input.startsWith("unmark")) {
            return "unmark";
        } else if (input.startsWith("event")) {
            return "event";
        } else if (input.startsWith("deadline")) {
            return "deadline";
        } else if (input.startsWith("todo")) {
            return "todo";
        } else if (input.startsWith("delete")) {
            return "delete";
        } else {
            throw new ClementineException("quack quack! i don't recognise this word!");
        }
    }



    public static Task parseTask(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }

        String taskType = parts[0];
        int isDone = Integer.parseInt(parts[1]);
        String description = parts[2];

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
        }

        if (task != null && isDone == 1) {
            task.taskDone();
        }
        return task;
    }

    public static int parseTaskNumber(String input, String commandType) throws ClementineException{
        try {
            String numberPart = input.substring(commandType.length()).trim();
            return Integer.parseInt(numberPart);
        } catch (NumberFormatException e) {
            throw new ClementineException("please provide a valid number!");
        }
    }

    public static Deadline parseDeadlineTask(String input) throws ClementineException {
        if (input.equals("deadline")) {
            throw new ClementineException("the description of a deadline cannot be empty");
        }
        if (!input.contains("/by")) {
            throw new ClementineException("quack! please use the format: deadline <description> /by <date>");
        }
        String[] parts = input.split("/by", 2);

        if (parts.length == 2) {
            String description = parts[0].substring(9).trim();
            String deadline = parts[1].trim();

            if (description.isEmpty()) {
                throw new ClementineException("the description of a deadline cannot be empty");
            }

            if (deadline.isEmpty()) {
                throw new ClementineException("the deadline date cannot be empty!");
            }

            try {
                LocalDateTime deadlineTime = parseDateTime(deadline);
                return new Deadline(description, deadlineTime);
            } catch (DateTimeParseException e) {
                throw new ClementineException("quack!" + e.getMessage());
            }

        } else {
            throw new ClementineException("quack! please use the format: deadline <description> /by <date>");
        }
    }

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
        String endTime = timeline[1].trim();

        if (startTime.isEmpty() || endTime.isEmpty()) {
            throw new ClementineException("both start time and end time must be specified!");
        }

        try {
            LocalDateTime start = parseDateTime(startTime);
            LocalDateTime end = parseDateTime(endTime);
            return new Event(description, start, end);
        } catch (DateTimeParseException e) {
            throw new ClementineException("quack! invalid date/time format");
        }
    }

    public static Todo parseTodoTask(String input) throws ClementineException {
        if (input.equals("todo")) {
            throw new ClementineException("quack! the description of a todo cannot be empty!");
        }

        String cleanDescription = input.substring(5).trim();
        if (cleanDescription.isEmpty()) {
            throw new ClementineException("quack! the description of a todo cannot be empty!");
        }

        return new Todo(cleanDescription);
    }

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

    public static String formatDateTime(LocalDateTime dateTime) {
        // if no time specified
        if (dateTime.getHour() == 0 && dateTime.getMinute() == 0) {
            return dateTime.format(DateTimeFormatter.ofPattern("d MMM yyyy"));
        } else {
            return dateTime.format(DateTimeFormatter.ofPattern("d MMM yyyy h:mm a"));
        }
    }

    public static String formatDateTimeForStorage(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HHmm"));
    }
}
