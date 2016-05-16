package by.bsu.famcs.uladbohdan;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import by.bsu.famcs.uladbohdan.messages.Message;
import by.bsu.famcs.uladbohdan.messages.MessageHistory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet(value = "/chat")
public class MessengerServlet extends HttpServlet {

    private MessageHistory messageStorage = new MessageHistory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        responseWithUpdates(req.getParameter("token"), resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleMessageWithCode(req, Message.REGULAR_MESSAGE_CODE);
        responseWithUpdates(req.getParameter("token"), resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleMessageWithCode(req, Message.EDITED_MESSAGE_CODE);
        responseWithUpdates(req.getParameter("token"), resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String messageId = req.getParameter("msgId");
        Message messageToRemove = new Message();
        messageToRemove.setId(messageId);
        messageToRemove.setMessageCode(Message.REMOVED_MESSAGE_CODE);
        messageStorage.addMessage(messageToRemove);
        responseWithUpdates(req.getParameter("token"), resp);
    }

    private void handleMessageWithCode(HttpServletRequest request, int messageCode) throws IOException {
        Gson gson = new GsonBuilder().create();
        Message receivedMessage = gson.fromJson(request.getReader(), Message.class);
        receivedMessage.setMessageCode(messageCode);
        messageStorage.addMessage(receivedMessage);
    }

    private void responseWithUpdates(String token, HttpServletResponse response) throws IOException {
        int index = getIndex(token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Response resp = new Response(messageStorage.getPortion(index), getNewToken());
        response.getWriter().write(new Gson().toJson(resp));
    }

    private int getIndex(String token) {
        String encodedIndex = token.substring(2, token.length() - 2);
        int tokenValue = Integer.valueOf(encodedIndex);
        return decodeIndex(tokenValue);
    }

    private String getNewToken() {
        return "TN" + Integer.toString(encodeIndex(messageStorage.size())) + "EN";
    }

    private int encodeIndex(int receivedMessagesCount) {
        return receivedMessagesCount * 8 + 11;
    }

    private int decodeIndex(int stateCode) {
        return (stateCode - 11) / 8;
    }
}