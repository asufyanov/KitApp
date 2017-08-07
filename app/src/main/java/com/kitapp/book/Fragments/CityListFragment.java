package com.kitapp.book.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kitapp.book.Activities.AddBookActivity;
import com.kitapp.book.Activities.SelectCityActivity;
import com.kitapp.book.Adapters.RecyclerCityAdapter;
import com.kitapp.book.Models.City;
import com.kitapp.book.MyDataHolder;
import com.kitapp.book.R;

import java.util.ArrayList;
import java.util.List;


public class CityListFragment extends Fragment {

    RecyclerView recyclerView;
    List<City> cities;
    RecyclerCityAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    public CityListFragment() {
        // Required empty public constructor
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_city_list, container, false);

        setReferences(v);

        return v;
    }


    private void setReferences(View v) {
        recyclerView = (RecyclerView)v.findViewById(R.id.swipe_target);

        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        cities = new ArrayList<>();

        adapter = new RecyclerCityAdapter(this, cities);

        recyclerView.setAdapter(adapter);
        cities.addAll(MyDataHolder.getInstance().cities);

        SelectCityActivity act = (SelectCityActivity)getActivity();
        if (act.getRemoveAllKz()==true) cities.remove(0);


        adapter.notifyDataSetChanged();
    }

    public void cityClicked(City city, int adapterPostition){
        Intent intent = new Intent();
        intent.putExtra("city", new Gson().toJson(city));
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
