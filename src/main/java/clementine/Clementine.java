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

    /**
     * Returns the UI component of this Clementine instance.
     * Provides access to the user interface for external components that need to interact with it.
     *
     * @return the UI object used by this Clementine instance
     */
    public UI getUI() {
        return this.ui;
    }

    /**
     * Initializes all core components of the Clementine application.
     * Creates instances of UI, Storage, and CommandProcessor with the specified file path.
     *
     * @param filePath the path to the file where tasks will be stored
     */
    private void initialiseComponents(String filePath) {
        ui = new UI();
        storage = new Storage(filePath);
        commandProcessor = new CommandProcessor(ui, storage);

        assert ui != null : "UI should be initialised";
        assert storage != null : "Storage should be initialised";
    }

    /**
     * Loads existing tasks from storage and initializes the task list.
     * If loading fails due to file issues or parsing errors, creates an empty task list
     * and displays an error message to the user.
     */
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

    /**
     * Parses raw file lines into Task objects.
     * Converts each line from storage into the appropriate Task type using the Parser.
     *
     * @param fileLines the list of raw task data lines from storage
     * @return an ArrayList of parsed Task objects
     * @throws ClementineException if any line cannot be parsed into a valid task
     */
    private ArrayList<Task> parseTasksFromLines(ArrayList<String> fileLines) throws ClementineException {
        ArrayList<Task> parsedTasks = new ArrayList<>();
        for (String line : fileLines) {
            parsedTasks.add(Parser.parseTask(line));
        }
        return parsedTasks;
    }

    /**
     * Processes user input and returns an appropriate response.
     * Validates the input, handles special commands like "bye", and delegates
     * other commands to the CommandProcessor for execution.
     *
     * @param input the user's command input
     * @return a response string to be displayed to the user
     */

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

    /**
     * Validates whether the user input is acceptable for processing.
     * Checks that input is not null and contains non-whitespace characters.
     *
     * @param input the user input to validate
     * @return true if input is valid, false if null or empty/whitespace-only
     */
    private boolean isValidInput(String input) {
        return input != null && !input.trim().isEmpty();
    }

    /**
     * Checks if the given input is the "bye" command to exit the application.
     *
     * @param input the trimmed user input to check
     * @return true if the input equals "bye", false otherwise
     */
    private boolean isByeCommand(String input) {
        return input.equals("bye");
    }

    /**
     * Processes a valid command through the CommandProcessor.
     * Handles any exceptions that occur during command execution and
     * returns appropriate error messages to the user.
     *
     * @param input the validated and trimmed user command
     * @return the response from command execution or an error message
     */
    private String processCommand(String input) {
        try {
            return commandProcessor.executeCommand(input, tasks);
        } catch (ClementineException e) {
            return ui.showError(e.getMessage());
        }
    }

    /**
     * Runs the main application loop for console-based interaction.
     * Displays the introduction, continuously reads user commands,
     * and processes them until the user enters "bye" to exit.
     *
     * @throws ClementineException if a critical error occurs during application execution
     */
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
