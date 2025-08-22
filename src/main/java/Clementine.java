import java.util.Scanner;
import java.util.ArrayList;

public class Clementine {

    private static ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
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
    public static void intro () {
        line();
        System.out.println("Quack! I'm clementine\n What can i help you with today?\n");
        line();
    }

    public static void outro () {
        line();
        System.out.println("Bye! quack u later! hope you have a great day!");
        line();
    }

    public static void line() {
        System.out.println( "______________________________________________\n");
    }

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
        String response = "okay! ive added the task quack!";
        line();
        System.out.println(response);
        System.out.println(" " + task.toString());
        System.out.println("now you have " + tasks.size() + " remaining tasks!");
        line();
    }

    public static void listTasks () {
        String response = "tasks to be done before freedom:\n";
        for (int i = 0; i < tasks.size(); i++) {
            response += (i + 1) + ". " + tasks.get(i).toString() + "\n";
        }
        line();
        System.out.println(response);
        line();
    }

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

            Task task = new Deadline(description, deadline);
            tasks.add(task);
            String response = "okay! ive added the deadline task quack!";
            line();
            System.out.println(response);
            System.out.println(" " + task.toString());
            System.out.println("now you have " + tasks.size() + " remaining tasks!");
            line();
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
                String endTime = timeline[1].trim();
                if (startTime.isEmpty() || endTime.isEmpty()) {
                    throw new ClementineException("both start time and end time must be specified!");
                }

                Task task = new Event(description, startTime, endTime);
                tasks.add(task);
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
}
