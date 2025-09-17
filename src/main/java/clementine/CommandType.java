package clementine;

/**
 * Enumeration of all supported command types in the Clementine task management application.
 * Each enum constant represents a specific user command that can be executed by the system.
 * This enum provides type-safe command identification and includes utility methods for
 * converting string input to the corresponding CommandType.
 *
 * @author zhiyu
 */
public enum CommandType {
    LIST,
    MARK,
    UNMARK,
    TODO,
    DEADLINE,
    EVENT,
    DELETE,
    FIND,
    PRIORITY;

    /**
     * Converts a string command input to the corresponding CommandType enum constant.
     * The conversion is case-insensitive, allowing users to enter commands in any case.
     *
     * @param command the string representation of the command (case-insensitive)
     * @return the corresponding CommandType enum constant
     * @throws ClementineException if the command string does not match any known command type
     */
    public static CommandType fromString(String command) throws ClementineException {
        switch (command.toLowerCase()) {
        case "list":
            return LIST;
        case "mark":
            return MARK;
        case "unmark":
            return UNMARK;
        case "todo":
            return TODO;
        case "deadline":
            return DEADLINE;
        case "event":
            return EVENT;
        case "delete":
            return DELETE;
        case "find":
            return FIND;
        case "priority":
            return PRIORITY;
        default:
            throw new ClementineException("quack quack! i don't recognise this word!");
        }
    }
}
