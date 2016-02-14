package uladbohdan.messages;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;

class Message implements Comparable<Message> {
    public Message() {
        id = nextID();
        this.message = "?";
        this.author = "?";
        timestamp = Long.toString(System.currentTimeMillis());
    }

    public Message(String author, String message) {
        id = nextID();
        this.message = message;
        this.author = author;
        timestamp = Long.toString(System.currentTimeMillis());
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

    public String getTimeMillis() {
        return timestamp;
    }

    public static void setData(ArrayList<Message> data) {
        Message.relatedData = data;
    }

    @Override
    public int compareTo(Message o) {
        int cmp = Long.compare(Long.parseLong(timestamp), Long.parseLong(o.getTimeMillis()));
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

    public LocalDateTime getTime() {
        long ms = Long.parseLong(timestamp);
        Date date = new Date(ms);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private String nextID() {
        boolean isFine;
        do {
            nextId++;
            isFine = true;
            for (Message i : relatedData)
                if (i.getId().equals(Integer.toString(nextId)))
                    isFine = false;
        }
        while (!isFine);
        return Integer.toString(nextId);
    }

    private String id, author, timestamp, message;
    private static int nextId = 0;
    private static ArrayList<Message> relatedData;
}