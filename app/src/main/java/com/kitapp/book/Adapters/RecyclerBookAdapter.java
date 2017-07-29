package com.kitapp.book.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kitapp.book.Activities.AddBookActivity;
import com.kitapp.book.Activities.DisplayBookActivity;
import com.kitapp.book.Fragments.BookListFragment;
import com.kitapp.book.Models.Book;
import com.kitapp.book.R;

import java.util.List;

/**
 * Created by Admin on 19.06.2017.
 */

public class RecyclerBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    boolean loading=false;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    Context parentContext;
    Fragment parentFragment;

    private List<Book> books;

    // Конструктор
    public RecyclerBookAdapter(Fragment fragment, final List<Book> books, RecyclerView recyclerView) {
        parentFragment = fragment;
        parentContext = fragment.getActivity();
        this.books = books;

            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (books.isEmpty()) return;
                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!loading && books.get(books.size()-1)!=null && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            // End has been reached
                            // Do something
                            Log.d("AzizAdapter", totalItemCount+" "+lastVisibleItem+loading);

                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }
                });
            }


    }

    @Override
    public int getItemViewType(int position) {
        return books.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;

        if (viewType == VIEW_ITEM) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.book_item, parent, false);

            // тут можно программно менять атрибуты лэйаута (size, margins, paddings и др.)

            vh = new BookViewHolder(v);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item,parent,false);
            vh = new ProgressViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof BookViewHolder) {
            final BookViewHolder vh = (BookViewHolder) holder;
            vh.bookTitleTextView.setText(books.get(position).getTitle());
            if (books.get(position).getPrice()==0){
                vh.bookPriceTextView.setText(parentContext.getString(R.string.chocolate));

            } else {
                vh.bookPriceTextView.setText("" + books.get(position).getPrice()+" "+parentContext.getString(R.string.tenge).toLowerCase());

            }
            //vh.bookPriceTextView.setText("" + books.get(position).getGenre().getTitle());


            vh.bookAuthorTextView.setText(books.get(position).getAuthor());
            Glide
                    .with(parentContext)
                    //.load(books.get(position).getImage())
                    .load(books.get(position).getImage())
                    .fitCenter()
                    .placeholder(R.drawable.bookplaceholder)
                    .crossFade()
                    .into(vh.imageView);
            if (books.get(position).getOwner()==null){
                vh.deleteBtn.setVisibility(View.GONE);
                vh.editBtn.setVisibility(View.GONE);
            }
            else if (!Backendless.UserService.CurrentUser().getObjectId().equals(books.get(position).getOwner().getObjectId())) {
                vh.deleteBtn.setVisibility(View.GONE);
                vh.editBtn.setVisibility(View.GONE);
            } else {
                vh.deleteBtn.setVisibility(View.VISIBLE);
                vh.editBtn.setVisibility(View.VISIBLE);

            }

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }



    @Override
    public int getItemCount() {
        if (books == null) return 0;
        return books.size();
    }

    public void setLoaded(Boolean a) {
        loading = !a;
    }

    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // наш пункт состоит только из одного TextView
        TextView bookTitleTextView;
        TextView bookAuthorTextView;
        TextView bookPriceTextView;
        ImageButton deleteBtn;
        ImageButton editBtn;



        ImageView imageView;

        public BookViewHolder(View v) {
            super(v);
            bookTitleTextView = (TextView) v.findViewById(R.id.bookTitleId);
            bookAuthorTextView = (TextView) v.findViewById(R.id.bookAuthorId);
            bookPriceTextView = (TextView) v.findViewById(R.id.bookPriceId);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            deleteBtn = (ImageButton) v.findViewById(R.id.deleteBtn);
            editBtn = (ImageButton) v.findViewById(R.id.editBtn);


            itemView.setOnClickListener(this);
            editBtn.setOnClickListener(this);
            deleteBtn.setOnClickListener(this);
            //imageView.setOnClickListener(this);





        }

        @Override
        public void onClick(View v) {
            Log.d("Aziz", "VIEW CLICKED = "+v.getId());
            if (v.getId() == deleteBtn.getId()){

                ((BookListFragment)parentFragment).deleteBook(books.get(getAdapterPosition()), getAdapterPosition());


            } else if (v.getId() == editBtn.getId()){
                String bookAsString = new Gson().toJson(books.get(getAdapterPosition()));
                Intent intent = new Intent(parentContext, AddBookActivity.class);

                intent.putExtra("book", bookAsString);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                parentContext.startActivity(intent);

            } else  {
                Book book = books.get(getAdapterPosition());
                Intent intent = new Intent(v.getContext(), DisplayBookActivity.class);

                Bundle b = new Bundle();

                String bookAsString = new Gson().toJson(book);
                intent.putExtra("book", bookAsString);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                v.getContext().startActivity(intent);
                Log.d("AzizListener", "CLICKED");
            }
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder{

        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
}
