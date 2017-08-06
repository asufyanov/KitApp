package com.kitapp.book.Models;

import java.io.Serializable;

/**
 * Created by Admin on 04.08.2017.
 */

public class City implements Serializable {
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
