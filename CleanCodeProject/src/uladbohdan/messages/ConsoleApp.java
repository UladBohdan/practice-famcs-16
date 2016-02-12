package uladbohdan.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class ConsoleApp {

    public ConsoleApp() {
        data = new ArrayList<>();
        try {
            logfile = new FileWriter("logfile");
            log("NEW SESSION");
        } catch (IOException e) {
            System.out.println("Sorry, logfile won't be formatted.");
            System.out.println();
        }
        try {
            System.out.println("Console App for working with messages in JSON format.");
            System.out.println("Type " + COMM + "help" + END + " for list of commands; " +
                    COMM + "quit" + END + " to quit.");
            Scanner in = new Scanner(System.in);
            while (true) {
                System.out.print("$ ");
                if (!handleCommand(in.nextLine()))
                    break;
            }
            if (logfile != null) {
                log("END OF SESSION");
                logfile.close();
            }
        } catch (Exception e) {
            System.out.println("Something went wrong:(");
        } finally {
            System.out.println("Thanks for using my app!");
        }
    }

    public static void main(String[] args) {
        new ConsoleApp();
    }

    private boolean handleCommand(String input) {
        String[] command = input.split(" ");
        switch (command[0]) {
            case "help":
                log("HELP queried");
                System.out.println("List of available commands:");
                System.out.println(COMM + "load [file]" + END +
                        " - load messages from file. Previous data will be lost");
                System.out.println(COMM + "add [author] [message]" + END +
                        "- add new message (id&time are given automatically");
                System.out.println(COMM + "show [-f] [time to start] [time to end]" + END +
                        " - show messages in chronological order (parameters are optional)");
                System.out.println(COMM + "remove [id]" + END + " - remove message by id");
                System.out.println(COMM + "save [file]" + END + " - save messages to a file (file is optional)");
                System.out.println(COMM + "search [-author/-keyword/-regex] [author/keyword/regex]" + END +
                        " - search by a parameter");
                System.out.println("* General format for datetime: 2011-12-03T10:15:30");
                return true;
            case "load":
                loadMessagesFromJSONFile(command);
                return true;
            case "add":
                addMessage(command);
                return true;
            case "show":
                showMessages(command);
                return true;
            case "remove":
                deleteMessage(command);
                return true;
            case "save":
                saveMessagesToJSONFile(command);
                return true;
            case "search":
                searchMessages(command);
                return true;
            case "quit":
            case "exit":
                return false;
            default:
                System.out.println("Unknown command. Try again.");
                return true;
        }
    }

    private void loadMessagesFromJSONFile(String[] command) {
        if (command.length == 1) {
            System.out.println(RED + "ERROR: not enough arguments" + END);
            return;
        }
        if (command.length > 2)
            System.out.println(RED + "WARNING: too many arguments" + END);
        try {
            Reader reader = new InputStreamReader(new FileInputStream(command[1]));
            Gson gson = new GsonBuilder().create();
            Message[] temp = gson.fromJson(reader, Message[].class);
            data.clear();
            Collections.addAll(data, temp);
            System.out.println("Successfully read " + temp.length + " messages.");
            log("LOAD " + command[1] + " successfully read " + temp.length + " messages.");
            file = command[1];
            reader.close();
        } catch (Exception e) {
            System.out.println(RED + "FAILED loading from file" + END);
            log("LOAD " + command[1] + " loading failed.");
        }
    }

    private void addMessage(String[] command) {
        if (command.length < 3)
            System.out.println(RED + "WARNING: not enough arguments" + END);
        if (command.length == 1)
            data.add(new Message());
        else if (command.length == 2)
            data.add(new Message(command[1], "?"));
        else {
            StringBuilder msg = new StringBuilder();
            for (int i = 2; i < command.length; i++) {
                msg.append(command[i]);
                msg.append(" ");
            }
            msg.deleteCharAt(msg.length()-1);
            data.add(new Message(command[1], msg.toString()));
        }
        Message last = data.get(data.size() - 1);
        if (last.getMessage().length() > 140)
            System.out.println(RED + "WARNING: long message (more than 140 symbols)." + END);
        System.out.println("Successfully added: " + last.getFormattedMessage());
        log("ADD " + last.getId() + " " + last.getAuthor() + " " + last.getMessage());
    }

    private void showMessages(String[] command) {
        try {
            Collections.sort(data);
            if (command.length == 1) {
                System.out.println("FULL LIST OF MESSAGES:");
                log("QUERY list");
                for (Message i : data)
                    System.out.println(i.toString());
            } else if (command.length == 2 && command[1].equals("-f")) {
                System.out.println("FORMATTED LIST OF MESSAGES:");
                log("QUERY formatted list");
                for (Message i : data)
                    System.out.println(i.getFormattedMessage());
            } else {
                LocalDateTime timeBegin, timeEnd;
                boolean isF;
                if (command[1].equals("-f")) {
                    isF = true;
                    timeBegin = LocalDateTime.parse(command[2], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    if (command.length > 3)
                        timeEnd = LocalDateTime.parse(command[3], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    else
                        timeEnd = LocalDateTime.now().plusMinutes(2);
                } else {
                    isF = false;
                    timeBegin = LocalDateTime.parse(command[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    if (command.length > 2)
                        timeEnd = LocalDateTime.parse(command[2], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    else
                        timeEnd = LocalDateTime.now().plusMinutes(2);
                }
                if (isF) {
                    System.out.println("FORMATTED LIST OF MESSAGES from " + timeBegin + " to " + timeEnd);
                    log("QUERY formatted list: " + timeBegin + " to " + timeEnd);
                } else {
                    System.out.println("FULL LIST OF MESSAGES from " + timeBegin + " to " + timeEnd);
                    log("QUERY list: " + timeBegin + " to " + timeEnd);
                }
                for (Message i : data)
                    if (i.getTime().compareTo(timeBegin) > 0 &&
                            i.getTime().compareTo(timeEnd) < 0)
                        if (isF)
                            System.out.println(i.getFormattedMessage());
                        else
                            System.out.println(i.toString());
            }
        } catch (Exception e) {
            System.out.println("Failed on your query. Try another one.");
            log("QUERY failed");
        }
    }

    private void deleteMessage(String[] command) {
        if (command.length == 1) {
            log("DELETE failed: lack of arguments");
            System.out.println(RED + "ERROR: not enough arguments. ID is required to delete a message" + END);
            return;
        }
        String id = command[1];
        boolean isRemoved = false;
        for (Iterator<Message> it = data.iterator(); it.hasNext(); ) {
            if (it.next().getId().equals(id)) {
                it.remove();
                isRemoved = true;
            }
        }
        if (isRemoved) {
            System.out.println("Successfully removed by id: " + id);
            log("DELETE successfully by id: " + id);
        } else {
            System.out.println("Message not found. Nothing removed.");
            log("DELETE failed: if not found");
        }
    }

    private void saveMessagesToJSONFile(String[] command) {
        String saveTo;
        if (command.length == 1) {
            if (file.equals("null")) {
                System.out.println(RED + "ERROR: not enough arguments." + END);
                log("SAVE failed: lack of arguments");
                return;
            } else {
                System.out.println("Will be saved to " + file);
                saveTo = file;
            }
        } else
            saveTo = command[1];
        try {
            FileWriter out = new FileWriter(saveTo);
            Gson gson = new GsonBuilder().create();
            gson.toJson(data, out);
            out.close();
            System.out.println("Successfully saved to " + saveTo);
            log("SAVE successfully to " + saveTo + ", " + data.size() + " messages");
        } catch (Exception e) {
            log("SAVE failed");
            System.out.println(RED + "FAILED saving to file" + END);
        }
    }

    private void searchMessages(String[] command) {
        if (command.length < 3) {
            System.out.println(RED + "ERROR: not enough arguments. Parameters are required" + END);
            log("SEARCH failed: lack of arguments.");
            return;
        }
        try {
            int counter;
            switch (command[1]) {
                case "-author":
                    System.out.println("BY AUTHOR: " + command[2]);
                    counter = 0;
                    for (Message i : data) {
                        if (i.getAuthor().equals(command[2])) {
                            System.out.println(i.getFormattedMessage());
                            counter++;
                        }
                    }
                    if (counter == 0) {
                        System.out.println("nothing found");
                        log("SEARCH by author: " + command[2] + ", nothing found");
                    } else {
                        log("SEARCH by author: " + command[2] + ", found: " + counter);
                    }
                    break;
                case "-keyword":
                    System.out.println("BY KEYWORD: " + command[2]);
                    counter = 0;
                    for (Message i : data) {
                        if (i.getMessage().contains(command[2])) {
                            System.out.println(i.getFormattedMessage());
                            counter++;
                        }
                    }
                    if (counter == 0) {
                        System.out.println("nothing found");
                        log("SEARCH by keyword: " + command[2] + ", nothing found");
                    } else {
                        log("SEARCH by keyword: " + command[2] + ", found: " + counter);
                    }
                    break;
                case "-regex":
                    System.out.println("BY REGULAR EXPRESSION: " + command[2]);
                    counter = 0;
                    for (Message i : data) {
                        if (Pattern.matches(command[2], i.getMessage())) {
                            System.out.println(i.getFormattedMessage());
                            counter++;
                        }
                    }
                    if (counter == 0) {
                        System.out.println("nothing found");
                        log("SEARCH by regex: " + command[2] + ", nothing found");
                    } else {
                        log("SEARCH by regex: " + command[2] + ", found: " + counter);
                    }
                    break;
            }
        } catch (Exception e) {
            System.out.println("Sorry, but something went wrong. Try to change the query.");
            log("SEARCH failed");
        }
    }

    private void log(String string) {
        if (logfile == null)
            return;
        try {
            logfile.write(LocalDateTime.now() + " " + string + "\n");
        } catch (IOException e) {        }
    }

    private ArrayList<Message> data;
    private FileWriter logfile;

    private static int nextId = 0;
    private String file = "saved.txt";
    private static final String COMM = (char) 27 + "[92m";
    private static final String RED = (char) 27 + "[91m";
    private static final String END = (char) 27 + "[0m";

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
            return getTime() + ", by " + author + "   " + message;
        }

        public LocalDateTime getTime() {
            long ms = Long.parseLong(timestamp);
            Date date = new Date(ms);
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

        private String nextID() {
            boolean isFine;
            do {
                ConsoleApp.nextId++;
                isFine = true;
                for (Message i : data)
                    if (i.getId().equals(Integer.toString(nextId)))
                        isFine = false;
            }
            while (!isFine);
            return Integer.toString(ConsoleApp.nextId);
        }

        private String id, author, timestamp, message;
    }
}
