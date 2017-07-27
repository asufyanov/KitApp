package com.kitapp.book.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.kitapp.book.Adapters.MyNavigationDrawer;
import com.kitapp.book.Adapters.MySearchView;
import com.kitapp.book.Adapters.ViewPagerAdapter;
import com.kitapp.book.Fragments.BookListFragment;
import com.kitapp.book.Fragments.GenreListFragment;
import com.kitapp.book.R;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    BookListFragment blf = new BookListFragment();
    GenreListFragment glf = new GenreListFragment();
    FloatingActionButton fab;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    SearchView searchView;

    CountDownTimer timer;
    String prevSearch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setReferences();


    }



    private void setReferences() {



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddBookActivity.class);
                startActivity(intent);
            }
        });



        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        MyNavigationDrawer.createDrawer(this, toolbar);

        Bundle bundle = getIntent().getExtras();


        if (bundle != null) {

            int command;
            command = bundle.getInt("command");
            if (command == 5) {
                selectPage(1);
            }
        }

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(blf, getString(R.string.books));
        adapter.addFragment(glf, getString(R.string.genres));
        //adapter.addFragment(new BookListFragment(), "Calls");
        viewPager.setAdapter(adapter);

    }
    public void selectPage(int pageIndex) {
        tabLayout.setScrollPosition(pageIndex, 0f, true);
        viewPager.setCurrentItem(pageIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MySearchView.createMySearchView(this, menu, searchView, blf, glf);
        blf.onRefresh();


        return true;
    }
}
