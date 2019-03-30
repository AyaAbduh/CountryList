package com.example.countrylist.Model.Network;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadImage implements Runnable {
    private final Handler handler;
    private Bitmap image=null;
    private URL urlobj=null;
    private HttpsURLConnection httpsURLConnection;
    private InputStream inputStream;
    private String url;

    public DownloadImage(Handler handler, String url) {
        this.handler=handler;
        this.url=url;
    }

    @Override
    public void run() {
        synchronized (handler){
        try {
            urlobj=new URL(url);
            httpsURLConnection= (HttpsURLConnection) urlobj.openConnection();
            httpsURLConnection.connect();
            inputStream=httpsURLConnection.getInputStream();
            image= BitmapFactory.decodeStream(inputStream);
            Bundle bundle=new Bundle();
            bundle.putParcelable("ImageBitmap",image);
            Message msg =handler.obtainMessage();
            msg.setData(bundle);
            handler.handleMessage(msg);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
    }
}
