package com.example.test;

import com.example.test.model.Album;
import com.example.test.model.Comment;
import com.example.test.model.Post;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class NetworkUtils {

    private static NetworkUtils instance = new NetworkUtils();
    private webAPI api;

    //private constructor.
    private NetworkUtils() {
        Moshi moshi = new Moshi.Builder().build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com")
                .addConverterFactory(MoshiConverterFactory.create(moshi)).build();
        api = retrofit.create(webAPI.class);
    }

    public static NetworkUtils getInstance() {
        return instance;
    }

    public List<Post> getAllPosts()
    {
        List<Post> returnList = new ArrayList<>();
        try
        {
            Response<List<Post>> response = api.getAllPosts().execute();
            returnList = response.body();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return returnList;
    }

    public List<Comment> getAllCommentsForPost(Post post)
    {
        List<Comment> returnList = new ArrayList<>();
        try
        {
            Response<List<Comment>> response = api.getAllCommentsForPostId(post.getId()).execute();
            returnList = response.body();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return returnList;
    }

    public List<Album> getAllAlbumsForPost(Post post)
    {
        List<Album> returnList = new ArrayList<>();
        try
        {
            Response<List<Album>> response =
                    api.getAllAlbumsForUserId(post.getUserId()).execute();
            returnList = response.body();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return returnList;

    }

    interface webAPI {
        @GET("posts")
        Call<List<Post>> getAllPosts();

        @GET("comments")
        Call<List<Comment>> getAllCommentsForPostId(@Query("postId") int postId);

        @GET("albums")
        Call<List<Album>> getAllAlbumsForUserId(@Query("id") int id);
    }

}
