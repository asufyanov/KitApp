package com.kitapp.book.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kitapp.book.R;

public class NameSurname extends AppCompatActivity {

    EditText name;
    EditText surname;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_surname);

        name = (EditText) findViewById(R.id.nameEditTextId);
        surname = (EditText) findViewById(R.id.surnameEditText);
        btn = (Button) findViewById(R.id.saveBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {
                    Intent intent = new Intent();
                    intent.putExtra("name", name.getText().toString().trim());
                    intent.putExtra("surname", surname.getText().toString().trim());

                    setResult(RESULT_OK, intent);
                    finish();

                }
            }
        });


    }

    private boolean isFormValid() {
        name.setError(null);
        surname.setError(null);

        View focusView = null;

        boolean cancel = true;
        if (TextUtils.isEmpty(name.getText())) {
            name.setError("Введите ИМЯ");
            focusView = name;
            cancel = false;
        }
        if (TextUtils.isEmpty(surname.getText())) {
            surname.setError("Введите фамилию");
            cancel = false;
            focusView = surname;
        }


        if (cancel == false) {
            focusView.requestFocus();
            return false;
        } else {

            return true;
        }

    }
}
