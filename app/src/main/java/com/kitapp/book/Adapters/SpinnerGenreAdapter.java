package com.kitapp.book.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kitapp.book.Models.Genre;
import com.kitapp.book.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 04.07.2017.
 */

public class SpinnerGenreAdapter extends BaseAdapter {

    ArrayList<Genre> genres;
    Context parentContext;

    public SpinnerGenreAdapter(Context context, ArrayList<Genre> genres) {
        this.genres = genres;
        parentContext = context;
    }

    @Override
    public int getCount() {
        return genres.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spinner_genre_item, parent, false);

            vh = new ViewHolder();
            vh.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);


            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.titleTextView.setText(genres.get(position).getTitle());

        return convertView;
    }

    static class ViewHolder {
        TextView titleTextView;

    }
}
