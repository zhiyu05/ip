import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;


public class Clementine {

    private static ArrayList<Task> tasks = new ArrayList<>();
    private static final String FILE_PATH = "./data/clementine.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        loadTasks();
        intro();
        while(scanner.hasNextLine()) {
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                outro();
                break;
            }
            try {
                processCommand(input);
            } catch (ClementineException e) {
                line();
                System.out.println("OOPS!!! " + e.getMessage());
                line();
            }
        }
        scanner.close();
    }

    private static void processCommand(String input) throws ClementineException {
        if (input.isEmpty()) {
            throw new ClementineException("oh quack! i cant read empty commands!");
        } else if (input.equals("list")) {
            listTasks();
        } else if (input.startsWith("mark")) {
            markTask(input);
        } else if (input.startsWith("unmark")) {
            unmarkTask(input);
        } else if (input.startsWith("event")) {
            addEventTask(input);
        } else if (input.startsWith("deadline")) {
            addDeadlineTask(input);
        } else if (input.startsWith("todo")) {
            addTask(input);
        } else if (input.startsWith("delete")) {
            deleteTask(input);
        } else {
            throw new ClementineException("quack quack! i don't recognise this word!");
        }
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

    private static void loadTasks() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return;
            }

            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    parseTask(line);
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {

        } catch (Exception e) {
            System.out.println("quack! error loading tasks: " + e.getMessage());
        }
    }

    private static void parseTask(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return;
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

        if (task != null) {
            if (isDone == 1) {
                task.taskDone();
            }
            tasks.add(task);
        }
    }

    private static void saveTasks() {
        try {
            File file = new File(FILE_PATH);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            StringBuilder content = new StringBuilder();
            for (Task task : tasks) {
                content.append(task.storeData()).append("\n");
            }
            writeToFile(FILE_PATH, content.toString());
        } catch (IOException e) {
            System.out.println("quack! error saving tasks: " + e.getMessage());
        }
    }

    /*public static void intro () {
        showLine();
        System.out.println("Quack! I'm clementine\n What can i help you with today?\n");
        showLine();
    }

    public static void outro () {
        showLine();
        System.out.println("Bye! quack u later! hope you have a great day!");
        showLine();
    }

    public static void showLine() {
        System.out.println( "______________________________________________\n");
    }*/

    // for todo tasks
    public static void addTask (String taskDescription) throws ClementineException {
        if (tasks.size() >= 100) {
            throw new ClementineException("oh quack! the task list is full, please complete some tasks before adding extra!");
        }
        String cleanDescription;
        if (taskDescription.equals("todo")) {
            throw new ClementineException("quack! the description of a todo cannot be empty!");
        } else if (taskDescription.startsWith("todo ")) {
            cleanDescription = taskDescription.substring(5).trim(); // Remove "todo "
        } else {
            cleanDescription = taskDescription.trim();
        }

        if (cleanDescription.isEmpty()) {
            throw new ClementineException("quack! the description of a todo cannot be empty!");
        }

        Task task = new Todo(cleanDescription);
        tasks.add(task);
        saveTasks();
        String response = "okay! ive added the task quack!";
        line();
        System.out.println(response);
        System.out.println(" " + task.toString());
        System.out.println("now you have " + tasks.size() + " remaining tasks!");
        line();
    }

    /*public static void listTasks () {
        String response = "tasks to be done before freedom:\n";
        for (int i = 0; i < tasks.size(); i++) {
            response += (i + 1) + ". " + tasks.get(i).toString() + "\n";
        }
        showLine();
        System.out.println(response);
        showLine();
    }*/

    public static void markTask(String input) throws ClementineException{
        if (tasks.size() == 0) {
            throw new ClementineException ("quack! u dont have any tasks yet!");
        }

        if (input.equals("mark")) {
            throw new ClementineException("quack! please provide a task number");
        }

        try{
            String numberPart = input.substring(5);
            int taskNumber = Integer.parseInt(numberPart);

            if (taskNumber >= 1 && taskNumber <= tasks.size()) {
                tasks.get(taskNumber - 1).taskDone();
                saveTasks();
                line();
                System.out.println("good job! you've completed the task");
                System.out.println(" " + tasks.get(taskNumber - 1).toString());
                line();
            } else {
                throw new ClementineException("invalid task number!");
            }
        } catch (NumberFormatException e) {
            throw new ClementineException("please provide a valid number");
        }
    }

    public static void unmarkTask(String input) throws ClementineException{
        if (tasks.size() == 0) {
            throw new ClementineException ("quack! u dont have any tasks yet!");
        }

        if (input.equals("unmark")) {
            throw new ClementineException("please provide a number!");
        }

        try {
            String numberPart = input.substring(7);
            int taskNumber = Integer.parseInt(numberPart);

            if (taskNumber >= 1 && taskNumber <= tasks.size()) {
                tasks.get(taskNumber - 1).taskUndone();
                saveTasks();
                line();
                System.out.println("okay, ive changed this task to not done. quack!");
                System.out.println(" " + tasks.get(taskNumber - 1).toString());
                line();
            } else {
                throw new ClementineException("invalid task number");
            }
        } catch (NumberFormatException e) {
            throw new ClementineException("please provide a valid number");
        }
    }

    public static void addDeadlineTask(String input) throws ClementineException{
        if (tasks.size() >= 100) {
            throw new ClementineException("oh quack! the task list is full, please complete some tasks before adding extra!");
        }
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
                Task task = new Deadline(description, deadlineTime);
                tasks.add(task);
                saveTasks();
                String response = "okay! ive added the deadline task quack!";
                line();
                System.out.println(response);
                System.out.println(" " + task.toString());
                System.out.println("now you have " + tasks.size() + " remaining tasks!");
                line();
            } catch (DateTimeParseException e) {
                throw new ClementineException("quack!" + e.getMessage());
            }

        } else {
            throw new ClementineException("quack! please use the format: deadline <description> /by <date>");
        }
    }

    public static void addEventTask (String input) throws ClementineException{
        if (tasks.size() >= 100) {
            throw new ClementineException("oh quack! the task list is full, please complete some tasks before adding extra!");
        }
        if (input.equals("event")) {
            throw new ClementineException("quack! the description of the event cannot be empty");
        }

        if (!input.contains("/from") || !input.contains("/to")) {
            throw new ClementineException("quack! please use the format: event <description> /from <time> /to <time>");
        }
        String[] parts = input.split("/from", 2);
        if (parts.length == 2) {
            String description = parts[0].substring(6).trim();
            String [] timeline = parts[1].split("/to", 2);

            if (description.isEmpty()) {
                throw new ClementineException("quack! the description of the event cannot be empty");
            }
            if (timeline.length == 2) {
                String startTime = timeline[0].trim();
                LocalDateTime start = parseDateTime(startTime);
                String endTime = timeline[1].trim();
                LocalDateTime end = parseDateTime(endTime);
                if (startTime.isEmpty() || endTime.isEmpty()) {
                    throw new ClementineException("both start time and end time must be specified!");
                }

                Task task = new Event(description, start, end);
                tasks.add(task);
                saveTasks();
                line();
                System.out.println("okay! ive added the event task for u! quack!");
                System.out.println(" " + task.toString());
                System.out.println("now you have " + tasks.size() + " remaining tasks!");
                line();
            } else {
                throw new ClementineException("quack! please use the format: event <description> /from <time> /to <time>");
            }
        } else {
            throw new ClementineException("please use correct format for event task!");
        }
    }

    public static void deleteTask (String input) throws ClementineException{
        if (input.equals("delete")) {
            throw new ClementineException("please specify a number!");
        }

        if (tasks.size() == 0) {
            throw new ClementineException("quack! you dont have any tasks to delete!");
        }

        try {
            String numberPart = input.substring(7);
            int taskNumber = Integer.parseInt(numberPart);

            if (taskNumber >= 1 && taskNumber <= tasks.size()) {
                Task deletedTask = tasks.remove(taskNumber - 1);
                saveTasks();
                line();
                System.out.println("quack! ive deleted this task for you :)");
                System.out.println(" " + deletedTask.toString());
                System.out.println("now you have " + tasks.size() + " remaining tasks");
                line();
            } else {
                throw new ClementineException("invalid task number!");
            }
        } catch (NumberFormatException e) {
            throw new ClementineException("please provide a valid number!");
        }
    }

    private static void writeToFile(String filePath, String textToAdd) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        fw.write(textToAdd);
        fw.close();
    }
}
