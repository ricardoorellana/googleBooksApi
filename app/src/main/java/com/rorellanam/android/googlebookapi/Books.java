package com.rorellanam.android.googlebookapi;

/**
 * Created by Rorellanam on 6/28/16.
 */
public class Books {
    private String title;
    private String author;
    private String url;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Books(String title, String author, String url) {
        this.title = title;
        this.author = author;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
