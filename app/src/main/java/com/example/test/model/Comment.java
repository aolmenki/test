package com.example.test.model;

import com.example.test.TypicodeData;
import com.squareup.moshi.Json;

public class Comment implements TypicodeData {
    @Json(name = "id") private int id;
    @Json(name = "postId") private int postId;
    @Json(name = "name") private String name;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
