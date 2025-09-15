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
        assert scanner != null : "Scanner should be initialised";
    }

    /**
     * Displays the introductory message when the application starts.
     * Shows a welcome message with the application name and prompts the user for input.
     * @return the intro message
     */
    public String showIntro() {
        String response = "Quack! I'm clementine\n What can i help you with today?\n ( •з• )";
        showLine();
        System.out.println(response);
        showLine();
        return response;
    }

    /**
     * Displays the farewell message when the application exits.
     * Shows a goodbye message to the user.
     */
    public String showOutro() {
        String response = "Bye! quack u later! hope you have a great day! •᷄ɞ•";
        showLine();
        System.out.println(response);
        showLine();
        return response;
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
    public String showError(String message) {
        String response = "OOPS!! " + message;
        showLine();
        System.out.println(response);
        showLine();
        return response;
    }

    /**
     * Displays confirmation that a task has been successfully added.
     * Shows different messages based on the task type and includes the task details and total count.
     * @param task the Task object that was added
     * @param totalTasks the total number of tasks after adding this task
     * @param taskType the type of task ("deadline", "event" or "todo")
     */
    public String showTaskAdded(Task task, int totalTasks, CommandType taskType) {
        showLine();
        String response = "";
        switch(taskType) {
        case DEADLINE:
            System.out.println("okay! ive added the deadline task quack!");
            response = "okay! ive added the deadline task quack!";
            break;
        case EVENT:
            System.out.println("okay! ive added the event task for u! quack!");
            response = "okay! ive added the event task for u! quack!";
            break;
        case TODO:
        default:
            System.out.println("okay! ive added the task quack!");
            response = "okay! ive added the task quack!";
            break;
        }
        System.out.println(" " + task.toString());
        System.out.println("now you have " + totalTasks + " remaining tasks!");
        showLine();
        return response + "\n" + " " + task.toString() + "\n" + "now you have " + totalTasks + " remaining tasks!";
    }

    /**
     * Displays the complete list of tasks to the user
     * Each task is numbered and shown with its string representation.
     * @param tasks an ArrayList of Task objects to display
     */
    public String showTaskList(ArrayList<Task> tasks) {
        String response = "tasks to be done before freedom: ( •᷄ɞ•᷅ )\n";
        for (int i = 0; i < tasks.size(); i++) {
            response += (i + 1) + ". " + tasks.get(i).toString() + "\n";
        }
        showLine();
        System.out.println(response);
        showLine();
        return response;
    }

    /**
     * Displays confirmation that a task has been marked as completed.
     * @param task the Task object that was marked as done
     */
    public String showMarkedTask(Task task) {
        showLine();
        System.out.println("good job! you've completed the task! quack!");
        System.out.println(" " + task.toString());
        showLine();
        return "good job! you've completed the task! quack!" + "\n" + " " + task.toString();
    }

    /**
     * Displays confirmation that a task has been marked as not completed.
     * @param task the Task object that was unmarked
     */
    public String showUnmarkedTask(Task task) {
        showLine();
        System.out.println("okay, ive changed this task to not done. quack!");
        System.out.println(" " + task.toString());
        showLine();
        return "okay, ive changed this task to not done. quack!" + "\n" + " " + task.toString();
    }

    /**
     * Displays confirmtation that a task has been successfully deleted.
     * Shows the deleted task details and the updated total task count.
     * @param task the Task object that was deleted
     * @param totalTasks the total number of remaining tasks after deletion
     */
    public String showDeletedTask(Task task, int totalTasks) {
        showLine();
        System.out.println("quack! ive deleted this task for you :)");
        System.out.println(" " + task.toString());
        System.out.println("now you have " + totalTasks + " remaining tasks");
        showLine();
        return "quack! ive deleted this task for you :)" + "\n" + " " + task.toString() + "\n"
                + "now you have " + totalTasks + " remaining tasks";
    }

    /**
     * Displays an error message when there's a problem loading tasks from storage.
     * @param errorMessage the specific error message describing what went wrong during loading
     */
    public String showLoadingError(String errorMessage) {
        String response = "quack! error loading tasks: " + errorMessage;
        System.out.println(response);
        return response;
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
    public String showFindTasks(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            showLine();
            System.out.println("oh quack! no matching tasks found!");
            showLine();
            return "oh quack! no matching tasks found!";
        } else {
            String response = "quack! here's the tasks u might be looking for:\n";
            for (int i = 0; i < tasks.size(); i++) {
                response += (i + 1) + ". " + tasks.get(i).toString() + "\n";
            }
            showLine();
            System.out.println(response);
            showLine();
            return response;
        }
    }

    public String showPriorityTasks(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            showLine();
            System.out.println("oh quack! no tasks with priority!");
            showLine();
            return "oh quack! no tasks with priority!";
        } else {
            String response = "quack! here's the priority list:\n";
            for (int i = 0; i < tasks.size(); i++) {
                response += "[" + (i + 1) + "]" + tasks.get(i).toString() + "\n";
            }
            showLine();
            System.out.println(response);
            showLine();
            return response;
        }
    }
}
