package com.kitapp.book.Activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kitapp.book.Fragments.CityListFragment;
import com.kitapp.book.R;

public class SelectCityActivity extends AppCompatActivity {
    Toolbar toolbar;
    CityListFragment clf = new CityListFragment();



    Boolean removeAllKz = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        setReferences();

    }

    private void setReferences(){


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.select_city));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        if (getIntent().getExtras()!=null){
            if (getIntent().getExtras().getBoolean("remove")==true){
                removeAllKz = true;
            }
        }

        openFragment();


    }

    public Boolean getRemoveAllKz() {
        return removeAllKz;
    }

    private void openFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frameLayout, clf);
        ft.commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
