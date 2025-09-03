package clementine;

import java.util.ArrayList;
import java.util.Scanner;

import clementine.task.Task;

/**
 * The UI class handles all user interface interactions for the Clementine chatbot.
 * This class manages output operations, displays messages to users, and formats output consistently.
 *
 * @author zhiyu
 *
 */
public class UI {
    private Scanner scanner;

    /**
     * Constructs a new UI object and initialises the Scanner for user input.
     */
    public UI() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays the introductory message when the application starts.
     * Shows a welcome message with the application name and prompts the user for input.
     */
    public void showIntro() {
        showLine();
        System.out.println("Quack! I'm clementine\n What can i help you with today?\n");
        showLine();
    }

    /**
     * Displays the farewell message when the application exits.
     * Shows a goodbye message to the user.
     */
    public void showOutro() {
        showLine();
        System.out.println("Bye! quack u later! hope you have a great day!");
        showLine();
    }

    /**
     * Prints a decorative line separator to improve visual formatting of the output
     */
    public void showLine() {
        System.out.println("______________________________________________\n");
    }

    /**
     * Reads and returns the next line of user input
     * @return the user's input as a String
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays an error message to the user in a formatted manner
     * @param message the error message to display
     */
    public void showError(String message) {
        showLine();
        System.out.println("OOPS!! " + message);
        showLine();
    }

    /**
     * Displays confirmation that a task has been successfully added.
     * Shows different messages based on the task type and includes the task details and total count.
     * @param task the Task object that was added
     * @param totalTasks the total number of tasks after adding this task
     * @param taskType the type of task ("deadline", "event" or "todo")
     */
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

    /**
     * Displays the complete list of tasks to the user
     * Each task is numbered and shown with its string representation.
     * @param tasks an ArrayList of Task objects to display
     */
    public void showTaskList(ArrayList<Task> tasks) {
        String response = "tasks to be done before freedom:\n";
        for (int i = 0; i < tasks.size(); i++) {
            response += (i + 1) + ". " + tasks.get(i).toString() + "\n";
        }
        showLine();
        System.out.println(response);
        showLine();
    }

    /**
     * Displays confirmation that a task has been marked as completed.
     * @param task the Task object that was marked as done
     */
    public void showMarkedTask(Task task) {
        showLine();
        System.out.println("good job! you've completed the task");
        System.out.println(" " + task.toString());
        showLine();
    }

    /**
     * Displays confirmation that a task has been marked as not completed.
     * @param task the Task object that was unmarked
     */
    public void showUnmarkedTask(Task task) {
        showLine();
        System.out.println("okay, ive changed this task to not done. quack!");
        System.out.println(" " + task.toString());
        showLine();
    }

    /**
     * Displays confirmtation that a task has been successfully deleted.
     * Shows the deleted task details and the updated total task count.
     * @param task the Task object that was deleted
     * @param totalTasks the total number of remaining tasks after deletion
     */
    public void showDeletedTask(Task task, int totalTasks) {
        showLine();
        System.out.println("quack! ive deleted this task for you :)");
        System.out.println(" " + task.toString());
        System.out.println("now you have " + totalTasks + " remaining tasks");
        showLine();
    }

    /**
     * Displays an error message when there's a problem loading tasks from storage.
     * @param errorMessage the specific error message describing what went wrong during loading
     */
    public void showLoadingError(String errorMessage) {
        System.out.println("quack! error loading tasks: " + errorMessage);
    }

    /**
     * Closes the Scanner resource to prevent resource leaks.
     * Should be called when the application is shutting down.
     */
    public void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }

    /**
     * Displays the given list of tasks that match a {@code find} command.
     * @param tasks the list of tasks to display
     */
    public void showFindTasks(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            showLine();
            System.out.println("oh quack! no matching tasks found!");
            showLine();
        } else {
            String response = "quack! here's the tasks u might be looking for:\n";
            for (int i = 0; i < tasks.size(); i++) {
                response += (i + 1) + ". " + tasks.get(i).toString() + "\n";
            }
            showLine();
            System.out.println(response);
            showLine();
        }
    }
}
