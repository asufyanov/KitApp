package com.kitapp.book.Models;

import com.backendless.BackendlessUser;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Admin on 19.06.2017.
 */

public class Genre implements Serializable {
    String title;
    private String objectId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
