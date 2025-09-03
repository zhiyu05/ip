package clementine;

import java.util.ArrayList;

import clementine.task.Task;


/**
 * The main Clementine class that serves as the entry point and orchestrator for the chatbot.
 * This class coordinates interactions between the UI, Storage, TaskList, and Parser components
 * to provide a complete task management experience. It handles the main application loop,
 * command processing, and error management.
 *
 * @author zhiyu
 */
public class Clementine {

    private Storage storage;
    private TaskList tasks;
    private UI ui;

    /**
     * Constructs a new Clementine application instance with the specified file path for data storage.
     * Initializes all components and attempts to load existing tasks from storage.
     * If loading fails, starts with an empty task list and displays the error to the user.
     * @param filePath the path to the file where tasks will be stored and loaded from
     */
    public Clementine(String filePath) {
        ui = new UI();
        storage = new Storage(filePath);
        try {
            ArrayList<String> fileLines = storage.load();
            ArrayList<Task> parsedTasks = new ArrayList<>();
            for (int i = 0; i < fileLines.size(); i++) {
                parsedTasks.add(Parser.parseTask(fileLines.get(i)));
            }
            tasks = new TaskList(parsedTasks);
        } catch (ClementineException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main application loop.
     * Displays the intro message, processes user commands until "bye" is entered,
     * and handles all command types including task creation, modification, and deletion.
     * Automatically saves changes to storage after each operation.
     *
     * @throws ClementineException if there's a critical error during application execution
     */
    public void run() throws ClementineException {
        ui.showIntro();
        while (true) {
            try {
                String input = ui.readCommand();

                if (input.equals("bye")) {
                    ui.showOutro();
                    break;
                }

                String command = Parser.getCommandType(input);
                int taskNumber;
                switch (command) {
                case "list":
                    ui.showTaskList(tasks.getTaskList());
                    break;
                case "find":
                    String keyword = Parser.parseFindKeyword(input);
                    ArrayList<Task> matchingTasks = tasks.findTasks(keyword);
                    ui.showFindTasks(matchingTasks);
                    break;
                case "mark":
                    taskNumber = Parser.parseTaskNumber(input, "mark");
                    Task markTask = tasks.getTask(taskNumber);
                    tasks.markTask(taskNumber);
                    storage.save(tasks.getTaskList());
                    ui.showMarkedTask(markTask);
                    break;
                case "unmark":
                    taskNumber = Parser.parseTaskNumber(input, "unmark");
                    Task unmarkTask = tasks.getTask(taskNumber);
                    tasks.unmarkTask(taskNumber);
                    storage.save(tasks.getTaskList());
                    ui.showUnmarkedTask(unmarkTask);
                    break;
                case "event":
                    Task eventTask = Parser.parseEventTask(input);
                    tasks.addTask(eventTask);
                    storage.save(tasks.getTaskList());
                    ui.showTaskAdded(eventTask, tasks.taskSize(), "event");
                    break;
                case "deadline":
                    Task deadlineTask = Parser.parseDeadlineTask(input);
                    tasks.addTask(deadlineTask);
                    storage.save(tasks.getTaskList());
                    ui.showTaskAdded(deadlineTask, tasks.taskSize(), "deadline");
                    break;
                case "todo":
                    Task todoTask = Parser.parseTodoTask(input);
                    tasks.addTask(todoTask);
                    storage.save(tasks.getTaskList());
                    ui.showTaskAdded(todoTask, tasks.taskSize(), "todo");
                    break;
                case "delete":
                    taskNumber = Parser.parseTaskNumber(input, "delete");
                    Task deleteTask = tasks.getTask(taskNumber);
                    tasks.deleteTask(taskNumber);
                    storage.save(tasks.getTaskList());
                    ui.showDeletedTask(deleteTask, tasks.taskSize());
                    break;
                default:
                    ui.showError("oh quack! i don't understand this command!");
                }
            } catch (ClementineException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.closeScanner();
    }

    /**
     * The main entry point for the Clementine task management application.
     * Creates a new Clementine instance with the default data file path and starts the application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            new Clementine("./data/clementine.txt").run();
        } catch (ClementineException e) {
            System.out.println("quack! something went wrong: " + e.getMessage());
        }
    }
}
