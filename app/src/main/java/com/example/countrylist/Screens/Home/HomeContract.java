package com.example.countrylist.Screens.Home;

import java.util.ArrayList;
import java.util.HashMap;

public interface HomeContract {
    interface HomeView{
        void showCourntry(ArrayList<HashMap<String, String>> countryList ,ArrayList<String> imageList);
    }
    interface HomePresenter{
        void sendDownloadedCountries(  ArrayList<HashMap<String, String>> countryList ,ArrayList<String> imageList);
    }
}
