package clementine.task;
public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public int getCompletion() {
        return (isDone ? 1 : 0);
    }

    public String toString() {
        return "[" + getStatusIcon() + "] " + this.description;
    }

    public void taskDone() {
        this.isDone = true;
    }

    public void taskUndone() {
        this.isDone = false;
    }

    public String storeData() {
        return getCompletion() + " | " + this.description;
    }

}

