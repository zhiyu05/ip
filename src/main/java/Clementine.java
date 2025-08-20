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
            } else if (input.startsWith("unmark ")){
                unmarkTask(input);
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
        tasks[taskCount] = new Task(taskDescription);
        String response = "okay! added: " + taskDescription + " quack!";
        taskCount++;
        line();
        System.out.println(response);
        line();
    }

    public static void listTasks () {
        String response = "tasks to be done before freedom:\n";
        for (int i = 1; i <= taskCount; i++) {
            response += i + ". " + tasks[i - 1].printTask() + "\n";
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
                System.out.println(" " + tasks[taskNumber - 1].printTask());
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
                System.out.println(" " + tasks[taskNumber - 1].printTask());
                line();
            } else {
                System.out.println("invalid task number");
            }
        } catch (NumberFormatException e) {
            System.out.println("please provide a valid number");
        }
    }
}
