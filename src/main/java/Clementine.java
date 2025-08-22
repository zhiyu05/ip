import java.util.Scanner;
public class Clementine {
    private static Task[] tasks = new Task[100];
    private static int taskCount = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        intro();
        while(scanner.hasNextLine()) {
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                outro();
                break;
            } else if (input.equals("list")) {
                listTasks();
            } else if (input.startsWith("mark ")) {
                markTask(input);
            } else if (input.startsWith("unmark ")) {
                unmarkTask(input);
            } else if (input.startsWith("event ")) {
                addEventTask(input);
            } else if (input.startsWith("deadline ")) {
                addDeadlineTask(input);
            } else {
                addTask(input);
            }
        }
        scanner.close();
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

    public static void addTask (String taskDescription) {
        Task task = new Todo(taskDescription);
        tasks[taskCount] = task;
        String response = "okay! ive added the task quack!";
        taskCount++;
        line();
        System.out.println(response);
        System.out.println(" " + task.toString());
        System.out.println("now you have " + taskCount + " remaining tasks!");
        line();
    }

    public static void listTasks () {
        String response = "tasks to be done before freedom:\n";
        for (int i = 1; i <= taskCount; i++) {
            response += i + ". " + tasks[i - 1].toString() + "\n";
        }
        line();
        System.out.println(response);
        line();
    }

    public static void markTask(String input) {
        try{
            String numberPart = input.substring(5);
            int taskNumber = Integer.parseInt(numberPart);

            if (taskNumber >= 1 && taskNumber <= taskCount) {
                tasks[taskNumber - 1].taskDone();
                line();
                System.out.println("good job! you've completed the task");
                System.out.println(" " + tasks[taskNumber - 1].toString());
                line();
            } else {
                System.out.println("invalid task number!");
            }
        } catch (NumberFormatException e) {
            System.out.println("please provide a valid number");
        }
    }

    public static void unmarkTask(String input) {
        try {
            String numberPart = input.substring(7);
            int taskNumber = Integer.parseInt(numberPart);

            if (taskNumber >= 1 && taskNumber <= taskCount) {
                tasks[taskNumber - 1].taskUndone();
                line();
                System.out.println("okay, ive changed this task to not done. quack!");
                System.out.println(" " + tasks[taskNumber - 1].toString());
                line();
            } else {
                System.out.println("invalid task number");
            }
        } catch (NumberFormatException e) {
            System.out.println("please provide a valid number");
        }
    }

    public static void addDeadlineTask(String input) {
        String[] parts = input.split("/by", 2);

        if (parts.length == 2) {
            String description = parts[0].substring(9);
            String deadline = parts[1];

            Task task = new Deadline(description, deadline);
            tasks[taskCount] = task;
            String response = "okay! ive added the deadline task quack!";
            taskCount++;
            line();
            System.out.println(response);
            System.out.println(" " + task.toString());
            System.out.println("now you have " + taskCount + " remaining tasks!");
            line();
        } else {
            System.out.println("please use the correct format for deadline");
        }
    }

    public static void addEventTask (String input) {
        String[] parts = input.split("/from", 2);
        if (parts.length == 2) {
            String description = parts[0].substring(6);
            String [] timeline = parts[1].split("/to", 2);

            if (timeline.length == 2) {
                String startTime = timeline[0];
                String endTime = timeline[1];

                Task task = new Event(description, startTime, endTime);
                tasks[taskCount] = task;
                taskCount++;
                line();
                System.out.println("okay! ive added the event task for u! quack!");
                System.out.println(" " + task.toString());
                System.out.println("now you have " + taskCount + " remaining tasks!");
                line();
            } else {
                System.out.println("please use correct format for event task!");
            }
        } else {
            System.out.println("please use correct format for event task!");
        }
    }
}
