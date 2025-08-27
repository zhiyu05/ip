import java.time.LocalDateTime;

public class Deadline extends Task {
    protected LocalDateTime by;

    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        String formattedDate = Clementine.formatDateTime(by);
        return "[D]" + super.toString() + " (by:" + formattedDate + ")";
    }

    @Override
    public String storeData() {
        String dateString = Clementine.formatDateTimeForStorage(by);
        return "D | " + super.storeData() + " | /by " + dateString;
    }
}
