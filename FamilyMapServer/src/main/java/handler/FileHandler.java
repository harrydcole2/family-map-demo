package handler;

import com.sun.net.httpserver.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;

public class FileHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {

        String urlPath = exchange.getRequestURI().toString();
        if(urlPath.equals("/")) {
            urlPath = "/index.html";
        }

        String filePath = "web" + urlPath;
        File file = new File(filePath);
        if(file.exists()) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

            OutputStream respBody = exchange.getResponseBody();
            Files.copy(file.toPath(), respBody);
            respBody.flush();
            respBody.close();
        }
        else {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);

            file = new File("web/HTML/404.html");
            OutputStream respBody = exchange.getResponseBody();
            Files.copy(file.toPath(), respBody);
            respBody.flush();
            respBody.close();
        }
    }
}
