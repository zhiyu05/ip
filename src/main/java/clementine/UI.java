package clementine;
import clementine.task.Task;

import java.util.Scanner;
import java.util.ArrayList;

public class UI {
    private Scanner scanner;

    public UI() {
        scanner = new Scanner(System.in);
    }
    public void showIntro() {
        showLine();
        System.out.println("Quack! I'm clementine\n What can i help you with today?\n");
        showLine();
    }

    public void showOutro() {
        showLine();
        System.out.println("Bye! quack u later! hope you have a great day!");
        showLine();
    }

    public void showLine() {
        System.out.println( "______________________________________________\n");
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void showError(String message) {
        showLine();
        System.out.println("OOPS!! " + message);
        showLine();
    }

    public void showTaskAdded(Task task, int totalTasks, String taskType) {
        showLine();
        switch(taskType) {
            case "deadline":
                System.out.println("okay! ive added the deadline task quack!");
                break;
            case "event":
                System.out.println("okay! ive added the event task for u! quack!");
                break;
            case "todo":
            default:
                System.out.println("okay! ive added the task quack!");
                break;
        }
        System.out.println(" " + task.toString());
        System.out.println("now you have " + totalTasks + " remaining tasks!");
        showLine();
    }

    public void showTaskList(ArrayList<Task> tasks) {
        String response = "tasks to be done before freedom:\n";
        for (int i = 0; i < tasks.size(); i++) {
            response += (i + 1) + ". " + tasks.get(i).toString() + "\n";
        }
        showLine();
        System.out.println(response);
        showLine();
    }

    public void showMarkedTask(Task task) {
        showLine();
        System.out.println("good job! you've completed the task");
        System.out.println(" " + task.toString());
        showLine();
    }

    public void showUnmarkedTask(Task task) {
        showLine();
        System.out.println("okay, ive changed this task to not done. quack!");
        System.out.println(" " + task.toString());
        showLine();
    }

    public void showDeletedTask(Task task, int totalTasks) {
        showLine();
        System.out.println("quack! ive deleted this task for you :)");
        System.out.println(" " + task.toString());
        System.out.println("now you have " + totalTasks + " remaining tasks");
        showLine();
    }

    public void showLoadingError(String errorMessage) {
        System.out.println("quack! error loading tasks: " + errorMessage);
    }

    public void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
