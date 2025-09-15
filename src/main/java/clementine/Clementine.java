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
    private CommandProcessor commandProcessor;

    /**
     * Constructs a new Clementine application instance with the specified file path for data storage.
     * Initializes all components and attempts to load existing tasks from storage.
     * If loading fails, starts with an empty task list and displays the error to the user.
     * @param filePath the path to the file where tasks will be stored and loaded from
     */
    public Clementine(String filePath) {
        initialiseComponents(filePath);
        loadTasksFromStorage();
    }

    public UI getUI() {
        return this.ui;
    }

    private void initialiseComponents(String filePath) {
        ui = new UI();
        storage = new Storage(filePath);
        commandProcessor = new CommandProcessor(ui, storage);

        assert ui != null : "UI should be initialised";
        assert storage != null : "Storage should be initialised";
    }

    private void loadTasksFromStorage() {
        try {
            ArrayList<String> fileLines = storage.load();
            assert fileLines != null : "Storage.load() should return non-null list";
            ArrayList<Task> parsedTasks = parseTasksFromLines(fileLines);
            tasks = new TaskList(parsedTasks);
        } catch (ClementineException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }
        assert tasks != null : "TaskList should be initialised (either loaded or empty)";
    }

    private ArrayList<Task> parseTasksFromLines(ArrayList<String> fileLines) throws ClementineException {
        ArrayList<Task> parsedTasks = new ArrayList<>();
        for (String line : fileLines) {
            parsedTasks.add(Parser.parseTask(line));
        }
        return parsedTasks;
    }

    public String getResponse(String input) {
        if (!isValidInput(input)) {
            return "Quack! Please enter a command!";
        }

        String trimmedInput = input.trim();

        if (isByeCommand(trimmedInput)) {
            return ui.showOutro();
        }
        return processCommand(trimmedInput);
    }

    private boolean isValidInput(String input) {
        return input != null && !input.trim().isEmpty();
    }

    private boolean isByeCommand(String input) {
        return input.equals("bye");
    }

    private String processCommand(String input) {
        try {
            return commandProcessor.executeCommand(input, tasks);
        } catch (ClementineException e) {
            return ui.showError(e.getMessage());
        }
    }

    public void run() throws ClementineException {
        ui.showIntro();
        while (true) {
            try {
                String input = ui.readCommand();

                if (isByeCommand(input)) {
                    ui.showOutro();
                    break;
                }
                commandProcessor.executeCommand(input, tasks);
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
