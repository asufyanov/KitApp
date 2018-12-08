package com.kitapp.book.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.backendless.persistence.local.UserTokenStorageFactory;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.kitapp.book.BackendlessValues;
import com.kitapp.book.Models.City;
import com.kitapp.book.Models.Genre;
import com.kitapp.book.MyDataHolder;
import com.kitapp.book.R;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    final int REQUEST_NAME_SURNAME = 1;
    final String password = "123AAA123!";
    Button registerBtn;
    Button recreateBtn;
    View mProgressView;
    String userId;
    String phone = "";
    String userToken = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Backendless.initApp(this, BackendlessValues.APPLICATION_ID, BackendlessValues.API_KEY);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);

        setReferences();


        if (MyDataHolder.getInstance().genreTitles == null && MyDataHolder.getInstance().cities == null ) loadGenres();
        else checkAuthorization();


    }

    private void loadGenres() {
        MyDataHolder.getInstance().loadingGenres = true;

        final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(70);
        queryBuilder.setSortBy("title ASC");



        Backendless.Data.of(Genre.class).find(queryBuilder, new AsyncCallback<List<Genre>>() {
            @Override
            public void handleResponse(List<Genre> genres) {

                DataQueryBuilder queryBuilder2 = DataQueryBuilder.create();
                queryBuilder2.setSortBy("created ASC");
                queryBuilder2.setPageSize(70);


                Backendless.Data.of(City.class).find(queryBuilder2, new AsyncCallback<List<City>>() {
                    @Override
                    public void handleResponse(List<City> response) {
                        checkAuthorization();
                        MyDataHolder.getInstance().cities = new ArrayList<City>();
                        MyDataHolder.getInstance().cities.add(null);
                        MyDataHolder.getInstance().cities.addAll(response);

                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        MyDataHolder.getInstance().loadingGenres = false;
                        String s = backendlessFault.getCode();
                        if (s.equals("3064")) {
                            UserTokenStorageFactory.instance().getStorage().set(null);
                            recreate();
                        }

                        Log.d("debug", "City fail " + s);

                        showProgress(false);
                        registerBtn.setVisibility(View.GONE);
                        recreateBtn.setVisibility(View.VISIBLE);

                    }
                });


                Log.d("AddBook", "genre size = " + genres.size());

                MyDataHolder.getInstance().genreTitles = new ArrayList<Genre>(genres);
                MyDataHolder.getInstance().loadingGenres = false;

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                MyDataHolder.getInstance().loadingGenres = false;
                String s = backendlessFault.getCode();
                if (s.equals("3064")) {
                    UserTokenStorageFactory.instance().getStorage().set(null);
                    recreate();
                }

                Log.d("debug", "Genre fail " + s + backendlessFault.toString());

                showProgress(false);
                registerBtn.setVisibility(View.GONE);
                recreateBtn.setVisibility(View.VISIBLE);


            }
        });

    }

    private void setReferences() {


        registerBtn = (Button) findViewById(R.id.button);
        recreateBtn = (Button) findViewById(R.id.recreateBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForm();


            }
        });

        recreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        mProgressView = findViewById(R.id.login_progress);

        userId = UserIdStorageFactory.instance().getStorage().get();


        userToken = UserTokenStorageFactory.instance().getStorage().get();


    }

    private void registerForm() {
        final Intent intent = new Intent(getApplicationContext(), AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, BackendlessValues.APP_REQUEST_CODE);
    }

    private void checkAuthorization() {
        final Boolean[] isLogined = {false};

        //   if (userToken == null || userToken.equals("")) return false;


        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {
                isLogined[0] = response;
                Log.d("Aziz", "IsValid = " + response.toString());

                if (response) loginByUserId();
                else showProgress(false);


            }

            @Override
            public void handleFault(BackendlessFault fault) {
                //Toast.makeText(getApplicationContext(), "Unable to check Authorization", Toast.LENGTH_SHORT).show();
                isLogined[0] = false;
                Log.d("AzizWelcomeActivity", "isValidLogin " + fault.getMessage());


                if (fault.getCode() != "3064")
                    Toast.makeText(getApplicationContext(), "Проблема с авторизацией, перезагрузите приложение", Toast.LENGTH_LONG).show();
                showProgress(false);
            }


        });


    }

    private void showProgress(final boolean b) {
        Log.d("ShowProgress", "" + b);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        registerBtn.setVisibility(b ? View.GONE : View.VISIBLE);

        // btn.animate().setDuration(shortAnimTime).alpha(
        //         b ? 0 : 1).setListener(new AnimatorListenerAdapter() {
        //     @Override
        //     public void onAnimationEnd(Animator animation) {
        //         btn.setVisibility(b ? View.GONE : View.VISIBLE);
        //     }
        // });
        mProgressView.setVisibility(b ? View.VISIBLE : View.GONE);
        // mProgressView.animate().setDuration(shortAnimTime).alpha(
        //         b ? 0 : 1).setListener(new AnimatorListenerAdapter() {
        //     @Override
        //     public void onAnimationEnd(Animator animation) {
        //         mProgressView.setVisibility(b ? View.VISIBLE : View.GONE);
        //     }
        // });


    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BackendlessValues.APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                //showErrorActivity(loginResult.getError());
                Log.d("LOGINSMS", loginResult.getError().toString());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
                showProgress(false);
                return;
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0, 10));
                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
                //goToMyLoggedInActivity();
                //startActivity(new Intent (getApplicationContext(), BookListActivity.class));
                registerNewNumber();
            }

            // Surface the result to your user in an appropriate way.

            //Toast.makeText(this,toastMessage,Toast.LENGTH_LONG).show();
        } else if (requestCode == REQUEST_NAME_SURNAME) {
            if (resultCode == RESULT_OK) {
                String name = data.getExtras().getString("name");
                String surname = data.getExtras().getString("surname");
                registerUser(phone, name, surname);
            } else {
                finish();
            }
        }
    }

    private void registerNewNumber() {

        showProgress(true);
        UserTokenStorageFactory.instance().getStorage().set(null);
        userToken = UserTokenStorageFactory.instance().getStorage().get();
        Log.d("SMSTAG", "registerNewNumber " + UserTokenStorageFactory.instance().getStorage().get().toString());

        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                phone = account.getPhoneNumber().toString();
                String whereClause = "username='" + phone + "'";
                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                queryBuilder.setWhereClause(whereClause);


                Backendless.Data.of(BackendlessUser.class).find(queryBuilder, new AsyncCallback<List<BackendlessUser>>() {
                    @Override
                    public void handleResponse(List<BackendlessUser> response) {
                        if (response.size() > 0) {
                            BackendlessUser user = response.get(0);
                            loginUser(phone, password);


                        } else {

                            Log.d("SMSTAG", "USER NOT FOUND");
                            Intent intent = new Intent(getApplicationContext(), NameSurname.class);

                            startActivityForResult(intent, REQUEST_NAME_SURNAME);

                        }

                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(getApplicationContext(), "Проблема с авторизацией, перезагрузите приложение", Toast.LENGTH_LONG);
                        showProgress(false);
                        Log.d("AzizWelcomeActivity", "findLogin " + fault.getMessage());


                    }
                });

            }

            @Override
            public void onError(AccountKitError accountKitError) {
                Log.d("SMSTAG", "ACCAUNT KIT ERROR " + accountKitError.toString());
                Toast.makeText(getApplicationContext(), "Проблема с авторизацией, перезагрузите приложение", Toast.LENGTH_LONG);
                showProgress(false);
            }
        });

    }

    private void registerUser(final String phone, String name, String surname) {
        BackendlessUser user = new BackendlessUser();
        user.setPassword("123AAA123!");
        user.setProperty("username", phone);
        user.setProperty("name", name);
        user.setProperty("surname", surname);


        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                //Toast.makeText(getApplicationContext(), "NEW USER REGGED", Toast.LENGTH_SHORT).show();
                Backendless.UserService.setCurrentUser(response);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                loginUser(phone, password);

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.d("AzizWelcomeActivity", "resisterUserFail " + fault.getMessage());
                Toast.makeText(getApplicationContext(), "Проблема с авторизацией, перезагрузите приложение", Toast.LENGTH_LONG);
                showProgress(false);
            }
        });

    }

    private void loginByUserId() {

        Backendless.UserService.findById(userId, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {

                Backendless.UserService.setCurrentUser(response);


                //Toast.makeText(getApplicationContext(), "LOGINED SUCCESS", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(intent);
                finish();

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getApplicationContext(), "Проблема с авторизацией, перезагрузите приложение", Toast.LENGTH_LONG);
                showProgress(false);
                Log.d("AzizWelcomeActivity", "loginById " + fault.getMessage());


            }
        });

    }

    private void loginUser(String phone, String password) {
        Backendless.UserService.login(phone, password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                //Backendless.UserService.setCurrentUser(response);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(intent);
                finish();

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getApplicationContext(), "Проблема с авторизацией, перезагрузите приложение", Toast.LENGTH_LONG).show();
                Log.d("AzizWelcomeActivity", "loginUser " + fault.getMessage());
                showProgress(false);

            }
        }, true);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }
}
