package by.bsu.famcs.uladbohdan.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class MessageHistory {

    public static final int MESSAGE_LENGTH = 140;
    public static final String RED = ConsoleApp.RED;
    public static final String END = ConsoleApp.END;

    private static final String MESSAGES_EXTERNAL_STORAGE = "backup.json";

    private List<Message> data;
    private FileWriter logfile;
    private String file;

    public MessageHistory() {
        data = new ArrayList<>();
        loadMessagesFromJsonFile(MESSAGES_EXTERNAL_STORAGE);
        try {
            logfile = new FileWriter("logfile");
            log("NEW SESSION");
        } catch (IOException e) {
            System.out.println("Sorry, logfile won't be formatted.");
            System.out.println();
        }
    }

    public void loadMessagesFromJsonFile(String fileName) {
        if (fileName == null) {
            System.out.println(RED + "ERROR: not enough arguments" + END);
            return;
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(fileName))) {
            Gson gson = new GsonBuilder().create();
            Message[] temp = gson.fromJson(reader, Message[].class);
            Collections.addAll(data, temp);
            System.out.println("Successfully read " + temp.length + " messages.");
            log("LOAD " + fileName + " successfully read " + temp.length + " messages.");
            file = fileName;
        } catch (Exception e) {
            System.out.println(RED + "FAILED loading from file" + END);
            log("LOAD " + fileName + " loading failed.");
        }
    }

    public void addMessage(Message message) {
        data.add(message);
        Message last = data.get(data.size() - 1);
        if (last.getText().length() > MESSAGE_LENGTH) {
            System.out.println(RED + "WARNING: long message (more than 140 symbols)." + END);
        }
        System.out.println("Successfully added: " + last.getFormattedMessage());
        log("ADD " + last.getId() + " " + last.getAuthor() + " " + last.getText());
        saveMessagesToJsonFile(MESSAGES_EXTERNAL_STORAGE);
    }

    public void addMessage(String author, String text) {
        Message message = new Message(author, text);
        addMessage(message);
    }

    public void showMessages(boolean isFormatted) {
        try {
            Collections.sort(data);
            if (isFormatted) {
                System.out.println("FORMATTED LIST OF MESSAGES:");
                log("QUERY formatted list");
                for (Message i : data) {
                    System.out.println(i.getFormattedMessage());
                }
            } else {
                System.out.println("FULL LIST OF MESSAGES:");
                log("QUERY full list");
                for (Message i : data) {
                    System.out.println(i.toString());
                }
            }
        } catch (Exception e) {
            System.out.println(RED + "Failed on your query. Try another one." + END);
            log("QUERY failed");
        }
    }

    public void showMessagesByTime(boolean isFormatted, String timeFirst, String timeSecond) {
        try {
            LocalDateTime timeBegin;
            LocalDateTime timeEnd;
            if (timeFirst == null) {
                timeBegin = LocalDateTime.now().plusMinutes(2);
            } else {
                timeBegin = LocalDateTime.parse(timeFirst, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (timeSecond == null) {
                timeEnd = LocalDateTime.now().plusMinutes(2);
            } else {
                timeEnd = LocalDateTime.parse(timeSecond, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (isFormatted) {
                System.out.println("FORMATTED LIST OF MESSAGES from " + timeBegin + " to " + timeEnd);
                log("QUERY formatted list: " + timeBegin + " to " + timeEnd);
            } else {
                System.out.println("FULL LIST OF MESSAGES from " + timeBegin + " to " + timeEnd);
                log("QUERY list: " + timeBegin + " to " + timeEnd);
            }
            data.stream().filter(i -> ((i.getTime().compareTo(timeBegin) > 0) &&
                    (i.getTime().compareTo(timeEnd) < 0))).forEach(i -> {
                if (isFormatted) {
                    System.out.println(i.getFormattedMessage());
                } else {
                    System.out.println(i.toString());
                }
            });
        } catch (Exception e) {
            System.out.println(RED + "Failed on your query. Try another one." + END);
            log("QUERY failed");
        }
    }

    public void deleteMessage(String id) {
        if (id == null) {
            log("DELETE failed: lack of arguments");
            System.out.println(RED + "ERROR: not enough arguments. ID is required to delete a message" + END);
            return;
        }
        boolean isRemoved = false;
        for (Iterator<Message> it = data.iterator(); it.hasNext(); ) {
            if (it.next().getId().equals(id)) {
                it.remove();
                isRemoved = true;
            }
        }
        if (isRemoved) {
            saveMessagesToJsonFile(MESSAGES_EXTERNAL_STORAGE);
            System.out.println("Successfully removed by id: " + id);
            log("DELETE successfully by id: " + id);
        } else {
            System.out.println("Message not found. Nothing removed.");
            log("DELETE failed: if not found");
        }
    }

    public void saveMessagesToJsonFile(String fileName) {
        String saveTo;
        if (fileName == null) {
            if (file == null) {
                System.out.println(RED + "ERROR: not enough arguments." + END);
                log("SAVE failed: lack of arguments");
                return;
            } else {
                System.out.println("Will be saved to " + file);
                saveTo = file;
            }
        } else {
            saveTo = fileName;
        }
        try (FileWriter out = new FileWriter(saveTo)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(data, out);
            System.out.println("Successfully saved to " + saveTo);
            log("SAVE successfully to " + saveTo + ", " + data.size() + " messages");
        } catch (Exception e) {
            log("SAVE failed");
            System.out.println(RED + "FAILED saving to file" + END);
        }
    }

    public void clearMessages() {
        data.clear();
        saveMessagesToJsonFile(MESSAGES_EXTERNAL_STORAGE);
        System.out.println(RED + "Cleared." + END);
        log("CLEAR all");
    }

    public int size() {
        return data.size();
    }

    public List<Message> getPortion(int index) {
        List<Message> portion = new ArrayList<Message>();
        for (int i = index; i < data.size(); i++) {
            portion.add(data.get(i));
        }
        return portion;
    }

    public void close() {
        try {
            if (logfile != null) {
                log("END OF SESSION");
                logfile.close();
            }
        } catch (IOException e) {
            System.out.println("Failed closing logfile");
        }
    }

    public void searchMessages(String searchType, String query) {
        try {
            switch (searchType) {
                case "-author":
                    searchByAuthor(query);
                    break;
                case "-keyword":
                    searchByKeyword(query);
                    break;
                case "-regex":
                    searchByRegex(query);
                    break;
            }
        } catch (Exception e) {
            System.out.println("Sorry, but something went wrong. Try to change the query.");
            log("SEARCH failed");
        }
    }

    private void searchByAuthor(String author) throws Exception {
        System.out.println("BY AUTHOR: " + author);
        int counter = 0;
        for (Message i : data) {
            if (i.getAuthor().equals(author)) {
                System.out.println(i.getFormattedMessage());
                counter++;
            }
        }
        if (counter == 0) {
            System.out.println("nothing found");
            log("SEARCH by author: " + author + ", nothing found");
        } else {
            log("SEARCH by author: " + author + ", found: " + counter);
        }
    }

    private void searchByKeyword(String keyword) throws Exception {
        System.out.println("BY KEYWORD: " + keyword);
        int counter = 0;
        for (Message i : data) {
            if (i.getText().contains(keyword)) {
                System.out.println(i.getFormattedMessage());
                counter++;
            }
        }
        if (counter == 0) {
            System.out.println("nothing found");
            log("SEARCH by keyword: " + keyword + ", nothing found");
        } else {
            log("SEARCH by keyword: " + keyword + ", found: " + counter);
        }
    }

    private void searchByRegex(String regex) throws Exception {
        System.out.println("BY REGULAR EXPRESSION: " + regex);
        int counter = 0;
        for (Message i : data) {
            if (Pattern.matches(regex, i.getText())) {
                System.out.println(i.getFormattedMessage());
                counter++;
            }
        }
        if (counter == 0) {
            System.out.println("nothing found");
            log("SEARCH by regex: " + regex + ", nothing found");
        } else {
            log("SEARCH by regex: " + regex + ", found: " + counter);
        }
    }

    private void log(String string) {
        if (logfile == null) {
            return;
        }
        try {
            logfile.write(LocalDateTime.now() + " " + string + "\n");
        } catch (IOException e) {
            System.out.println(RED + "FAILED log writing" + END);
        }
    }
}
