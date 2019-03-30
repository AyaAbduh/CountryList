package com.example.countrylist.Screens.Home;


import com.example.countrylist.Model.Network.JsonTask;

import java.util.ArrayList;
import java.util.HashMap;


public class HomePresenterImpl implements  HomeContract.HomePresenter{

    private HomeActivity home;

    public HomePresenterImpl(HomeActivity home,String url){
        this.home=home;
        new JsonTask(this).execute(url);
    }

    @Override
    public void sendDownloadedCountries(ArrayList<HashMap<String, String>> countryList, ArrayList<String> imageList) {
        home.showCourntry(countryList,imageList);
    }
}
