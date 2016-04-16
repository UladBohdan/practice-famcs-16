package by.bsu.up.chat.server;

import by.bsu.up.chat.Constants;
import by.bsu.up.chat.InvalidTokenException;
import by.bsu.up.chat.logging.Logger;
import by.bsu.up.chat.logging.impl.Log;

import by.bsu.up.chat.utils.MessageHelper;
import by.bsu.up.chat.utils.StringUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.bsu.famcs.uladbohdan.messages.MessageHistory;
import by.bsu.famcs.uladbohdan.messages.Message;

public class ServerHandler implements HttpHandler {

    private static final Logger logger = Log.create(ServerHandler.class);

    private MessageHistory messageStorage = new MessageHistory();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Response response;

        try {
            response = dispatch(httpExchange);
        } catch (Throwable e) {
            // WARNING! It's not a good practice to catch all exceptions via Throwable
            // or Exception classes. But if you want to handle and you know
            // how to handle them correctly, you may use such approach.
            // Useful when you use thread pool and don't want to corrupt a thread
            logger.error("An error occurred when dispatching request.", e);
            response = new Response(Constants.RESPONSE_CODE_INTERNAL_SERVER_ERROR, "Error while dispatching message");
        }
        sendResponse(httpExchange, response);

    }

    private Response dispatch(HttpExchange httpExchange) {
        if (Constants.REQUEST_METHOD_GET.equals(httpExchange.getRequestMethod())) {
            return doGet(httpExchange);
        } else if (Constants.REQUEST_METHOD_POST.equals(httpExchange.getRequestMethod())) {
            return doPost(httpExchange);
        } else if (Constants.REQUEST_METHOD_PUT.equals(httpExchange.getRequestMethod())) {
            return doPut(httpExchange);
        } else if (Constants.REQUEST_METHOD_DELETE.equals(httpExchange.getRequestMethod())) {
            return doDelete(httpExchange);
        } else if (Constants.REQUEST_METHOD_OPTIONS.equals(httpExchange.getRequestMethod())) {
            return doOptions(httpExchange);
        } else {
            return new Response(Constants.RESPONSE_CODE_METHOD_NOT_ALLOWED,
                    String.format("Unsupported http method %s", httpExchange.getRequestMethod()));
        }
    }

    private Response doGet(HttpExchange httpExchange) {
        try {
            Map<String, String> params = getRequestParams(httpExchange);
            return respondWithUpdates(params);
        } catch (InvalidTokenException e) {
            logger.error("GET: invalid token.", e);
            return Response.badRequest(e.getMessage());
        }
    }

    private Response doPost(HttpExchange httpExchange) {
        try {
            Message message = MessageHelper.getClientMessage(httpExchange.getRequestBody());
            logger.info(String.format("Received new message from user: %s", message));
            messageStorage.addMessage(message);
            return respondWithUpdates(getRequestParams(httpExchange));
        } catch (ParseException e) {
            logger.error("Could not parse message.", e);
            return new Response(Constants.RESPONSE_CODE_BAD_REQUEST, "Incorrect request body");
        } catch (InvalidTokenException e) {
            logger.error("POST: invalid token.", e);
            return Response.badRequest(e.getMessage());
        }
    }

    private Response doPut(HttpExchange httpExchange) {
        try {
            Message message = MessageHelper.getMessageToEdit(httpExchange.getRequestBody());
            Message updatedMessage = messageStorage.editMessage(message.getId(), message.getText());
            if (updatedMessage != null) {
                logger.info(String.format("Message edited: %s", updatedMessage));
            } else {
                logger.info("Message to be edited not found");
            }
            return respondWithUpdates(getRequestParams(httpExchange));
        } catch (ParseException e) {
            logger.error("Could not parse message.", e);
            return new Response(Constants.RESPONSE_CODE_BAD_REQUEST, "Incorrect request body");
        } catch (InvalidTokenException e) {
            logger.error("PUT: invalid token.", e);
            return Response.badRequest(e.getMessage());
        }
    }

    private Response doDelete(HttpExchange httpExchange) {
        try {
            Map<String, String> params = getRequestParams(httpExchange);
            String id = getIdParam(params);
            messageStorage.markMessageAsRemovedOrRecovered(id);
            logger.info("Message marked as removed or recovered: " + id);
            return respondWithUpdates(getRequestParams(httpExchange));
        } catch (InvalidTokenException e) {
            logger.error("DELETE: invalid token.", e);
            return Response.badRequest(e.getMessage());
        }
    }

    private Response doOptions(HttpExchange httpExchange) {
        httpExchange.getResponseHeaders().add(Constants.REQUEST_HEADER_ACCESS_CONTROL_METHODS,Constants.HEADER_VALUE_ALL_METHODS);
        return Response.ok();
    }

    private Map<String, String> getRequestParams(HttpExchange httpExchange) throws InvalidTokenException {
        String query = httpExchange.getRequestURI().getQuery();
        if (query == null) {
            throw new InvalidTokenException("Absent query in request");
        }
        return queryToMap(query);
    }

    private String getTokenParam(Map<String, String> params) throws InvalidTokenException {
        String token = params.get(Constants.REQUEST_PARAM_TOKEN);
        if (StringUtils.isEmpty(token)) {
            throw new InvalidTokenException("Token query parameter is required");
        }
        return token;
    }

    private String getIdParam(Map<String, String> params) throws InvalidTokenException {
        String id = params.get(Constants.REQUEST_PARAM_MESSAGE_ID);
        if (StringUtils.isEmpty(id)) {
            throw new InvalidTokenException("Token query parameter is required");
        }
        return id;
    }

    private Response respondWithUpdates(Map<String, String> params) throws InvalidTokenException {
        String token = getTokenParam(params);
        int index = MessageHelper.parseToken(token);
        if (index > messageStorage.size()) {
            return Response.badRequest(
                    String.format("Incorrect token in request: %s. Server does not have so many messages", token));
        }
        List<Message> messages = messageStorage.getPortion(index);
        String responseBody = MessageHelper.buildServerResponseBody(messages, messageStorage.size());
        return Response.ok(responseBody);
    }

    private void sendResponse(HttpExchange httpExchange, Response response) {
        try (OutputStream os = httpExchange.getResponseBody()) {
            byte[] bytes = response.getBody().getBytes();

            Headers headers = httpExchange.getResponseHeaders();
            headers.add(Constants.REQUEST_HEADER_ACCESS_CONTROL_ORIGIN,"*");
            httpExchange.sendResponseHeaders(response.getStatusCode(), bytes.length);

            os.write( bytes);
            // there is no need to close stream manually
            // as try-catch with auto-closable is used
            /**
             * {@see http://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html}
             */
        } catch (IOException e) {
            logger.error("Could not send response", e);
        }
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();

        for (String queryParam : query.split(Constants.REQUEST_PARAMS_DELIMITER)) {
            String paramKeyValuePair[] = queryParam.split("=");
            if (paramKeyValuePair.length > 1) {
                result.put(paramKeyValuePair[0], paramKeyValuePair[1]);
            } else {
                result.put(paramKeyValuePair[0], "");
            }
        }
        return result;
    }
}
