package com.kitapp.book.Models;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.persistence.DataQueryBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Admin on 19.06.2017.
 */

public class Book implements Serializable{
    private String descr;
    private Boolean enabled;
    private String author;
    private String ownerId;


    private City city;
    private String cityId;



    String genreId;
    private String title;
    private java.util.Date updated;
    private java.util.Date created;
    private String image;
    private Integer price;
    private String objectId;
    private BackendlessUser owner;
    public String getDescr()
    {
        return descr;
    }



    Genre genre;

    public void setDescr( String descr )
    {
        this.descr = descr;
    }

    public Boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled( Boolean enabled )
    {
        this.enabled = enabled;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor( String author )
    {
        this.author = author;
    }

    public String getOwnerId()
    {
        return ownerId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public java.util.Date getUpdated()
    {
        return updated;
    }

    public java.util.Date getCreated()
    {
        return created;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage( String image )
    {
        this.image = image;
    }

    public Integer getPrice()
    {
        return price;
    }

    public void setPrice( Integer price )
    {
        this.price = price;
    }

    public String getObjectId()
    {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public BackendlessUser getOwner()
    {
        return owner;
    }

    public void setOwner( BackendlessUser owner )
    {
        this.owner = owner;
    }


    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }


    public Book save()
    {
        return Backendless.Data.of( Book.class ).save( this );
    }

    public void saveAsync( AsyncCallback<Book> callback )
    {
        Backendless.Data.of( Book.class ).save( this, callback );
    }

    public Long remove()
    {
        return Backendless.Data.of( Book.class ).remove( this );
    }

    public void removeAsync( AsyncCallback<Long> callback )
    {
        Backendless.Data.of( Book.class ).remove( this, callback );
    }

    public static Book findById( String id )
    {
        return Backendless.Data.of( Book.class ).findById( id );
    }

    public static void findByIdAsync( String id, AsyncCallback<Book> callback )
    {
        Backendless.Data.of( Book.class ).findById( id, callback );
    }

    public static Book findFirst()
    {
        return Backendless.Data.of( Book.class ).findFirst();
    }

    public static void findFirstAsync( AsyncCallback<Book> callback )
    {
        Backendless.Data.of( Book.class ).findFirst( callback );
    }

    public static Book findLast()
    {
        return Backendless.Data.of( Book.class ).findLast();
    }

    public static void findLastAsync( AsyncCallback<Book> callback )
    {
        Backendless.Data.of( Book.class ).findLast( callback );
    }

    public static List<Book> find( DataQueryBuilder queryBuilder )
    {
        return Backendless.Data.of( Book.class ).find( queryBuilder );
    }

    public static void findAsync( DataQueryBuilder queryBuilder, AsyncCallback<List<Book>> callback )
    {
        Backendless.Data.of( Book.class ).find( queryBuilder, callback );
    }








}
