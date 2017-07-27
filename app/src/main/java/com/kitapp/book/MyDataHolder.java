package com.kitapp.book;

import android.util.Log;

import com.kitapp.book.Models.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 09.07.2017.
 */

public class MyDataHolder {
   private static final MyDataHolder ourInstance = new MyDataHolder();

    public Boolean loadingGenres = false;
    public ArrayList<Genre> genreTitles = null;






    public static MyDataHolder getInstance() {

        return ourInstance;
    }

    private MyDataHolder() {
    }

}
