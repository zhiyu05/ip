package clementine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clementine.task.Deadline;
import clementine.task.Event;
import clementine.task.Task;
import clementine.task.Todo;

public class StorageTest {
    // used claude to generate more test cases by feeding it my storage class
    @TempDir
    Path tempDir;

    private Storage storage;
    private File testFile;

    @BeforeEach
    public void setUp() {
        testFile = tempDir.resolve("tasks.txt").toFile();
        storage = new Storage(testFile.getAbsolutePath());
    }

    // Load tests
    @Test
    public void load_emptyFile_returnsEmptyList() throws ClementineException, IOException {
        // Create empty file
        testFile.createNewFile();

        ArrayList<String> tasks = storage.load();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void load_fileWithEmptyLines_skipsEmptyLines() throws ClementineException, IOException {
        // Create file with some empty lines
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("T | 0 | task 1\n");
            writer.write("\n"); // Empty line
            writer.write("   \n"); // Whitespace only line
            writer.write("T | 0 | task 2\n");
            writer.write("\n"); // Another empty line
        }

        ArrayList<String> tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("T | 0 | task 1", tasks.get(0));
        assertEquals("T | 0 | task 2", tasks.get(1));
    }

    @Test
    public void load_validTaskData_returnsCorrectLines() throws ClementineException, IOException {
        // Create file with valid task data
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("T | 0 | finish homework\n");
            writer.write("D | 1 | submit project | /by 31/12/2024 2359\n");
            writer.write("E | 0 | team meeting | /from 15/1/2025 1400 /to 15/1/2025 1600\n");
        }

        ArrayList<String> tasks = storage.load();

        assertEquals(3, tasks.size());
        assertEquals("T | 0 | finish homework", tasks.get(0));
        assertEquals("D | 1 | submit project | /by 31/12/2024 2359", tasks.get(1));
        assertEquals("E | 0 | team meeting | /from 15/1/2025 1400 /to 15/1/2025 1600", tasks.get(2));
    }

    @Test
    public void load_nonExistentFile_returnsEmptyList() throws ClementineException {
        File file = tempDir.resolve("nonexistent.txt").toFile();
        Storage storage = new Storage(file.getAbsolutePath());

        ArrayList<String> tasks = storage.load();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void saveAndLoad_validTasks_success() throws ClementineException {
        File file = tempDir.resolve("tasks.txt").toFile();
        Storage storage = new Storage(file.getAbsolutePath());

        ArrayList<Task> tasksToSave = new ArrayList<>();
        tasksToSave.add(new Todo("eat dinner with friends"));

        storage.save(tasksToSave);

        ArrayList<String> lines = storage.load();

        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("eat dinner with friends"));
    }

    // Save tests
    @Test
    public void save_emptyTaskList_createsEmptyFile() throws ClementineException, IOException {
        ArrayList<Task> emptyTasks = new ArrayList<>();

        storage.save(emptyTasks);

        assertTrue(testFile.exists());
        assertEquals(0, testFile.length()); // File should be empty
    }

    @Test
    public void save_singleTodo_success() throws ClementineException, IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("test task"));

        storage.save(tasks);

        assertTrue(testFile.exists());

        // Verify content by loading back
        ArrayList<String> loadedLines = storage.load();
        assertEquals(1, loadedLines.size());
        assertTrue(loadedLines.get(0).contains("test task"));
    }

    @Test
    public void save_multipleDifferentTasks_success() throws ClementineException, IOException {
        ArrayList<Task> tasks = new ArrayList<>();

        Todo todo = new Todo("finish homework");
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2024, 12, 31, 23, 59));
        Event event = new Event("team meeting",
                LocalDateTime.of(2025, 1, 15, 14, 0),
                LocalDateTime.of(2025, 1, 15, 16, 0));
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.save(tasks);

        ArrayList<String> loadedLines = storage.load();
        assertEquals(3, loadedLines.size());
        assertTrue(loadedLines.get(0).contains("finish homework"));
        assertTrue(loadedLines.get(1).contains("submit report"));
        assertTrue(loadedLines.get(2).contains("team meeting"));

    }
}
