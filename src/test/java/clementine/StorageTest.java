package clementine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clementine.task.Task;
import clementine.task.Todo;

public class StorageTest {
    @TempDir
    Path tempDir;

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
}
