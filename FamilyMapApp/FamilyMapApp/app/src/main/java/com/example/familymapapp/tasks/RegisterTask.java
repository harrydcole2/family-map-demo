package com.example.familymapapp.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.familymapapp.controller.DataCache;
import com.example.familymapapp.controller.ServerProxy;

import request.RegisterRequest;
import result.RegisterResult;

public class RegisterTask implements Runnable {
    private Handler messageHandler;
    private RegisterRequest request;
    private String host;
    private String port;

    public RegisterTask(Handler messageHandler, RegisterRequest req, String host, String port) {
        this.messageHandler = messageHandler;
        this.request = req;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        RegisterResult res = ServerProxy.register(request, host, port);

        DataCache cache = DataCache.getInstance();
        cache.setUserPersonId(res.getPersonID());

        sendMessage(res);
    }

    private void sendMessage(RegisterResult res) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        if(res.isSuccess() == true) {
            messageBundle.putBoolean("isSuccess", true);
            messageBundle.putString("Authorization", res.getAuthtoken());
        }
        else {
            messageBundle.putBoolean("isSuccess", false);
        }
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }
}
