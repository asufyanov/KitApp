package com.kitapp.book;

import com.kitapp.book.Models.City;
import com.kitapp.book.Models.Genre;

import java.util.ArrayList;

/**
 * Created by Admin on 09.07.2017.
 */

public class MyDataHolder {
    public static final int MAX_PAGE_LOAD = 15;
   private static final MyDataHolder ourInstance = new MyDataHolder();
    public Boolean loadingGenres = false;
    public ArrayList<Genre> genreTitles = null;
    public ArrayList<City> cities = null;


    private MyDataHolder() {
    }

    public static MyDataHolder getInstance() {

        return ourInstance;
    }

}
