package com.kitapp.book.Adapters;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;

import com.kitapp.book.Activities.MainActivity;
import com.kitapp.book.Fragments.BookListFragment;
import com.kitapp.book.Fragments.GenreListFragment;
import com.kitapp.book.R;

import java.util.Timer;

import static android.content.Context.SEARCH_SERVICE;

/**
 * Created by Admin on 04.07.2017.
 */

public class MySearchView {


    public static void createMySearchView(final Activity act, Menu menu, SearchView searchView, final BookListFragment blf, GenreListFragment glf){
        final CountDownTimer[] timer = new CountDownTimer[1];
        final String[] prevSearch = new String[1];
        act.getMenuInflater().inflate(R.menu.main_menu, menu);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        blf.setSearchView(searchView);
        if (glf!=null) glf.setSearchView(searchView);



        SearchManager searchManager = (SearchManager) act.getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(act.getComponentName()));


        final SearchView finalSearchView = searchView;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                //selectPage(0);
                if (act instanceof MainActivity){
                    MainActivity ma = (MainActivity) act;
                    ma.selectPage(0);
                }


                int size = blf.getBooks().size();
                blf.getBooks().clear();
                blf.getAdapter().notifyItemRangeRemoved(0, size);

                //blf.addBooksToRecycleView(0, finalSearchView.getQuery().toString(), 1, "onQuerySubmit");
                blf.onRefresh();
                finalSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                prevSearch[0] = newText;
                if (prevSearch[0] == null) {

                    return false;
                }
                if (timer[0] == null) {
                    timer[0] = new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            blf.onRefresh();

                        }
                    }.start();
                } else {
                    timer[0].cancel();
                    timer[0] = new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            blf.onRefresh();
                        }
                    }.start();
                }
                if (finalSearchView.getQuery().toString().length() == 0) {
                    blf.onRefresh();
                    finalSearchView.clearFocus();

                    finalSearchView.setIconified(true);


                }
                return false;
            }
        });

    }
}
