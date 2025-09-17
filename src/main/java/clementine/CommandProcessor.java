package clementine;

import java.util.ArrayList;

import clementine.task.Task;

/**
 * The CommandProcessor class handles the execution of user commands in the Clementine chatbot.
 * This class processes different types of commands such as list, find, mark, unmark, delete,
 * and task creation commands. It coordinates between the UI, TaskList, and Storage components
 * to perform the requested operations and provide appropriate feedback to the user.
 *
 * @author zhiyu
 */
public class CommandProcessor {
    private final UI ui;
    private final Storage storage;

    /**
     * Constructs a new CommandProcessor with the specified UI and Storage components.
     *
     * @param ui the UI component for displaying messages to the user
     * @param storage the Storage component for persisting task data
     */
    public CommandProcessor(UI ui, Storage storage) {
        this.ui = ui;
        this.storage = storage;
    }

    /**
     * Executes the appropriate command based on the user input.
     * Parses the command type from the input and delegates to specific handler methods.
     *
     * @param input the user's command input string
     * @param tasks the TaskList containing all current tasks
     * @return a String response to be displayed to the user
     * @throws ClementineException if the command is invalid or execution fails
     */
    public String executeCommand(String input, TaskList tasks) throws ClementineException {
        CommandType command = Parser.getCommandType(input);

        switch(command) {
        case LIST:
            return handleListCommand(tasks);
        case FIND:
            return handleFindCommand(input, tasks);
        case MARK:
        case UNMARK:
            return handleTaskStatusCommand(command, input, tasks);
        case DELETE:
            return handleDeleteCommand(input, tasks);
        case EVENT:
        case DEADLINE:
        case TODO:
            return handleAddTaskCommand(command, input, tasks);
        case PRIORITY:
            return handlePriorityCommand(tasks);
        default:
            return ui.showError("oh quack! i don't understand this command!");
        }
    }

    /**
     * Handles the list command to display all tasks to the user.
     *
     * @param tasks the TaskList containing all current tasks
     * @return a String response showing all tasks
     */
    private String handleListCommand(TaskList tasks) {
        return ui.showTaskList(tasks.getTaskList());
    }

    /**
     * Handles the find command to search for tasks containing a specific keyword.
     *
     * @param input the user's find command input
     * @param tasks the TaskList to search through
     * @return a String response showing matching tasks
     * @throws ClementineException if the keyword is missing or invalid
     */
    private String handleFindCommand(String input, TaskList tasks) throws ClementineException {
        String keyword = Parser.parseFindKeyword(input);
        ArrayList<Task> matchingTasks = tasks.findTasks(keyword);
        return ui.showFindTasks(matchingTasks);
    }

    /**
     * Handles mark and unmark commands to change the completion status of tasks.
     *
     * @param command the CommandType (MARK or UNMARK)
     * @param input the user's command input
     * @param tasks the TaskList containing the task to modify
     * @return a String response confirming the status change
     * @throws ClementineException if the task number is invalid or out of bounds
     */
    private String handleTaskStatusCommand(CommandType command, String input, TaskList tasks)
            throws ClementineException {
        int taskNumber = Parser.parseTaskNumber(input, command.name().toLowerCase());
        Task task = tasks.getTask(taskNumber);

        if (command == CommandType.MARK) {
            tasks.markTask(taskNumber);
            saveTasksToStorage(tasks);
            return ui.showMarkedTask(task);
        } else {
            tasks.unmarkTask(taskNumber);
            saveTasksToStorage(tasks);
            return ui.showUnmarkedTask(task);
        }
    }

    /**
     * Handles the delete command to remove a task from the task list.
     *
     * @param input the user's delete command input
     * @param tasks the TaskList containing the task to delete
     * @return a String response confirming the deletion
     * @throws ClementineException if the task number is invalid or out of bounds
     */
    private String handleDeleteCommand(String input, TaskList tasks) throws ClementineException {
        int taskNumber = Parser.parseTaskNumber(input, "delete");
        Task deleteTask = tasks.getTask(taskNumber);
        tasks.deleteTask(taskNumber);
        saveTasksToStorage(tasks);
        return ui.showDeletedTask(deleteTask, tasks.taskSize());
    }

    /**
     * Handles task creation commands (todo, deadline, event) to add new tasks to the list.
     *
     * @param command the CommandType indicating the type of task to create
     * @param input the user's command input with task details
     * @param tasks the TaskList to add the new task to
     * @return a String response confirming the task addition
     * @throws ClementineException if the task format is invalid or creation fails
     */
    private String handleAddTaskCommand(CommandType command, String input, TaskList tasks)
            throws ClementineException {
        Task newTask = parseNewTask(command, input);
        tasks.addTask(newTask);
        saveTasksToStorage(tasks);
        return ui.showTaskAdded(newTask, tasks.taskSize(), command);
    }

    /**
     * Parses user input to create a new Task object based on the command type.
     *
     * @param command the CommandType indicating what type of task to create
     * @param input the user's command input string
     * @return a new Task object of the appropriate type
     * @throws ClementineException if the command type is unknown or parsing fails
     */
    private Task parseNewTask(CommandType command, String input) throws ClementineException {
        switch (command) {
        case EVENT:
            return Parser.parseEventTask(input);
        case DEADLINE:
            return Parser.parseDeadlineTask(input);
        case TODO:
            return Parser.parseTodoTask(input);
        default:
            throw new ClementineException("Unknown task type: " + command);
        }
    }

    /**
     * Saves the current task list to storage.
     *
     * @param tasks the TaskList to save to persistent storage
     * @throws ClementineException if there's an error during the save operation
     */
    private void saveTasksToStorage(TaskList tasks) throws ClementineException {
        storage.save(tasks.getTaskList());
    }

    /**
     * Handles the priority command to display tasks sorted by priority level.
     *
     * @param tasks the TaskList containing tasks to filter and sort by priority
     * @return a String response showing tasks ordered by priority
     */
    private String handlePriorityCommand(TaskList tasks) {
        return ui.showPriorityTasks(tasks.getTasksByPriority());
    }
}
