package com.kitapp.book.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.google.gson.Gson;
import com.kitapp.book.Activities.BookListActivity;
import com.kitapp.book.Activities.MainActivity;
import com.kitapp.book.Activities.SelectGenreActivity;
import com.kitapp.book.Adapters.RecyclerGenreAdapter;
import com.kitapp.book.Models.Genre;
import com.kitapp.book.MyDataHolder;
import com.kitapp.book.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenreListFragment extends Fragment  {


    RecyclerView recyclerView;
    List<Genre> genres;
    RecyclerGenreAdapter adapter;
    LinearLayoutManager mLayoutManager;


    SearchView searchView;


    public GenreListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_genre_list, container, false);

        setReferences(v);

        return v;
    }

    private void setReferences(View v) {




        recyclerView = (RecyclerView) v.findViewById(R.id.swipe_target);


        // если мы уверены, что изменения в контенте не изменят размер layout-а RecyclerView
        // передаем параметр true - это увеличивает производительность
        recyclerView.setHasFixedSize(true);

        // используем linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setLayoutManager(mLayoutManager);


        genres = new ArrayList<>();
        adapter = new RecyclerGenreAdapter(this, genres);


        recyclerView.setAdapter(adapter);

        genres.addAll(MyDataHolder.getInstance().genreTitles);
        adapter.notifyDataSetChanged();

        //addGenresToRecycleView(0, "", 1);

    }

    public void genreClicked (Genre genre, int adapterPosition){

        if (getActivity() instanceof MainActivity){
            Log.d("Aziz", "GENRE CLICKED");
            Intent intent = new Intent(getActivity(), BookListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("genre", genres.get(adapterPosition));
            getActivity().startActivity(intent);
        } else if (getActivity() instanceof SelectGenreActivity){
            Intent intent = new Intent();
            //intent.putExtra("genre", genre);
            intent.putExtra("genre", new Gson().toJson(genre));
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
        }

    }


    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
    }


}
