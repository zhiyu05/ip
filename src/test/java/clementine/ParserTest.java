package clementine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import clementine.task.Deadline;
import clementine.task.Event;
import clementine.task.Task;
import clementine.task.Todo;
public class ParserTest {
    // used claude to generate more test cases by feeding it my parser class
    @Test
    public void dummyTest() {
        assertEquals(2, 2);
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
            assertEquals("oh quack! i can't read empty commands!", e.getMessage());
        }
    }

    @Test
    public void getCommandType_nullInput() {
        try {
            Parser.getCommandType(null);
            fail();
        } catch (ClementineException e) {
            assertEquals("oh quack! i can't read empty commands!", e.getMessage());
        }
    }

    @Test
    public void getCommandType_whitespaceOnly() {
        try {
            Parser.getCommandType("   ");
            fail();
        } catch (ClementineException e) {
            assertEquals("oh quack! i can't read empty commands!", e.getMessage());
        }
    }

    @Test
    public void getCommandType_validCommands() throws ClementineException {
        assertEquals(CommandType.LIST, Parser.getCommandType("list"));
        assertEquals(CommandType.MARK, Parser.getCommandType("mark 1"));
        assertEquals(CommandType.UNMARK, Parser.getCommandType("unmark 2"));
        assertEquals(CommandType.DELETE, Parser.getCommandType("delete 3"));
        assertEquals(CommandType.TODO, Parser.getCommandType("todo homework"));
        assertEquals(CommandType.DEADLINE, Parser.getCommandType("deadline project /by 12/12/2024"));
        assertEquals(CommandType.EVENT, Parser.getCommandType("event meeting /from 1/1/2024 /to 2/1/2024"));
        assertEquals(CommandType.FIND, Parser.getCommandType("find keyword"));
        assertEquals(CommandType.PRIORITY, Parser.getCommandType("priority"));
    }

    @Test
    public void parsePriority_validPriority() throws ClementineException {
        Priority priority = Parser.parsePriority("todo task /priority 1");
        assertNotNull(priority);
        assertEquals(1, priority.getLevel());
    }

    @Test
    public void parsePriority_noPriorityKeyword() throws ClementineException {
        Priority priority = Parser.parsePriority("todo task");
        assertNull(priority);
    }

    @Test
    public void parsePriority_invalidNumber() {
        assertThrows(ClementineException.class, () -> {
            Parser.parsePriority("todo task /priority abc");
        });
    }

    @Test
    public void parsePriority_negativePriority() {
        assertThrows(ClementineException.class, () -> {
            Parser.parsePriority("todo task /priority -1");
        });
    }

    @Test
    public void parsePriority_zeroPriority() {
        assertThrows(ClementineException.class, () -> {
            Parser.parsePriority("todo task /priority 0");
        });
    }

    @Test
    public void parseTask_validTodo() {
        Task task = Parser.parseTask("T | 0 | finish ip project");
        assertNotNull(task);
        assertEquals("finish ip project", task.getDescription());
        assertEquals(" ", task.getStatusIcon());
        assertFalse(task.isDone());
    }

    @Test
    public void getCommandType_success() throws ClementineException {
        assertEquals(CommandType.UNMARK, Parser.getCommandType("unmark 2"));
    }

    @Test
    public void parseTask_validTodoMarkedDone() {
        Task task = Parser.parseTask("T | 1 | finish ip project");
        assertNotNull(task);
        assertEquals("finish ip project", task.getDescription());
        assertEquals("X", task.getStatusIcon());
        assertTrue(task.isDone());
    }

    @Test
    public void parseTask_validDeadline() {
        Task task = Parser.parseTask("D | 0 | submit assignment | /by 12/12/2024 2359");
        assertNotNull(task);
        assertEquals("submit assignment", task.getDescription());
        assertFalse(task.isDone());
        assertTrue(task instanceof Deadline);
    }

    @Test
    public void parseTask_validDeadlineWithPriority() {
        Task task = Parser.parseTask("D | 0 | submit assignment | /by 12/12/2024 2359 | 2");
        assertNotNull(task);
        assertEquals("submit assignment", task.getDescription());
        assertTrue(task.hasPriority());
        assertEquals(2, task.getPriority().getLevel());
    }

    @Test
    public void parseTask_validEventFromStorage() {
        Task task = Parser.parseTask("E | 0 | meeting | /from 12/12/2024 1400 /to 12/12/2024 1600");
        assertNotNull(task);
        assertEquals("meeting", task.getDescription());
        assertFalse(task.isDone());
        assertTrue(task instanceof Event);
    }

    @Test
    public void parseTask_validEventWithPriority() {
        Task task = Parser.parseTask("E | 0 | meeting | /from 12/12/2024 1400 /to 12/12/2024 1600 | 3");
        assertNotNull(task);
        assertEquals("meeting", task.getDescription());
        assertTrue(task.hasPriority());
        assertEquals(3, task.getPriority().getLevel());
    }

    @Test
    public void parseTask_invalidFormat() {
        Task task = Parser.parseTask("invalid format");
        assertNull(task);
    }

    @Test
    public void parseTask_unknownTaskType() {
        Task task = Parser.parseTask("X | 0 | unknown task");
        assertNull(task);
    }

    @Test
    public void parseTask_insufficientParts() {
        Task task = Parser.parseTask("T | 0");
        assertNull(task);
    }

    @Test
    public void parseTaskNumber_validNumber() throws ClementineException {
        int result = Parser.parseTaskNumber("mark 5", "mark");
        assertEquals(5, result);
    }

    @Test
    public void parseTaskNumber_invalidNumber() {
        assertThrows(ClementineException.class, () -> {
            Parser.parseTaskNumber("mark abc", "mark");
        });
    }

    @Test
    public void parseTaskNumber_noNumber() {
        assertThrows(ClementineException.class, () -> {
            Parser.parseTaskNumber("mark", "mark");
        });
    }

    @Test
    public void parseDeadlineTask_validDeadlineWithPriority() throws ClementineException {
        Deadline deadline = Parser.parseDeadlineTask("deadline submit report /by 12/12/2024 1400 /priority 1");
        assertEquals("submit report", deadline.getDescription());
        assertTrue(deadline.hasPriority());
        assertEquals(1, deadline.getPriority().getLevel());
    }

    @Test
    public void parseDeadlineTask_emptyDescription() {
        assertThrows(ClementineException.class, () -> {
            Parser.parseDeadlineTask("deadline");
        });
    }

    @Test
    public void parseDeadlineTask_noByKeyword() {
        assertThrows(ClementineException.class, () -> {
            Parser.parseDeadlineTask("deadline submit report 12/12/2024");
        });
    }

    @Test
    public void parseDeadlineTask_emptyDeadline() {
        assertThrows(ClementineException.class, () -> {
            Parser.parseDeadlineTask("deadline submit report /by");
        });
    }

    @Test
    public void parseDeadlineTask_invalidDate() {
        assertThrows(ClementineException.class, () -> {
            Parser.parseDeadlineTask("deadline submit report /by invalid-date");
        });
    }

    // Event parsing tests (additional)
    @Test
    public void parseEventTask_validEventWithPriority() throws ClementineException {
        Event event = Parser.parseEventTask("event meeting /from 19/8/2025 1400 /to 25/8/2025 1400 /priority 2");
        assertEquals("meeting", event.getDescription());
        assertTrue(event.hasPriority());
        assertEquals(2, event.getPriority().getLevel());
    }

    @Test
    public void parseEventTask_emptyDescription() {
        assertThrows(ClementineException.class, () -> {
            Parser.parseEventTask("event");
        });
    }

    @Test
    public void parseEventTask_missingTo() {
        assertThrows(ClementineException.class, () -> {
            Parser.parseEventTask("event meeting /from 29/9/2025 1900");
        });
    }

    @Test
    public void parseEventTask_invalidStartTime() {
        assertThrows(ClementineException.class, () -> {
            Parser.parseEventTask("event meeting /from invalid /to 29/9/2025 1900");
        });
    }

    @Test
    public void parseEventTask_invalidEndTime() {
        assertThrows(ClementineException.class, () -> {
            Parser.parseEventTask("event meeting /from 29/9/2025 1900 /to invalid");
        });
    }

    @Test
    public void parseTodoTask_validTodoWithPriority() throws ClementineException {
        Todo todo = Parser.parseTodoTask("todo finish homework /priority 3");
        assertEquals("finish homework", todo.getDescription());
        assertTrue(todo.hasPriority());
        assertEquals(3, todo.getPriority().getLevel());
    }

    @Test
    public void parseTodoTask_whitespaceOnlyDescription() {
        assertThrows(ClementineException.class, () -> {
            Parser.parseTodoTask("todo    ");
        });
    }

    @Test
    public void parseDateTime_validFormats() throws DateTimeParseException {
        // Test various date-time formats
        LocalDateTime result1 = Parser.parseDateTime("12/12/2024 1400");
        assertEquals(12, result1.getDayOfMonth());
        assertEquals(12, result1.getMonthValue());
        assertEquals(2024, result1.getYear());
        assertEquals(14, result1.getHour());
        assertEquals(0, result1.getMinute());

        LocalDateTime result2 = Parser.parseDateTime("1/1/2024 14:30");
        assertEquals(1, result2.getDayOfMonth());
        assertEquals(1, result2.getMonthValue());
        assertEquals(2024, result2.getYear());
        assertEquals(14, result2.getHour());
        assertEquals(30, result2.getMinute());

        // Test date only (should default to start of day)
        LocalDateTime result3 = Parser.parseDateTime("15/6/2024");
        assertEquals(15, result3.getDayOfMonth());
        assertEquals(6, result3.getMonthValue());
        assertEquals(2024, result3.getYear());
        assertEquals(0, result3.getHour());
        assertEquals(0, result3.getMinute());
    }

    @Test
    public void parseDateTime_invalidFormat() {
        assertThrows(DateTimeParseException.class, () -> {
            Parser.parseDateTime("invalid-date");
        });
    }

    // DateTime formatting tests
    @Test
    public void formatDateTime_dateOnly() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 25, 0, 0);
        String result = Parser.formatDateTime(dateTime);
        assertEquals("25 Dec 2024", result);
    }

    @Test
    public void formatDateTime_dateAndTime() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 25, 14, 30);
        String result = Parser.formatDateTime(dateTime);
        assertEquals("25 Dec 2024 2:30 pm", result);
    }

    @Test
    public void formatDateTimeForStorage_valid() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 25, 14, 30);
        String result = Parser.formatDateTimeForStorage(dateTime);
        assertEquals("25/12/2024 1430", result);
    }

    // Find keyword parsing tests
    @Test
    public void parseFindKeyword_validKeyword() throws ClementineException {
        String result = Parser.parseFindKeyword("find homework");
        assertEquals("homework", result);
    }

    @Test
    public void parseFindKeyword_keywordWithSpaces() throws ClementineException {
        String result = Parser.parseFindKeyword("find project meeting");
        assertEquals("project meeting", result);
    }

    @Test
    public void parseFindKeyword_emptyKeyword() {
        assertThrows(ClementineException.class, () -> {
            Parser.parseFindKeyword("find");
        });
    }

    @Test
    public void parseFindKeyword_whitespaceOnlyKeyword() {
        assertThrows(ClementineException.class, () -> {
            Parser.parseFindKeyword("find   ");
        });
    }


    @Test
    public void parseTask_validTodoWithPriority() {
        Task task = Parser.parseTask("T | 0 | finish ip project | 1");
        assertNotNull(task);
        assertEquals("finish ip project", task.getDescription());
        assertTrue(task.hasPriority());
        assertEquals(1, task.getPriority().getLevel());
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
