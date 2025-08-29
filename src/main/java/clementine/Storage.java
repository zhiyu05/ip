package clementine;

import clementine.task.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;


public class Storage {
    private String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

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
        return lines;
    }

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

    private void writeToFile(String content) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        fw.write(content);
        fw.close();
    }
}
