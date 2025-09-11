package clementine;

public enum CommandType {
    LIST,
    MARK,
    UNMARK,
    TODO,
    DEADLINE,
    EVENT,
    DELETE,
    FIND;

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
        default:
            throw new ClementineException("quack quack! i don't recognise this word!");
        }
    }
}
