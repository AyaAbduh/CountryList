package com.example.countrylist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    ArrayList<HashMap<String, String>> countryList;
    TextView TextViewRank;
    TextView TextViewPopulation;
    TextView TextViewCountryName;
    ImageView Flag;
    Button BtnNext;
    Button BtnPrevious;
    private  Handler handler;
    DownloadImage imageThread;
    private Bitmap image;
    private ArrayList<String> imageList;
    private int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        countryList = new ArrayList<>();
        String url="https://www.androidbegin.com/tutorial/jsonparsetutorial.txt";
        TextViewPopulation=findViewById(R.id.population);
        TextViewCountryName=findViewById(R.id.Country);
        TextViewRank=findViewById(R.id.rank);
        BtnNext=findViewById(R.id.Next);
        BtnPrevious=findViewById(R.id.previous);
        Flag=findViewById(R.id.countryFlag);
        imageList=new ArrayList<String>();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                image =msg.getData().getParcelable("ImageBitmap");
                imageList.add(BitMapToString(image));
                //Flag.setImageBitmap(image);
            }
        };
        new JsonTask().execute(url);
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
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
       // return countryList;
    }
    private class JsonTask extends AsyncTask<String, Void, Void> {

        @Override
        protected synchronized Void doInBackground(String... urls) {
           // HashMap<String,String> Country=null;
            try {
                    Download(urls[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
           return null;
        }
        @Override
        protected void onPostExecute( Void result) {
            //get values from hashmap on GUI
            TextViewCountryName.setText(countryList.get(i).get("countryName"));
            TextViewRank.setText(countryList.get(i).get("rank"));
            TextViewPopulation.setText(countryList.get(i).get("population"));
            Flag.setImageBitmap(StringToBitMap(imageList.get(i)));
            BtnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(i+1==countryList.size()){
                       Toast.makeText(MainActivity.this, "That is the last country", Toast.LENGTH_SHORT).show();
                   }
                   else{
                        i++;
                        TextViewCountryName.setText(countryList.get(i).get("countryName"));
                        TextViewRank.setText(countryList.get(i).get("rank"));
                        TextViewPopulation.setText(countryList.get(i).get("population"));
                       // Flag.setImageBitmap(StringToBitMap(countryList.get(i).get("flag")));
                        Flag.setImageBitmap(StringToBitMap(imageList.get(i)));

                   }}});
            BtnPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(i==0){
                        Toast.makeText(MainActivity.this, "That is the first Country", Toast.LENGTH_LONG).show();
                    }else{
                        i--;
                        TextViewCountryName.setText(countryList.get(i).get("countryName"));
                        TextViewRank.setText(countryList.get(i).get("rank"));
                        TextViewPopulation.setText(countryList.get(i).get("population"));
                       // Flag.setImageBitmap(StringToBitMap(countryList.get(i).get("flag")));
                        Flag.setImageBitmap(StringToBitMap(imageList.get(i)));
                    }
                }
            });
        }


    }
}
