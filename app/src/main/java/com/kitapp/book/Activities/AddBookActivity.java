package com.kitapp.book.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kitapp.book.Models.Book;
import com.kitapp.book.Models.City;
import com.kitapp.book.Models.Genre;
import com.kitapp.book.MyDataHolder;
import com.kitapp.book.R;
import com.kitapp.book.ServerCalls;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AddBookActivity extends AppCompatActivity {

    private static final int SELECT_GENRE_REQUEST_CODE = 3;
    private static final int SELECT_CITY_REQUEST_CODE = 4;
    protected View view;
    protected ImageView imgViewCamera;
    File sendFile = null;
    EditText titleEditText;
    EditText authorEditText;
    EditText priceEditText;
    EditText descriptionEditText;
    View progressView;
    View loginForm;
    Toolbar toolbar;
    MenuItem saveMenuBtn;

    TextView genreTextView;
    TextView cityTextView;

    Book editedBook = null;
    String oldImageUrl = null;
    ArrayList<Genre> genreList;
    String selectedGenreId = null;
    String selectedCityId = null;
    Uri uri = null;
    private Button btn;
    private BackendlessUser curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        curUser = Backendless.UserService.CurrentUser();
        setReference();
        loadGenres();
        fillEditedBook();


    }


    public void setReference() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_add_book);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.add_book));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        imgViewCamera = (ImageView) findViewById(R.id.img_camera);
        titleEditText = (EditText) findViewById(R.id.editTextTitle);
        authorEditText = (EditText) findViewById(R.id.editTextAuthor);
        priceEditText = (EditText) findViewById(R.id.editTextPrice);
        descriptionEditText = (EditText) findViewById(R.id.editTextDescription);
        progressView = findViewById(R.id.progressBar);
        loginForm = findViewById(R.id.login_form);
        btn = (Button) findViewById(R.id.saveBtn);
        genreTextView = (TextView) findViewById(R.id.genreTextView);
        cityTextView = (TextView) findViewById(R.id.cityTextView);
        genreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), SelectGenreActivity.class);

                startActivityForResult(intent, SELECT_GENRE_REQUEST_CODE);
            }
        });
        cityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), SelectCityActivity.class);
                intent.putExtra("remove", true);

                startActivityForResult(intent, SELECT_CITY_REQUEST_CODE);
            }
        });


        imgViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageSelector();
                
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uploadBook(sendFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Glide
                .with(this)
                //.load(books.get(position).getImage())
                .load(R.drawable.addbookplaceholder)
                //.fitCenter()
                //.placeholder(R.drawable.addbookplaceholder)
                .crossFade()
                .into(imgViewCamera);
    }

    private void imageSelector() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3, 4)

                .start(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                sendFile = new File(uri.getPath());
                imgViewCamera.setImageBitmap(BitmapFactory.decodeFile(sendFile.getAbsolutePath()));
                sendFile = saveBitmapToFile(sendFile);
                //imgViewCamera.setImageURI(uri);
                imgViewCamera.setImageBitmap(BitmapFactory.decodeFile(sendFile.getAbsolutePath()));


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (requestCode == SELECT_GENRE_REQUEST_CODE) {
            if (data != null) {
                String genreAsString = data.getExtras().getString("genre");
                Genre genre = new Gson().fromJson(genreAsString, Genre.class);
                genreTextView.setText(genre.getTitle() + " >");
                selectedGenreId = genre.getObjectId();
            }
        } else if (requestCode == SELECT_CITY_REQUEST_CODE) {
            if (data != null) {
                String cityAsString = data.getExtras().getString("city");
                City city = new Gson().fromJson(cityAsString, City.class);
                cityTextView.setText(city.getTitle() + " >");
                selectedCityId = city.getObjectId();
            }
        }

    }


    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addbook_menu, menu);
        saveMenuBtn = menu.findItem(R.id.saveBtn);


        saveMenuBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    uploadBook(sendFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        return true;
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


    public void uploadBook(File file) throws IOException {

        if (isFormValid() != true) return;

        progressView.setVisibility(View.VISIBLE);
        loginForm.setVisibility(View.GONE);

        String filename = "";
        Random randomG = new Random();
        filename += randomG.nextInt(111);
        filename += System.currentTimeMillis() + randomG.nextInt(1111);
        filename += randomG.nextInt(111);
        filename += ".jpg";

        final Book newBook = new Book();

        if (editedBook != null) newBook.setObjectId(editedBook.getObjectId());

        if (titleEditText.getText().toString().trim() != null)
            newBook.setTitle(titleEditText.getText().toString());
        if (authorEditText.getText().toString().trim() != null)
            newBook.setAuthor(authorEditText.getText().toString());
        if (priceEditText.getText().toString().trim() != null)
            newBook.setPrice(Integer.parseInt(priceEditText.getText().toString()));
        newBook.setOwner(Backendless.UserService.CurrentUser());
        if (descriptionEditText.getText().toString().trim() != null)
            newBook.setDescr(descriptionEditText.getText().toString());
        if (selectedGenreId != null) newBook.setGenreId(selectedGenreId);
        if (selectedCityId != null) newBook.setCityId(selectedCityId);


        if (sendFile != null) { //LOAD FROM NEW PHOTO


            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            Backendless.Files.Android.upload(bitmap, Bitmap.CompressFormat.PNG, 100, filename, "bookImage",
                    new AsyncCallback<BackendlessFile>() {
                        @Override
                        public void handleResponse(final BackendlessFile backendlessFile) {
                            Log.d(this.getClass().getSimpleName(), "Book Image File UPLOADED");


                            newBook.setImage(backendlessFile.getFileURL());

                            saveBook(newBook);
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Log.d(this.getClass().getSimpleName(), "Book Image File FAIL TO UPLOAD");
                            Log.d(this.getClass().getSimpleName(), "fault = " + backendlessFault.toString());

                            Toast.makeText(getApplicationContext(), getString(R.string.book_created_fail), Toast.LENGTH_LONG).show();
                            progressView.setVisibility(View.GONE);
                            loginForm.setVisibility(View.VISIBLE);

                        }
                    });

        } else {  //LOAD FROM OLD IMAGE
            if (oldImageUrl != null) newBook.setImage(oldImageUrl);
            saveBook(newBook);
        }


    }


    private boolean isFormValid() {

        View focusView = null;
        Boolean isValid = true;

        titleEditText.setError(null);
        authorEditText.setError(null);
        priceEditText.setError(null);
        descriptionEditText.setError(null);
        String toastMessage = "";


        if (TextUtils.isEmpty(titleEditText.getText().toString())) {
            titleEditText.setError(getString(R.string.enter_book_name));
            focusView = titleEditText;
            isValid = false;
        }

        if (sendFile == null && oldImageUrl == null) {
            titleEditText.setError(getString(R.string.select_picture));
            focusView = imgViewCamera;
            isValid = false;
            toastMessage = getString(R.string.select_picture);

        }

        if (TextUtils.isEmpty(authorEditText.getText().toString())) {

            authorEditText.setError(getString(R.string.enter_author));
            focusView = authorEditText;
            isValid = false;
        }
        if (TextUtils.isEmpty(priceEditText.getText().toString())) {
            priceEditText.setError(getString(R.string.enter_price));
            focusView = priceEditText;
            isValid = false;
        }
        if (TextUtils.isEmpty(descriptionEditText.getText().toString())) {
            descriptionEditText.setError(getString(R.string.enter_description));
            focusView = descriptionEditText;
            isValid = false;
        }

        if (selectedGenreId == null) {
            isValid = false;
            focusView = genreTextView;
            toastMessage = getString(R.string.select_genre);
        }
        if (selectedCityId == null) {
            isValid = false;
            focusView = cityTextView;
            toastMessage = getString(R.string.select_city);
        }

        if (isValid == false) focusView.requestFocus();
        if (toastMessage != null && toastMessage.isEmpty() == false)
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();


        return isValid;
    }

    public File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            //o.inSampleSize = 6;  OLD
            o.inSampleSize = 1;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 200;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return file;
        } catch (Exception e) {

            Toast.makeText(this, "Не удалось сжать фото", Toast.LENGTH_LONG);
            return null;
        }
    }

    public void saveBook(Book bookToSave) {

        ServerCalls.saveBookAsync(bookToSave, new AsyncCallback<Book>() {
            @Override
            public void handleResponse(Book book) {
                Log.d(this.getClass().getSimpleName(), "Book SAVED SUCCESS");
                Toast.makeText(getApplicationContext(), getString(R.string.book_created_success), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.d(this.getClass().getSimpleName(), "Book FAILED TO SAVE");
                Log.d(this.getClass().getSimpleName(), "fault = " + fault.toString());
                Toast.makeText(getApplicationContext(), getString(R.string.book_created_fail), Toast.LENGTH_LONG).show();
                progressView.setVisibility(View.GONE);
                loginForm.setVisibility(View.VISIBLE);
            }
        });

    }

    public void loadGenres() {

        progressView.setVisibility(View.VISIBLE);
        loginForm.setVisibility(View.GONE);


        progressView.setVisibility(View.GONE);
        loginForm.setVisibility(View.VISIBLE);

        genreList = MyDataHolder.getInstance().genreTitles;

    }

    public void fillEditedBook() {
        String bookAsString = null;
        if (getIntent().getExtras() != null)
            bookAsString = getIntent().getExtras().getString("book");

        if (bookAsString != null) {
            toolbar.setTitle(getString(R.string.edit_book));

            editedBook = new Gson().fromJson(bookAsString, Book.class);
            authorEditText.setText(editedBook.getAuthor());
            priceEditText.setText(editedBook.getPrice() + "");
            titleEditText.setText(editedBook.getTitle());
            descriptionEditText.setText(editedBook.getDescr());
            selectedGenreId = editedBook.getGenreId();
            selectedCityId = editedBook.getCityId();


            if (editedBook.getGenre() != null) {
                genreTextView.setText(editedBook.getGenre().getTitle());
                selectedGenreId = editedBook.getGenreId();
            }
            if (editedBook.getCity() != null) {
                cityTextView.setText(editedBook.getCity().getTitle());
                selectedCityId = editedBook.getCityId();
            }


            oldImageUrl = editedBook.getImage();

            Glide
                    .with(this)
                    //.load(books.get(position).getImage())
                    .load(editedBook.getImage())
                    .fitCenter()
                    .placeholder(R.drawable.bookplaceholder)
                    .crossFade()
                    .into(imgViewCamera);

        }
    }

}
