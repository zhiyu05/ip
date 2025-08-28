package clementine;
import clementine.task.Event;
import clementine.task.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {
    @Test
    public void dummyTest() {
        assertEquals(2,2);
    }

    @Test
    public void getCommandType_exceptionThrown() {
        try {
            assertEquals("test", Parser.getCommandType("hi"));
            fail();
        } catch (ClementineException e) {
            assertEquals("quack quack! i don't recognise this word!", e.getMessage());
        }
    }

    @Test
    public void getCommandType_emptyInput() {
        try {
            assertEquals("test", Parser.getCommandType(""));
            fail();
        } catch (ClementineException e) {
            assertEquals("oh quack! i cant read empty commands!", e.getMessage());
        }
    }

    @Test
    public void getCommandType_success() throws ClementineException{
        assertEquals("unmark", Parser.getCommandType("unmark 2"));
    }

    @Test
    public void parseTask_validTodo() {
        Task task = Parser.parseTask("T | 0 | finish ip project");
        assertNotNull(task);
        assertEquals("finish ip project", task.getDescription());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void parseEventTask_missingFrom() {
        try {
            Parser.parseEventTask("event meeting /to 29/9/2025 1900");
            fail();
        } catch (ClementineException e) {
            assertEquals("quack! please use the format: event <description> /from <time> /to <time>", e.getMessage());
        }
    }

    @Test
    public void parseEventTask_emptyTimes() {
        try {
            Parser.parseEventTask("event meeting /from /to");
            fail();
        } catch (ClementineException e) {
            assertEquals("both start time and end time must be specified!", e.getMessage());
        }
    }

    @Test
    public void parseEventTask_validEvent() throws ClementineException {
        Event event = Parser.parseEventTask("event meeting /from 19/8/2025 1400 /to 25/8/2025 1400");
        assertEquals("meeting", event.getDescription());
    }

}
