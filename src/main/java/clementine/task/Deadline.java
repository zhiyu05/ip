package clementine.task;

import clementine.Parser;

import java.time.LocalDateTime;

public class Deadline extends Task {
    protected LocalDateTime by;

    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        String formattedDate = Parser.formatDateTime(by);
        return "[D]" + super.toString() + " (by:" + formattedDate + ")";
    }

    @Override
    public String storeData() {
        String dateString = Parser.formatDateTimeForStorage(by);
        return "D | " + super.storeData() + " | /by " + dateString;
    }
}
