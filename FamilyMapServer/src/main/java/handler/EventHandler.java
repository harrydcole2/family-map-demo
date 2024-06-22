package handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.*;
import request.EventRequest;
import result.EventResult;
import service.GetEventService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class EventHandler implements HttpHandler{
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            if (exchange.getRequestMethod().equalsIgnoreCase("get")) {

                EventRequest eventRequest = parseUrlIntoRequest(exchange);

                if (eventRequest.getAuthtoken() != null) {

                    GetEventService getEventService = new GetEventService();
                    EventResult eventResult = getEventService.getEvent(eventRequest);

                    if(eventResult.isSuccess()) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        if(eventResult.getData().size() == 1) { //may be insufficient for spec if they fill 0 then /event, & misses success message
                            writeEventByIDJsonResult(eventResult, exchange);
                        }
                        else {
                            GsonHelper.writeJsonResult(eventResult, exchange);
                        }
                    }
                    else {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                        GsonHelper.writeJsonResult(eventResult, exchange);
                    }
                    success = true;
                }
            }
            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }
        catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();

            e.printStackTrace();
        }
    }

    private EventRequest parseUrlIntoRequest(HttpExchange exchange) {
        String eventReqUrl = exchange.getRequestURI().toString();
        String[] splitUrl = eventReqUrl.substring(1).split("/");

        EventRequest eventRequest = new EventRequest();
        if(splitUrl.length > 1) {
            eventRequest.setEventID(splitUrl[1]);
        }

        Headers reqHeaders = exchange.getRequestHeaders();
        if (reqHeaders.containsKey("Authorization")) {
            String authToken = reqHeaders.getFirst("Authorization");
            eventRequest.setAuthtoken(authToken);
        }

        return eventRequest;
    }

    private void writeEventByIDJsonResult(EventResult eventResult, HttpExchange exchange) throws IOException {
        Gson gson = new Gson();
        String respData = gson.toJson(eventResult.getData().get(0));

        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(respData);
        JsonObject object = jsonElement.getAsJsonObject();
        object.addProperty("success", true);
        respData = gson.toJson(object);

        OutputStream respBody = exchange.getResponseBody();
        GsonHelper.writeString(respData, respBody);
        respBody.close();
    }
}
