package com.example.countrylist.Screens.Home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.countrylist.R;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class HomeActivity extends AppCompatActivity implements HomeContract.HomeView{

    ArrayList<HashMap<String, String>> countryList;
    TextView TextViewRank;
    TextView TextViewPopulation;
    TextView TextViewCountryName;
    ImageView Flag;
    Button BtnNext;
    Button BtnPrevious;
    private ArrayList<String> imageList;
    private HomePresenterImpl presenter;
    private int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String url="https://www.androidbegin.com/tutorial/jsonparsetutorial.txt";
        TextViewPopulation=findViewById(R.id.population);
        TextViewCountryName=findViewById(R.id.Country);
        TextViewRank=findViewById(R.id.rank);
        BtnNext=findViewById(R.id.Next);
        BtnPrevious=findViewById(R.id.previous);
        Flag=findViewById(R.id.countryFlag);
        presenter =new HomePresenterImpl(this,url);
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

    @Override
    public void showCourntry(ArrayList<HashMap<String, String>> ListOfcountry ,ArrayList<String> ListOfimages) {
        this.countryList=ListOfcountry;
        this.imageList=ListOfimages;
        //get values from hashmap on GUI
        TextViewCountryName.setText(countryList.get(i).get("countryName"));
        TextViewRank.setText(countryList.get(i).get("rank"));
        TextViewPopulation.setText(countryList.get(i).get("population"));
        Flag.setImageBitmap(StringToBitMap(imageList.get(i)));
        BtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i+1==countryList.size()){
                    Toast.makeText(HomeActivity.this, "That is the last country", Toast.LENGTH_SHORT).show();
                }
                else{
                    i++;
                    TextViewCountryName.setText(countryList.get(i).get("countryName"));
                    TextViewRank.setText(countryList.get(i).get("rank"));
                    TextViewPopulation.setText(countryList.get(i).get("population"));
                    Flag.setImageBitmap(StringToBitMap(imageList.get(i)));

                }}});
        BtnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i==0){
                    Toast.makeText(HomeActivity.this, "That is the first Country", Toast.LENGTH_LONG).show();
                }else{
                    i--;
                    TextViewCountryName.setText(countryList.get(i).get("countryName"));
                    TextViewRank.setText(countryList.get(i).get("rank"));
                    TextViewPopulation.setText(countryList.get(i).get("population"));
                    Flag.setImageBitmap(StringToBitMap(imageList.get(i)));
                }
            }
        });

    }

}
