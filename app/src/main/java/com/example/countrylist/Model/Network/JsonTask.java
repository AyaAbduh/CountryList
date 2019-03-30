package com.example.countrylist.Model.Network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.countrylist.Screens.Home.HomePresenterImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javax.net.ssl.HttpsURLConnection;


public class JsonTask extends AsyncTask<String, Void, Void> {

    private ArrayList<HashMap<String, String>> countryList=new ArrayList<>();
    private ArrayList<String> imageList=new ArrayList<>();
    private  Handler handler;
    private Bitmap image;
    private DownloadImage imageThread;
    private HomePresenterImpl presenter;

    public JsonTask(HomePresenterImpl presenter){
        this.presenter=presenter;
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                image =msg.getData().getParcelable("ImageBitmap");
                imageList.add(BitMapToString(image));
            }
        };
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    void  Download(String url) throws IOException {
        HashMap<String,String> country=null;
        URL urlobj=null;
        HttpsURLConnection httpsURLConnection;
        InputStream inputStream=null;
        String JsonString=null;
        try {
            urlobj=new URL(url);
            httpsURLConnection= (HttpsURLConnection) urlobj.openConnection();
            httpsURLConnection.connect();
            inputStream=httpsURLConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            JsonString=stringBuilder.toString();
            if (JsonString != null) {
                try {
                    JSONObject jsonObj = new JSONObject(JsonString);
                    JSONArray countries = jsonObj.getJSONArray("worldpopulation");
                    for (int i = 0; i < countries.length(); i++) {
                        JSONObject countryObject = countries.getJSONObject(i);
                        String rank=countryObject.getString("rank");
                        String countryName=countryObject.getString("country");
                        String population=countryObject.getString("population");
                        String flag=countryObject.getString("flag");
                        //convert from http to https
                        flag=flag.replace("http","https");
                        synchronized (handler) {
                            //call handler
                            imageThread = new DownloadImage(handler, flag);
                            Thread thread=new Thread(imageThread);
                            thread.start();

                        }
                        // hash map for single country
                        country = new HashMap<String,String>();
                        // adding each child node to HashMap key => value
                        country.put("rank", rank);
                        country.put("countryName", countryName);
                        country.put("population", population);
                        if(image!=null){
                            country.put("flag", BitMapToString(image));
                        }
                        // adding country to country list
                        countryList.add(country);
                        // System.out.println("Rank"+rank);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected synchronized Void doInBackground(String... urls) {
        try {
            Download(urls[0]);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute( Void result) {
        presenter.sendDownloadedCountries(countryList,imageList);
    }

}
