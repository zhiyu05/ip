package clementine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import clementine.task.Task;

/**
 * The Storage class handles persistent storage operations for the Clementine chatbot
 * This class provides functionality to save tasks to and load tasks from a file,
 * ensuring data persistence between application sessions.
 *
 * @author zhiyu
 */
public class Storage {
    private String filePath;

    /**
     * Constructs a Stroage object with the specified file path.
     * @param filePath the path to the file where tasks will be stored
     */
    public Storage(String filePath) {
        this.filePath = filePath;
        assert this.filePath != null : "File path should be initialised";
    }

    /**
     * Loads task data from the storage file.
     * Returns an empty list if the file does not exist or is empty.
     * Skips empty lines during loading.
     * @return an ArrayList of String objects representing the raw task data from the file
     * @throws ClementineException if there's an error reading from the file (excluding FileNotFoundException)
     */
    public ArrayList<String> load() throws ClementineException {
        ArrayList<String> lines = new ArrayList<>();
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return lines;
            }

            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            // File not found on first run. Safe to ignore as a new file will be created when saving.
        } catch (Exception e) {
            throw new ClementineException("quack! error loading tasks: " + e.getMessage());
        }
        assert lines != null : "Loaded lines should not be null";
        return lines;
    }

    /**
     * Saves the current list of tasks of the storage file.
     * Creates the parent directory if it doesn't exist.
     * Overwrites any existing file content.
     * @param tasks an ArrayList of Task objects to save to the file
     * @throws ClementineException if there's an error writing to the file
     */
    public void save(ArrayList<Task> tasks) throws ClementineException {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            StringBuilder content = new StringBuilder();
            for (Task task : tasks) {
                content.append(task.storeData()).append("\n");
            }
            writeToFile(content.toString());
        } catch (IOException e) {
            throw new ClementineException("quack! error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Writes the given content string to the file specified by filePath.
     * @param content the string content to write to the file
     * @throws IOException if there's an error writing to the file
     */
    private void writeToFile(String content) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        fw.write(content);
        fw.close();
    }
}
