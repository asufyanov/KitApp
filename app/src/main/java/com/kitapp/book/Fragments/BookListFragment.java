package com.kitapp.book.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.google.gson.Gson;
import com.kitapp.book.Activities.AddBookActivity;
import com.kitapp.book.Activities.MainActivity;
import com.kitapp.book.Activities.SelectCityActivity;
import com.kitapp.book.Adapters.RecyclerBookAdapter;
import com.kitapp.book.Models.Book;
import com.kitapp.book.Models.City;
import com.kitapp.book.Models.Genre;
import com.kitapp.book.R;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookListFragment extends Fragment {

    private static final int SELECT_CITY_REQUEST_CODE = 4;
    public Boolean myLikedBooks = false;
    public Boolean myBooks = false;
    public Genre searchGenre = null;
    public BackendlessUser searchOwner = null;
    public City searchCity = null;

    RecyclerView recyclerView;
    RecyclerBookAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    ProgressBar progressBar;
    List<Book> books;
    SearchView searchView;
    TextView cityTextView;

    View emptyState;
    Button btnAddBook;

    long lastSearchTime;
    private LinearLayoutManager mLayoutManager;


    public BookListFragment() {
        // Required empty public constructor

        if (getActivity() instanceof MainActivity) {
            MainActivity activity = new MainActivity();
            activity = (MainActivity) getActivity();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_book_list, container, false);
        setReferences(v);
        setCityPref();
        return v;

    }

    private void setCityPref() {
        String prefCityId = Prefs.getString("cityId", null);
        if (prefCityId == null) return;
        String prefCityTitle = Prefs.getString("cityTitle", null);

        searchCity = new City();
        searchCity.setObjectId(prefCityId);
        searchCity.setTitle(prefCityTitle);
        cityTextView.setText(prefCityTitle);

    }


    private void setReferences(View v) {

        cityTextView = (TextView) v.findViewById(R.id.cityTextView);

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BookListFragment.this.onRefresh();


            }
        });
        swipeContainer.setColorSchemeColors(getResources().getColor(R.color.accent));
        emptyState = v.findViewById(R.id.linearlay_empty_state);
        btnAddBook = (Button) v.findViewById(R.id.btn_add_book);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) v.findViewById(R.id.swipe_target);

        // если мы уверены, что изменения в контенте не изменят размер layout-а RecyclerView
        // передаем параметр true - это увеличивает производительность
        recyclerView.setHasFixedSize(true);


        // используем linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        books = new ArrayList<>();
        adapter = new RecyclerBookAdapter(this, books, recyclerView);

        adapter.setOnLoadMoreListener(new RecyclerBookAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                addProgressBarToAdapter();
                int incr = 0;
                if (books.get(books.size() - 1) == null) incr--;
                addBooksToRecycleView(books.size() + incr, searchView.getQuery().toString(), 2, "onLoadMore");

            }
        });

        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddBookActivity.class);
                startActivity(intent);
            }
        });

        cityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectCityActivity.class);

                startActivityForResult(intent, SELECT_CITY_REQUEST_CODE);
            }
        });


        recyclerView.setAdapter(adapter);
    }

    private void addProgressBarToAdapter() {
        books.add(null);
        adapter.notifyItemChanged(books.size() - 1);
        adapter.notifyDataSetChanged();
    }

    private void removeProgressBarFromAdapter() {
        if (books.isEmpty()) return;
        if (books.get(books.size() - 1) != null) return;
        books.remove(books.size() - 1);
        adapter.notifyItemRemoved(books.size());
        adapter.setLoaded(true);
    }


    public void addBooksToRecycleView(int i, String s, int progress, String from) {

        long actualSearchTime = (Calendar.getInstance()).getTimeInMillis();
        //if (actualSearchTime < lastSearchTime + 1000) return;
        emptyState.setVisibility(View.GONE);


        lastSearchTime = (Calendar.getInstance()).getTimeInMillis();

        // 1 - top progress
        // 2 -bot progress

        Log.d("ddBooksToRes", from);


        s = s.trim();

        //Toast.makeText(getApplicationContext(), "STARTED " + s, Toast.LENGTH_SHORT).show();
        String whereClause = "(author LIKE '%" + s + "%'";
        whereClause += " OR title LIKE '%" + s + "%')";

        if (myBooks == true) {
            whereClause += " and owner.objectId='" + Backendless.UserService.CurrentUser().getObjectId() + "'";
        }
        if (myLikedBooks == true) {
            whereClause += " and Users[bookmarks].objectId='" + Backendless.UserService.CurrentUser().getObjectId() + "'";
        }
        if (searchGenre != null) {
            whereClause += " and genre.objectId='" + searchGenre.getObjectId() + "'";
        }
        if (searchOwner != null) {
            whereClause += " and owner.objectId='" + searchOwner.getObjectId() + "'";
        }
        if (searchCity != null) {
            whereClause += " and city.objectId='" + searchCity.getObjectId() + "'";
        }
        whereClause += " and (enabled=true OR enabled is null)";

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        //queryBuilder.setSortBy( "name", "age DESC" );
        queryBuilder.setSortBy("created DESC");

//
        Log.d("addBooksToRes", "offsetAfter=" + i);
        Log.d("addBooksToRes", "whereClause=" + whereClause);

        queryBuilder.setPageSize(10);
        queryBuilder.setOffset(i);


        queryBuilder.setRelated("owner");
        queryBuilder.setRelated("genre");
        queryBuilder.setRelated("city");

        //query.setQueryOptions(qo);
        Backendless.Persistence.of(Book.class).find(queryBuilder, new AsyncCallback<List<Book>>() {
            @Override
            public void handleResponse(List<Book> bookBackendlessCollection) {
                swipeContainer.setRefreshing(false);
                removeProgressBarFromAdapter();

                if (bookBackendlessCollection.size() == 0 && books.size() == 0) {

                    emptyState.setVisibility(View.VISIBLE);

                    //Toast.makeText(getActivity(), getString(R.string.no_books), Toast.LENGTH_SHORT).show();
                } else if (bookBackendlessCollection.size() == 0 && books.size() != 0) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), getString(R.string.no_books_left_else), Toast.LENGTH_SHORT).show();
                    adapter.setLoaded(false);
                }
                int size = books.size();
                books.addAll(bookBackendlessCollection);


                adapter.notifyItemRangeInserted(size, bookBackendlessCollection.size());
                //recyclerView.setAdapter(adapter);


            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(getActivity(), getString(R.string.fail_to_load_books), Toast.LENGTH_SHORT).show();
                Log.d(this.getClass().getSimpleName(), backendlessFault.getMessage() + " " + backendlessFault.getDetail());
                swipeContainer.setRefreshing(false);
                removeProgressBarFromAdapter();
            }

        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_CITY_REQUEST_CODE && resultCode == RESULT_OK){
            if (data!=null){
                City city = new Gson().fromJson(data.getExtras().getString("city"), City.class);

                setSearchCity (city);

            }

        }
    }

    public void onRefresh() {


        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
            }
        });

        adapter.setLoaded(true);
        int bookSize = books.size();
        books.clear();
        adapter.notifyItemRangeRemoved(0, bookSize);
        Log.d("AzizOnRefresh", "searchString = " + searchView.getQuery().toString());

        //swipeToLoadLayout.setRefreshing(false);

        addBooksToRecycleView(0, searchView.getQuery().toString(), 1, "onRefresh");

    }

    //@Override
    public void onLoadMore() {
        addBooksToRecycleView(books.size(), searchView.getQuery().toString(), 2, "onLoadMore");

    }

    public void deleteBook(final Book book, final int adapterPosition) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Удалить?");
        builder.setMessage("Удаленые книги не могут быть востановлены");

        String positiveText = "OK";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        book.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        Backendless.Persistence.of(Book.class).save(book, new AsyncCallback<Book>() {
                            @Override
                            public void handleResponse(Book book) {
                                books.remove(adapterPosition);
                                adapter.notifyItemRemoved(adapterPosition);
                                adapter.notifyItemRangeChanged(adapterPosition, books.size());
                                Log.d("deleBtn", "Книга Удалена");
                                progressBar.setVisibility(View.INVISIBLE);

                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Log.d("deleBtn", "Книгу не удалось удалить");
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    }
                });

        String negativeText = "НЕТ";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    public void editBook(final Book book, final int adapterPosition) {
        String bookAsString = new Gson().toJson(books.get(adapterPosition));
        Intent intent = new Intent(getActivity(), AddBookActivity.class);

        intent.putExtra("book", bookAsString);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);

    }

    public void moreBtnClicked(final Book book, final int adapterPosition) {
        final String[] catNamesArray = {getString(R.string.edit_book), getString(R.string.delete_book)};


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.select_action))
                .setItems(catNamesArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            editBook(book, adapterPosition);
                        }else if (which == 1){
                            deleteBook(book, adapterPosition);
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }


    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
    }

    public RecyclerBookAdapter getAdapter() {
        return adapter;
    }

    public void setSearchCity(City searchCity) {
        if (searchCity!=null){
            Prefs.putString("cityTitle", searchCity.getTitle());
            Prefs.putString("cityId", searchCity.getObjectId());
            this.searchCity = searchCity;
            cityTextView.setText(searchCity.getTitle());
        } else {
            Prefs.remove("cityTitle");
            Prefs.remove("cityId");
            this.searchCity = null;
            cityTextView.setText(getString(R.string.wholeKazakhstan));
        }



        onRefresh();

    }
}
