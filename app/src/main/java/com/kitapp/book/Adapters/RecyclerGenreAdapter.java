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
import com.kitapp.book.Fragments.GenreListFragment;
import com.kitapp.book.Models.Genre;
import com.kitapp.book.R;

import java.util.List;

/**
 * Created by Admin on 20.06.2017.
 */

public class RecyclerGenreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Genre> genres;
    Context parentContext;
    Fragment parentFragment;

    public RecyclerGenreAdapter(Fragment fragment, List<Genre> genres) {
        this.genres = genres;
        parentContext = fragment.getActivity();
        parentFragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.genre_item, parent, false);

        // Return a new holder instance
        GenreViewHolder viewHolder = new GenreViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        GenreViewHolder vh = (GenreViewHolder) holder;
        vh.textView.setText(genres.get(position).getTitle());



    }


    @Override
    public int getItemCount() {
        return genres.size();
    }


    public class GenreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;


        public GenreViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            ((GenreListFragment)parentFragment).genreClicked(genres.get(getAdapterPosition()), getAdapterPosition());


        }
    }
}
