package com.kitapp.book;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

/**
 * Created by Admin on 02.12.2018.
 */

public class ServerCalls {
    public static void updateNameSurname(String name, String surname, final AsyncCallback<BackendlessUser> callback) {
        BackendlessUser curUser = Backendless.UserService.CurrentUser();
        Backendless.UserService.CurrentUser().setProperty("name", name);
        Backendless.UserService.CurrentUser().setProperty("surname", surname);


        Backendless.Persistence.of(BackendlessUser.class).save(curUser, new AsyncCallback<BackendlessUser>() {

            @Override
            public void handleResponse(BackendlessUser response) {
                callback.handleResponse(response);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                callback.handleFault(fault);
            }
        });

    }
}
