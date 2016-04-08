package by.bsu.famcs.uladbohdan.messages;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

public class Message implements Comparable<Message> {

    private String id;
    private String author;
    private Long timestamp;
    private String text;
    private boolean edited;
    private boolean removed;

    public Message() {
        id = UUID.randomUUID().toString();
        this.text = "?";
        this.author = "?";
        this.removed = false;
        this.edited = false;
        timestamp = System.currentTimeMillis();
    }

    public Message(String author, String message) {
        id = UUID.randomUUID().toString();
        this.text = message;
        this.author = author;
        this.removed = false;
        this.edited = false;
        timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public boolean isRemoved() {
        return removed;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public LocalDateTime getTime() {
        Date date = new Date(timestamp);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public int compareTo(Message o) {
        int cmp = timestamp.compareTo(o.getTimestamp());
        if (cmp != 0) {
            return cmp;
        }
        cmp = author.compareTo(o.getAuthor());
        if (cmp != 0) {
            return cmp;
        }
        cmp = text.compareTo(o.getText());
        if (cmp != 0) {
            return cmp;
        }
        return id.compareTo(o.getId());
    }

    @Override
    public String toString() {
        return id + " " + author + " " + timestamp + " " + text;
    }

    public String getFormattedMessage() {
        return String.format("%s %15s : %s", getTime().toString(), author, text);
    }
}