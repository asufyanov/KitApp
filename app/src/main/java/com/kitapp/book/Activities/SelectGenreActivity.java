package com.kitapp.book.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kitapp.book.Adapters.MyNavigationDrawer;
import com.kitapp.book.Fragments.BookListFragment;
import com.kitapp.book.Fragments.GenreListFragment;
import com.kitapp.book.R;

public class SelectGenreActivity extends AppCompatActivity {

    GenreListFragment glf = new GenreListFragment();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_genre);
        setReferences();
    }

    private void setReferences(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.select_genre));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        openFragment();


    }

    private void openFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frameLayout, glf);
        ft.commit();

    }
}
