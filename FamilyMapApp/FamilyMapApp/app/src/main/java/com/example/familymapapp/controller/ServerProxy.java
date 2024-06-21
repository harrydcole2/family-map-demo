package com.example.familymapapp.controller;

import android.media.metrics.Event;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.*;
import result.*;
import request.*;

public class ServerProxy {
    public static LoginResult login(LoginRequest req, String host, String port) {

        try {
            URL url = new URL("http://" + host + ":" + port + "/user/login");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            http.addRequestProperty("Accept", "application/json");
            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(req);

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            LoginResult res;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                res = gson.fromJson(respData, LoginResult.class);
            }
            else {
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                res = gson.fromJson(respData, LoginResult.class);
            }
            return res;
        }
        catch(IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static RegisterResult register(RegisterRequest req, String host, String port) {
        try {
            URL url = new URL("http://" + host + ":" + port + "/user/register");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            http.addRequestProperty("Accept", "application/json");
            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(req);

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            RegisterResult res;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                res = gson.fromJson(respData, RegisterResult.class);
            }
            else {
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                res = gson.fromJson(respData, RegisterResult.class);
            }
            return res;
        }
        catch(IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static PersonResult getPeople(PersonRequest req, String host, String port) {
        try {
            URL url = new URL("http://" + host + ":" + port + "/person");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);

            http.addRequestProperty("Authorization", req.getAuthtoken()); //fix this!
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            Gson gson = new Gson();
            PersonResult res;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                res = gson.fromJson(respData, PersonResult.class);
            }
            else {
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                res = gson.fromJson(respData, PersonResult.class);
            }
            return res;
        }
        catch(IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static EventResult getEvents(EventRequest req, String host, String port) {
        try {
            URL url = new URL("http://" + host + ":" + port + "/event");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);

            http.addRequestProperty("Authorization", req.getAuthtoken()); //fix this!
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            Gson gson = new Gson();
            EventResult res;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                res = gson.fromJson(respData, EventResult.class);
            }
            else {
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                res = gson.fromJson(respData, EventResult.class);
            }
            return res;
        }
        catch(IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

}
