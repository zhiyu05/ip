package clementine.task;
public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String storeData() {
        return "T | " + super.storeData();
    }
}
