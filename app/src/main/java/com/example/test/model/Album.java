package com.example.test.model;

import com.example.test.TypicodeData;
import com.squareup.moshi.Json;

public class Album implements TypicodeData {
    @Json(name = "id") private int id;
    @Json(name = "userId") private int userId;
    @Json(name = "title") private String title;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
