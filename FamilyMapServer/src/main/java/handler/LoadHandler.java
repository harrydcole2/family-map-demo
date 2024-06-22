package handler;

import com.sun.net.httpserver.*;
import request.LoadRequest;
import result.LoadResult;
import service.LoadService;

import java.io.IOException;
import java.net.HttpURLConnection;

public class LoadHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {

        try {
            if (exchange.getRequestMethod().equalsIgnoreCase("post")) {
                LoadRequest loadRequest = (LoadRequest) GsonHelper.readJsonRequest(exchange, LoadRequest.class);

                LoadService loadService = new LoadService();
                LoadResult loadResult = loadService.load(loadRequest);

                if(loadResult.isSuccess()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }

                GsonHelper.writeJsonResult(loadResult, exchange);
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
}
