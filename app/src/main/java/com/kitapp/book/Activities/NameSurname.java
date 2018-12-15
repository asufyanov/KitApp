package com.kitapp.book.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.kitapp.book.R;
import com.kitapp.book.ServerCalls;

public class NameSurname extends AppCompatActivity {

    EditText nameEditText;
    EditText surnameEditText;
    Button btn;
    Boolean isForResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_surname);

        nameEditText = (EditText) findViewById(R.id.nameEditTextId);
        surnameEditText = (EditText) findViewById(R.id.surnameEditText);
        btn = (Button) findViewById(R.id.saveBtn);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        fillCurData();


        if (getCallingActivity() == null) isForResult = false;
        else isForResult = true;






        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {

                    if (isForResult == true) returnResult();
                    else updateUserInfo();


                }
            }
        });


    }

    private void fillCurData() {
        try {
            BackendlessUser curUser = Backendless.UserService.CurrentUser();
            String oldName = (String) curUser.getProperty("name");
            String oldSurname = (String) curUser.getProperty("surname");
            nameEditText.setText(oldName);
            surnameEditText.setText(oldSurname);


        } catch (Exception e) {
            Log.d("NameSurname", e.getMessage());
        }
    }

    private void updateUserInfo() {
        String name = nameEditText.getText().toString().trim();
        String surname = surnameEditText.getText().toString().trim();

        ServerCalls.updateNameSurname(name, surname, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                Toast.makeText(getApplicationContext(), "Сохранено", Toast.LENGTH_LONG).show();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                Activity act = getParent();
                if (act != null) getParent().recreate();

                finish();


            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getApplicationContext(), "Не удалось сохранить", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void returnResult() {
        Intent intent = new Intent();
        intent.putExtra("name", nameEditText.getText().toString().trim());
        intent.putExtra("surname", surnameEditText.getText().toString().trim());


        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean isFormValid() {
        nameEditText.setError(null);
        surnameEditText.setError(null);

        View focusView = null;

        boolean cancel = true;
        if (TextUtils.isEmpty(nameEditText.getText())) {
            nameEditText.setError("Введите ИМЯ");
            focusView = nameEditText;
            cancel = false;
        }
        if (TextUtils.isEmpty(surnameEditText.getText())) {
            surnameEditText.setError("Введите фамилию");
            cancel = false;
            focusView = surnameEditText;
        }


        if (cancel == false) {
            focusView.requestFocus();
            return false;
        } else {

            return true;
        }

    }
}
