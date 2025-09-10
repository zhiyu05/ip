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

        assert ui != null : "UI should be initialised";
        assert storage != null : "Storage should be initialised";
        try {
            ArrayList<String> fileLines = storage.load();
            assert fileLines != null : "Storage.load() should return non-null list";
            ArrayList<Task> parsedTasks = new ArrayList<>();
            for (int i = 0; i < fileLines.size(); i++) {
                parsedTasks.add(Parser.parseTask(fileLines.get(i)));
            }
            tasks = new TaskList(parsedTasks);
        } catch (ClementineException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }
        assert tasks != null : "TaskList should be initialised (either loaded or empty)";
    }

    /**
     * Processes a single command and returns the response to be displayed in the GUI.
     * This replaces the continuous loop from the CLI version
     */
    public String getResponse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Please enter a command!";
        }
        input = input.trim();

        if (input.equals("bye")) {
            return "Bye! Hope to see you again soon!";
        }

        try {
            String command = Parser.getCommandType(input);
            assert tasks != null : "TaskList should be initialised";
            assert storage != null : "Storage should be initialised";
            assert ui != null : "UI should be initialised";
            int taskNumber;

            switch(command) {
            case "list":
                return ui.showTaskList(tasks.getTaskList());
            case "find":
                String keyword = Parser.parseFindKeyword(input);
                ArrayList<Task> matchingTasks = tasks.findTasks(keyword);
                return ui.showFindTasks(matchingTasks);
            case "mark":
                taskNumber = Parser.parseTaskNumber(input, "mark");
                Task markTask = tasks.getTask(taskNumber);
                assert markTask != null : "Retrieved task should not be null";
                tasks.markTask(taskNumber);
                assert markTask.isDone() : "Task should be marked as done after marking";
                storage.save(tasks.getTaskList());
                return ui.showMarkedTask(markTask);
            case "unmark":
                taskNumber = Parser.parseTaskNumber(input, "unmark");
                Task unmarkTask = tasks.getTask(taskNumber);
                assert unmarkTask != null : "Retrieved task should not be null";
                tasks.unmarkTask(taskNumber);
                assert !unmarkTask.isDone() : "Task should be unmarked after unmarking";
                storage.save(tasks.getTaskList());
                return ui.showUnmarkedTask(unmarkTask);
            case "event":
                Task eventTask = Parser.parseEventTask(input);
                assert eventTask != null : "Parser should create valid event task";
                int sizeBeforeAdd = tasks.taskSize();
                tasks.addTask(eventTask);
                assert tasks.taskSize() == sizeBeforeAdd + 1 : "Task count should increase";
                storage.save(tasks.getTaskList());
                return ui.showTaskAdded(eventTask, tasks.taskSize(), "event");
            case "deadline":
                Task deadlineTask = Parser.parseDeadlineTask(input);
                assert deadlineTask != null : "Parser should create valid deadline task";
                int sizeBeforeAddDeadline = tasks.taskSize();
                tasks.addTask(deadlineTask);
                assert tasks.taskSize() == sizeBeforeAddDeadline + 1 : "Task count should increase";
                storage.save(tasks.getTaskList());
                return ui.showTaskAdded(deadlineTask, tasks.taskSize(), "deadline");
            case "todo":
                Task todoTask = Parser.parseTodoTask(input);
                assert todoTask != null : "Parser should create valid todo task";
                int sizeBeforeAddTodo = tasks.taskSize();
                tasks.addTask(todoTask);
                assert tasks.taskSize() == sizeBeforeAddTodo + 1 : "Task count should increase";
                storage.save(tasks.getTaskList());
                return ui.showTaskAdded(todoTask, tasks.taskSize(), "todo");
            case "delete":
                taskNumber = Parser.parseTaskNumber(input, "delete");
                Task deleteTask = tasks.getTask(taskNumber);
                assert deleteTask != null : "Retrieved task should not be null";
                int sizeBeforeDelete = tasks.taskSize();
                tasks.deleteTask(taskNumber);
                assert tasks.taskSize() == sizeBeforeDelete - 1 : "Task count should decrease";
                storage.save(tasks.getTaskList());
                return ui.showDeletedTask(deleteTask, tasks.taskSize());
            default:
                return ui.showError("oh quack! i don't understand this command!");
            }
        } catch (ClementineException e) {
            return ui.showError(e.getMessage());
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
        assert args != null : "Command line arguments should not be null";
        try {
            Clementine app = new Clementine("./data/clementine.txt");
            assert app != null : "Clementine instance should be created";
            app.run();
        } catch (ClementineException e) {
            System.out.println("quack! something went wrong: " + e.getMessage());
        }
    }
}
