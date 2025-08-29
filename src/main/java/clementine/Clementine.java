package clementine;
import clementine.task.Task;

import java.util.ArrayList;


public class Clementine {

    private Storage storage;
    private TaskList tasks;
    private UI ui;

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

    public void run() throws ClementineException{
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
                }
            } catch (ClementineException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.closeScanner();
    }

    public static void main(String[] args) {
        try {
            new Clementine("./data/clementine.txt").run();
        } catch (ClementineException e) {
            System.out.println("quack! something went wrong: " + e.getMessage());
        }
    }
}
