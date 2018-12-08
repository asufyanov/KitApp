package com.kitapp.book.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.view.MenuItem;
import android.view.View;

import com.kitapp.book.Adapters.MyNavigationDrawer;
import com.kitapp.book.Adapters.MySearchView;
import com.kitapp.book.Adapters.ViewPagerAdapter;
import com.kitapp.book.Fragments.BookListFragment;
import com.kitapp.book.Fragments.GenreListFragment;
import com.kitapp.book.R;
import com.mikepenz.materialdrawer.Drawer;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_CITY_REQUEST_CODE = 4;


    Toolbar toolbar;

    BookListFragment blf = new BookListFragment();
    GenreListFragment glf = new GenreListFragment();
    FloatingActionButton fab;
    SearchView searchView;
    CountDownTimer timer;
    String prevSearch = null;
    Drawer drawer;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setReferences();


    }


    private void setReferences() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


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

        drawer = MyNavigationDrawer.createDrawer(this, toolbar);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.filterMenuItem){
            //Intent intent = new Intent(this, )
            Intent intent = new Intent(this, SelectCityActivity.class);

            blf.startActivityForResult(intent, SELECT_CITY_REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (drawer != null) MyNavigationDrawer.updateNameSurname(drawer);

    }
}
