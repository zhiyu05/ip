import java.time.LocalDateTime;

public class Event extends Task {
    protected LocalDateTime start;
    protected LocalDateTime end;

    public Event (String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        String started = Clementine.formatDateTime(this.start);
        String ended = Clementine.formatDateTime(this.end);
        return "[E]" + super.toString() + " (from:" + started + " to:" + ended + ")";
    }

    @Override
    public String storeData() {
        String started = Clementine.formatDateTimeForStorage(this.start);
        String ended = Clementine.formatDateTimeForStorage(this.end);
        return "E | " + super.storeData() + " | /from " + started + "/to " + ended;
    }
}
