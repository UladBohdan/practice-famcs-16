package by.bsu.famcs.uladbohdan.messages;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

class Message implements Comparable<Message> {

    private String id;
    private String author;
    private Long timestamp;
    private String message;

    public Message() {
        id = UUID.randomUUID().toString();
        this.message = "?";
        this.author = "?";
        timestamp = System.currentTimeMillis();
    }

    public Message(String author, String message) {
        id = UUID.randomUUID().toString();
        this.message = message;
        this.author = author;
        timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public Long getTimeMillis() {
        return timestamp;
    }

    public LocalDateTime getTime() {
        Date date = new Date(timestamp);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public int compareTo(Message o) {
        int cmp = timestamp.compareTo(o.getTimeMillis());
        if (cmp != 0)
            return cmp;
        cmp = author.compareTo(o.getAuthor());
        if (cmp != 0)
            return cmp;
        cmp = message.compareTo(o.getMessage());
        if (cmp != 0)
            return cmp;
        return id.compareTo(o.getId());
    }

    @Override
    public String toString() {
        return id + " " + author + " " + timestamp + " " + message;
    }

    public String getFormattedMessage() {
        return String.format("%s %15s : %s", getTime().toString(), author, message);
    }
}