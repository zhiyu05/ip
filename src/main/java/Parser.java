import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Parser {

    public static String processCommand(String input) throws ClementineException {
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
