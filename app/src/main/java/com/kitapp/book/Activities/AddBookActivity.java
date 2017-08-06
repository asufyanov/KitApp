package com.kitapp.book.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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

import com.android.camera.CropImageIntentBuilder;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class AddBookActivity extends AppCompatActivity {

    private static final int SELECT_GENRE_REQUEST_CODE = 3;
    private static final int SELECT_CITY_REQUEST_CODE = 4;
    protected View view;
    protected ImageView imgViewCamera;
    protected int LOAD_IMAGE_CAMERA = 0, REQUEST_CROP_PICTURE = 1, LOAD_IMAGE_GALLARY = 2;
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
    boolean isActivityJustStarted = true;
    private Uri picUri;
    private Button btn;
    private File pic;
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
        toolbar.setTitle(getString(R.string.all_books));
        if (editedBook != null) toolbar.setTitle(getString(R.string.edit));
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

                startActivityForResult(intent, SELECT_CITY_REQUEST_CODE);
            }
        });


        imgViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), LOAD_IMAGE_GALLARY);


                /*
                final CharSequence[] options = {"Take Photo", "Choose from Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(AddBookActivity.this);
                builder.setTitle("Select Pic Using...");
                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {

                            try {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                                pic = new File(Environment.getExternalStorageDirectory(),
                                        "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

                                picUri = Uri.fromFile(pic);

                                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, picUri);

                                cameraIntent.putExtra("return-data", true);
                                startActivityForResult(cameraIntent, LOAD_IMAGE_CAMERA);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                            }

                        } else if (options[item].equals("Choose from Gallery")) {
                            Intent intent = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), LOAD_IMAGE_GALLARY);

                        }
                    }
                });

                builder.show();*/

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadBook(sendFile);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        File croppedImageFile = new File(getFilesDir(), "test.jpg");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_IMAGE_CAMERA && resultCode == RESULT_OK) {


            Uri croppedImage = Uri.fromFile(croppedImageFile);
            CropImageIntentBuilder cropImage = new CropImageIntentBuilder(3, 4, 600, 800, croppedImage);
            cropImage.setOutlineColor(0xFF03A9F4);

            cropImage.setSourceImage(picUri);

            startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);
            //CropImage();

        } else if (requestCode == LOAD_IMAGE_GALLARY) {
            if (data != null) {

                //Uri croppedImage = data.getData();
                Uri croppedImage = Uri.fromFile(croppedImageFile);
                CropImageIntentBuilder cropImage = new CropImageIntentBuilder(3, 4, 600, 800, croppedImage);
                cropImage.setOutlineColor(0xFF03A9F4);
                cropImage.setSourceImage(data.getData());


                startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);

                // picUri = data.getData();

//
                // CropImage();
            }
        } else if (requestCode == REQUEST_CROP_PICTURE) {
            Log.d("CROP AFTER", "I AM HERE1");
            if (data != null) {
                Log.d("CROP AFTER", "I AM HERE2");
                croppedImageFile = saveBitmapToFile(croppedImageFile);
                imgViewCamera.setImageBitmap(BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath()));
                sendFile = croppedImageFile;
                //uploadBook(croppedImageFile);
                // get the returned data
                //Bundle extras = data.getExtras();

                // get the cropped bitmap
                //Bitmap photo = extras.getParcelable("data");

                //imgViewCamera.setImageBitmap(photo);

                if (pic != null) {
                    // To delete original image taken by camera
                    if (pic.delete()) {
                        //Common.showToast(AddBookActivity.this,"original image deleted...");
                    }
                }
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
                cityTextView.setText(city.getTitle()+ " >");
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
                uploadBook(sendFile);

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


    public void uploadBook(File file) {

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

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
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
            return null;
        }
    }

    public void loadGenres() {

        progressView.setVisibility(View.VISIBLE);
        loginForm.setVisibility(View.GONE);


        progressView.setVisibility(View.GONE);
        loginForm.setVisibility(View.VISIBLE);

        genreList = MyDataHolder.getInstance().genreTitles;

/*
        Backendless.Data.of(Genre.class).find(queryBuilder, new AsyncCallback<List<Genre>>() {
            @Override
            public void handleResponse(List<Genre> genreBackendlessCollection) {

                progressView.setVisibility(View.GONE);
                loginForm.setVisibility(View.VISIBLE);

                Log.d("AddBook", "genre size = " + genreBackendlessCollection.size());
                genreList = genreBackendlessCollection;
                spinnerGenreAdapter = new SpinnerGenreAdapter(getApplicationContext(), genreList);
                spinner.setAdapter(spinnerGenreAdapter);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.d("AddBook", backendlessFault.getMessage());
                progressView.setVisibility(View.GONE);
                Toast.makeText(getApplication(), "Не удалось запустить задачу, попробуйте позже", Toast.LENGTH_LONG);
            }
        });
*/
    }

    public void fillEditedBook() {
        String bookAsString = null;
        if (getIntent().getExtras() != null)
            bookAsString = getIntent().getExtras().getString("book");

        if (bookAsString != null) {
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
                genreTextView.setText(editedBook.getCity().getTitle());
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

    public void saveBook(Book bookToSave) {

        bookToSave.saveAsync(new AsyncCallback<Book>() {
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

}
