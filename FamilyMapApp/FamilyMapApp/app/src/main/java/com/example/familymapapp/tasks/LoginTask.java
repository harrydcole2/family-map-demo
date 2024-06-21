package com.example.familymapapp.tasks;

import android.os.Bundle;
import android.os.Message;

import android.os.Handler;

import com.example.familymapapp.controller.DataCache;
import com.example.familymapapp.controller.ServerProxy;

import request.LoginRequest;
import result.LoginResult;

public class LoginTask implements Runnable {
    private Handler messageHandler;
    private LoginRequest request;
    private String host;
    private String port;

    public LoginTask(Handler messageHandler, LoginRequest req, String host, String port) {
        this.messageHandler = messageHandler;
        this.request = req;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        LoginResult res = ServerProxy.login(request, host, port);

        DataCache cache = DataCache.getInstance();
        cache.setUserPersonId(res.getPersonID());

        sendMessage(res);
    }

    private void sendMessage(LoginResult res) {
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
