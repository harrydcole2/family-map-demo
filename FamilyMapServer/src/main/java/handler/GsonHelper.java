package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import result.Result;
import request.Request;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

public class GsonHelper {
    public static Request readJsonRequest(HttpExchange exchange, Type reqType) {
        Gson gson = new Gson();
        InputStreamReader inputReader = new InputStreamReader(exchange.getRequestBody());
        Request request = gson.fromJson(inputReader, reqType);
        return request;
    }
    public static void writeJsonResult(Result result, HttpExchange exchange) throws IOException {
        Gson gson = new Gson();
        String respData = gson.toJson(result);

        OutputStream respBody = exchange.getResponseBody();
        writeString(respData, respBody);
        respBody.close();
    }
    public static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
