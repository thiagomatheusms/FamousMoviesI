package com.thiagomatheusms.famousmovies.Utilities;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class InternetChecking extends Thread{

    private Handler handler;

    public InternetChecking(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();

        Boolean bool = false;
        Message message = new Message();
        message.what = 1;

        try {
            URL url = new URL("https://google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            bool = connection.getResponseCode() == 200;
            if(bool) {
                message.obj = "ok";
            }
        } catch (IOException e) {
            message.obj = "error";
            e.printStackTrace();
        }
        handler.sendMessage(message);
    }

}
