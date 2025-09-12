package clementine;

import java.util.ArrayList;

import clementine.task.Task;

public class CommandProcessor {
    private final UI ui;
    private final Storage storage;

    public CommandProcessor(UI ui, Storage storage) {
        this.ui = ui;
        this.storage = storage;
    }

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

    private String handleListCommand(TaskList tasks) {
        return ui.showTaskList(tasks.getTaskList());
    }

    private String handleFindCommand(String input, TaskList tasks) throws ClementineException {
        String keyword = Parser.parseFindKeyword(input);
        ArrayList<Task> matchingTasks = tasks.findTasks(keyword);
        return ui.showFindTasks(matchingTasks);
    }

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

    private String handleDeleteCommand(String input, TaskList tasks) throws ClementineException {
        int taskNumber = Parser.parseTaskNumber(input, "delete");
        Task deleteTask = tasks.getTask(taskNumber);
        tasks.deleteTask(taskNumber);
        saveTasksToStorage(tasks);
        return ui.showDeletedTask(deleteTask, tasks.taskSize());
    }

    private String handleAddTaskCommand(CommandType command, String input, TaskList tasks)
            throws ClementineException {
        Task newTask = parseNewTask(command, input);
        tasks.addTask(newTask);
        saveTasksToStorage(tasks);
        return ui.showTaskAdded(newTask, tasks.taskSize(), command);
    }

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

    private void saveTasksToStorage(TaskList tasks) throws ClementineException {
        storage.save(tasks.getTaskList());
    }

    private String handlePriorityCommand(TaskList tasks) {
        return ui.showPriorityTasks(tasks.getTasksByPriority());
    }
}
