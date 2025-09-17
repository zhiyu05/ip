package clementine;
/**
 * The Priority class represents a priority level for tasks in the Clementine application.
 * Each Priority object contains a numeric level indicating the importance of a task,
 * with lower numbers typically representing higher priority.
 *
 * @author zhiyu
 */
public class Priority {
    private int level;

    /**
     * Constructs a new Priority object with the specified priority level.
     *
     * @param level the numeric priority level (typically a positive integer)
     */
    public Priority(int level) {
        this.level = level;
    }

    /**
     * Returns the priority level of this Priority object.
     *
     * @return the numeric priority level
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Returns a string representation of this Priority object.
     *
     * @return the priority level as a string
     */
    @Override
    public String toString() {
        return String.valueOf(this.level);
    }
}
