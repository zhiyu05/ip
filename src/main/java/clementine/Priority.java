package clementine;

public class Priority {
    private int level;

    public Priority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    @Override
    public String toString() {
        return String.valueOf(this.level);
    }
}
