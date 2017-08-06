package com.kitapp.book.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kitapp.book.Activities.BookListActivity;
import com.kitapp.book.Fragments.BookListFragment;
import com.kitapp.book.Fragments.CityListFragment;
import com.kitapp.book.Fragments.GenreListFragment;
import com.kitapp.book.Models.City;
import com.kitapp.book.Models.Genre;
import com.kitapp.book.R;

import java.util.List;

/**
 * Created by Admin on 04.08.2017.
 */

public class RecyclerCityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context parentContext;
    List<City> cities;
    Fragment parentFragment;



    public RecyclerCityAdapter (Fragment fragment, List<City> cities){
        this.cities = cities;
        parentFragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentContext = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View cityView = inflater.inflate(R.layout.city_item, parent, false);

        CityViewHolder cityViewHolder = new CityViewHolder(cityView);


        return cityViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        CityViewHolder vh = (CityViewHolder) holder;

        if (cities.get(position)==null){
            vh.textView.setText(parentContext.getString(R.string.wholeKazakhstan));
        } else {
            vh.textView.setText(cities.get(position).getTitle());

        }

    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public class CityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        TextView textView;

        public CityViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ((CityListFragment)parentFragment).cityClicked(cities.get(getAdapterPosition()), getAdapterPosition());

        }
    }
}
