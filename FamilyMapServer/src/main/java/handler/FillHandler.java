package handler;

import com.sun.net.httpserver.*;
import request.FillRequest;
import result.FillResult;
import service.FillService;

import java.io.IOException;
import java.net.HttpURLConnection;
public class FillHandler implements HttpHandler{
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().equalsIgnoreCase("post")) {

                FillRequest fillRequest = parseUrlIntoRequest(exchange.getRequestURI().toString());

                FillService fillService = new FillService();
                FillResult fillResult = fillService.fill(fillRequest);

                if(fillResult.isSuccess()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }
                GsonHelper.writeJsonResult(fillResult, exchange);
            }
            else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }
        catch(IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();

            e.printStackTrace();
        }
    }

    private FillRequest parseUrlIntoRequest(String fillUrl) {
        String[] splitUrl = fillUrl.substring(1).split("/");

        FillRequest fillRequest = new FillRequest();
        fillRequest.setUsername(splitUrl[1]);

        if(splitUrl.length > 2) {
            fillRequest.setGenerations(Integer.parseInt(splitUrl[2]));
        }
        else {
            fillRequest.setGenerations(4);
        }
        return fillRequest;
    }

}
