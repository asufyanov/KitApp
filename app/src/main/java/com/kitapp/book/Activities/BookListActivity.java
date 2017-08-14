package com.kitapp.book.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.backendless.BackendlessUser;
import com.google.gson.Gson;
import com.kitapp.book.Adapters.MyNavigationDrawer;
import com.kitapp.book.Adapters.MySearchView;
import com.kitapp.book.Fragments.BookListFragment;
import com.kitapp.book.Models.City;
import com.kitapp.book.Models.Genre;
import com.kitapp.book.R;

public class BookListActivity extends AppCompatActivity {

    private static final int SELECT_CITY_REQUEST_CODE = 4 ;
    Toolbar toolbar;
    FloatingActionButton fab;
    SearchView searchView;
    String searchString = "";


    long lastSearchTime;
    CountDownTimer timer;
    String prevSearch = null;

    BookListFragment blf = new BookListFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        setReferences();
        setSearchFilters();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        if (item.getItemId()==R.id.filterMenuItem){
            //Intent intent = new Intent(this, )
            Intent intent = new Intent(this, SelectCityActivity.class);

            blf.startActivityForResult(intent, SELECT_CITY_REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MySearchView.createMySearchView(this, menu, searchView, blf, null);
        blf.onRefresh();
        return true;

    }



    private void setReferences() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        blf = new BookListFragment();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddBookActivity.class));
            }
        });


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.all_books));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        openFragment();

        MyNavigationDrawer.createDrawer(this, toolbar);

    }

    private void openFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frameLayout, blf);
        ft.commit();

    }

    private void setSearchFilters() {
        //      1   Все Книги
        //      2   Мои Книги
        //      3   Избранные Книги
        int command = 0;
        Bundle bundle = getIntent().getExtras();


        if (bundle != null) {
            command = bundle.getInt("command");


            if (command == 1) {
                blf.myBooks = false;
                blf.myLikedBooks = false;
                toolbar.setTitle(getString(R.string.all_books));
            } else if (command == 2) {
                toolbar.setTitle(getString(R.string.my_books));
                blf.myBooks = true;
                blf.myLikedBooks = false;
            } else if (command == 3) {
                toolbar.setTitle(getString(R.string.favorite_books));

                blf.myLikedBooks = true;
                blf.myBooks = false;
            } else {
                blf.myBooks = false;
                blf.myLikedBooks = false;
                toolbar.setTitle(getString(R.string.all_books));
            }

            if (getIntent().getSerializableExtra("genre") != null) {
                blf.searchGenre = (Genre) getIntent().getSerializableExtra("genre");
                toolbar.setTitle(blf.searchGenre.getTitle());
            }

            if (getIntent().getSerializableExtra("owner") != null) {
                blf.searchOwner = (BackendlessUser) getIntent().getSerializableExtra("owner");
                String nameSurname = "";
                if (blf.searchOwner.getProperty("name") != null)
                    nameSurname += blf.searchOwner.getProperty("name") + " ";
                if (blf.searchOwner.getProperty("surname") != null)
                    nameSurname += blf.searchOwner.getProperty("surname");
                if (nameSurname.equals("")) nameSurname = getString(R.string.users_books);
                toolbar.setTitle(getString(R.string.books) + ": " + nameSurname);
            }

        }


    }

}
