package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import jsonModel.*;

import java.io.*;
import java.net.*;

public class Server {
    private static final int MAX_WAITING_CONNECTIONS = 12;
    private HttpServer server;
    private void run(String portNumber) {

        System.out.println("Initializing HTTP Server");

        try {
            server = HttpServer.create(
                    new InetSocketAddress(Integer.parseInt(portNumber)),
                    MAX_WAITING_CONNECTIONS);
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        server.setExecutor(null);

        System.out.println("Creating contexts");

        server.createContext("/user/register", new RegisterHandler());
        server.createContext("/user/login", new LoginHandler());
        server.createContext("/clear", new ClearHandler());
        server.createContext("/fill", new FillHandler());
        server.createContext("/load", new LoadHandler());
        server.createContext("/person", new PersonHandler());
        server.createContext("/event", new EventHandler());

        server.createContext("/", new FileHandler());

        System.out.println("Starting server");

        server.start();
        System.out.println("Server started");
    }

    public static void main(String[] args) {

        Gson gson = new Gson();
        try {
            Reader fReader = new FileReader("json" + File.separator + "fnames.json");
            FNamesData femaleNames = gson.fromJson(fReader, FNamesData.class);

            Reader mReader = new FileReader("json" + File.separator + "mnames.json");
            MNamesData maleNames = gson.fromJson(mReader, MNamesData.class);

            Reader sReader = new FileReader("json" + File.separator + "snames.json");
            SNamesData lastNames = gson.fromJson(sReader, SNamesData.class);

            Reader locReader = new FileReader("json" + File.separator + "locations.json");
            LocationData locationData = gson.fromJson(locReader, LocationData.class);

            JsonDataCache cache = JsonDataCache.getInstance();
            cache.loadData(femaleNames, maleNames, lastNames, locationData);

        }
        catch (IOException e) {
            e.printStackTrace();

            System.out.println("Problem loading into cache");
        }

        String portNumber = args[0];
        new Server().run(portNumber);
    }
}
