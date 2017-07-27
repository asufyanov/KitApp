package com.kitapp.book.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kitapp.book.Adapters.MyNavigationDrawer;
import com.kitapp.book.Models.Book;
import com.kitapp.book.R;

import java.util.ArrayList;
import java.util.List;

public class DisplayBookActivity extends AppCompatActivity {


    TextView descriptionTextView;
    ImageView imageView;
    TextView priceTextView;
    TextView ownerTextView;
    TextView genreTextView;
    TextView authorTextView;
    Book book;
    Boolean isLiked = null;

    MenuItem likeBtn;
    Button callBtn;

    FloatingActionButton fab;

    Toolbar toolbar;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_book);

        setReferences();
        MyNavigationDrawer.createDrawer(this, toolbar);
    }
    private void setReferences() {



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        Intent i = getIntent();
        Bundle b = i.getExtras();
        String bookAsString = getIntent().getExtras().getString("book");

        book = new Gson().fromJson(bookAsString, Book.class);
        BackendlessUser user = book.getOwner();
        phone = (String) book.getOwner().getProperty("username");


        callBtn = (Button)findViewById(R.id.callBtn);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);

        imageView = (ImageView) findViewById(R.id.bookImageView);
        priceTextView = (TextView) findViewById(R.id.priceView);
        authorTextView = (TextView) findViewById(R.id.authorTextView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        ownerTextView = (TextView) findViewById(R.id.ownerTextView);
        genreTextView = (TextView) findViewById(R.id.genreTextView);

        ownerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookListActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (book.getOwner() == null) return;
                intent.putExtra("owner", book.getOwner());
                startActivity(intent);
            }
        });
        genreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookListActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (book.getGenre() == null) return;
                intent.putExtra("genre", book.getGenre());
                startActivity(intent);

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                //Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+"+77012552727"));
                startActivity(intent);
            }
        });
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fab.performClick();
                String uriPhone = phone.substring(1);
                boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");


                if (isWhatsappInstalled) {

                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
                    sendIntent.putExtra("jid",     PhoneNumberUtils.stripSeparators(phone)+"@s.whatsapp.net");//phone number without "+" prefix

                    startActivity(sendIntent);
                } else {
                    Uri uri = Uri.parse("market://details?id=com.whatsapp");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    Toast.makeText(getApplicationContext(), "WhatsApp not Installed",
                            Toast.LENGTH_SHORT).show();
                    startActivity(goToMarket);
                }



            }
        });


        if (user == null) Log.d("displayBook", "owner==null");
        else Log.d("displayBook", user.toString());

        authorTextView.setText(getString(R.string.author)+": "+ book.getAuthor());

        priceTextView.setText(book.getPrice() + " "+ getString(R.string.tenge));

        getSupportActionBar().setTitle(book.getTitle());


        //Забиваем ВЛАДЕЛЬЦА
        String name = "";
        String tempName = (String) book.getOwner().getProperty("name");
        String tempSurname = (String) book.getOwner().getProperty("surname");
        if (tempName != null) name += (String) book.getOwner().getProperty("name");
        if (tempSurname != null) name += " " + (String) book.getOwner().getProperty("surname");
        if (name.length() < 1) name = (String) book.getOwner().getProperty("username");
        ownerTextView.setText(ownerTextView.getText()+" "+ name+"");

        //ЗАБИВАЕМ ЖАНР
        String genre = "";
        if (book.getGenre()!=null) genre += book.getGenre().getTitle();
        if (genre.length() == 0) genre = getString(R.string.not_entered);
        genreTextView.setText(getString(R.string.genre)+ ": " + genre);

        descriptionTextView.setText(book.getDescr());
        Glide
                .with(this)
                //.load(books.get(position).getImage())
                .load(book.getImage())
                .fitCenter()
                .placeholder(R.drawable.bookplaceholder)
                .crossFade()
                .into(imageView);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.displaybook_menu, menu);
        likeBtn = menu.findItem(R.id.likeMenuItem);
        likeBtn.setActionView(new ProgressBar(this));
        likeBtn.setEnabled(false);

        String curUserObjectId = Backendless.UserService.CurrentUser().getObjectId();

        String whereClause = "bookmarks.objectId="+"'"+book.getObjectId()+"' and objectId='"+curUserObjectId+"'";
        DataQueryBuilder dataQueryBuilder = DataQueryBuilder.create();
        dataQueryBuilder.setSortBy("created DESC");
        dataQueryBuilder.setWhereClause(whereClause);

        Backendless.Data.of(BackendlessUser.class).find(dataQueryBuilder, new AsyncCallback<List<BackendlessUser>>() {
            @Override
            public void handleResponse(List<BackendlessUser> response) {
                likeBtn.setEnabled(true);
                likeBtn.setActionView(null);
                if (response.size()!=0){
                    likeBtn.setIcon(getResources().getDrawable(R.mipmap.heart_filled));
                    isLiked = true;
                } else {
                    likeBtn.setIcon(getResources().getDrawable(R.mipmap.heart_empty));
                    isLiked = false;

                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });

        likeBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (!isLiked) like(book);
                else unlike(book);

                return false;
            }
        });

        return true;
    }

    private void like(Book book) {
        likeBtn.setEnabled(false);
        likeBtn.setActionView(new ProgressBar(this));

        ArrayList<Book> listRelation = new ArrayList<>();
        listRelation.add(book);

        Backendless.Data.of(BackendlessUser.class).addRelation(Backendless.UserService.CurrentUser(), "bookmarks:Book:n", listRelation, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse(Integer response) {

                isLiked = true;
                likeBtn.setEnabled(true);
                likeBtn.setActionView(null);
                likeBtn.setIcon(getResources().getDrawable(R.mipmap.heart_filled));
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                isLiked = false;
                likeBtn.setEnabled(true);
                likeBtn.setActionView(null);
                likeBtn.setIcon(getResources().getDrawable(R.mipmap.heart_empty));

            }
        });


    }

    private void unlike (Book book) {
        likeBtn.setEnabled(false);
        likeBtn.setActionView(new ProgressBar(this));

        ArrayList<Book> listRelation = new ArrayList<>();
        listRelation.add(book);

        Backendless.Data.of(BackendlessUser.class).deleteRelation(Backendless.UserService.CurrentUser(), "bookmarks", listRelation, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse(Integer response) {

                isLiked = false;
                likeBtn.setEnabled(true);
                likeBtn.setActionView(null);
                likeBtn.setIcon(getResources().getDrawable(R.mipmap.heart_empty));
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                isLiked = true;
                likeBtn.setEnabled(true);
                likeBtn.setActionView(null);
                likeBtn.setIcon(getResources().getDrawable(R.mipmap.heart_filled));

            }
        });

    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

}
