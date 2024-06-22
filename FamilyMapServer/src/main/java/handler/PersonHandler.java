package handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.*;
import model.Person;
import request.PersonRequest;
import result.PersonResult;
import service.GetPersonService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class PersonHandler implements HttpHandler{
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try {
            if (exchange.getRequestMethod().equalsIgnoreCase("get")) {

                PersonRequest personRequest = parseUrlIntoRequest(exchange);

                if (personRequest.getAuthtoken() != null) {

                    GetPersonService getPersonService = new GetPersonService();
                    PersonResult personResult = getPersonService.getPerson(personRequest);

                    if(personResult.isSuccess()) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        if(personResult.getData().size() == 1) { //may be insufficient for spec if they fill 0 then /person, & misses success
                            writePersonByIDJsonResult(personResult, exchange);
                        }
                        else {
                            GsonHelper.writeJsonResult(personResult, exchange);
                        }
                    }
                    else {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                        GsonHelper.writeJsonResult(personResult, exchange);
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

    private PersonRequest parseUrlIntoRequest(HttpExchange exchange) {
        String personReqUrl = exchange.getRequestURI().toString();
        String[] splitUrl = personReqUrl.substring(1).split("/");

        PersonRequest personRequest = new PersonRequest();
        if(splitUrl.length > 1) {
            personRequest.setPersonID(splitUrl[1]);
        }

        Headers reqHeaders = exchange.getRequestHeaders();
        if (reqHeaders.containsKey("Authorization")) {

            String authToken = reqHeaders.getFirst("Authorization");
            personRequest.setAuthtoken(authToken);
        }

        return personRequest;
    }

    private void writePersonByIDJsonResult(PersonResult personResult, HttpExchange exchange) throws IOException {
        Gson gson = new Gson();
        String respData = gson.toJson(personResult.getData().get(0), Person.class);

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
