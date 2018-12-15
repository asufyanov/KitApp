package com.kitapp.book;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.kitapp.book.Models.Book;

/**
 * Created by Admin on 02.12.2018.
 */

public class ServerCalls {
    public static void updateNameSurname(String name, String surname, final AsyncCallback<BackendlessUser> callback) {
        BackendlessUser curUser = Backendless.UserService.CurrentUser();
        Backendless.UserService.CurrentUser().setProperty("name", name.trim());
        Backendless.UserService.CurrentUser().setProperty("surname", surname.trim());


        Backendless.Persistence.of(BackendlessUser.class).save(curUser, callback);

    }

    public static void saveBookAsync(Book book, AsyncCallback<Book> callback) {
        Backendless.Data.of(Book.class).save(book, callback);
    }
}
