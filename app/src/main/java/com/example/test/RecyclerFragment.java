package com.example.test;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.model.Album;
import com.example.test.model.Post;

import java.util.List;

public class RecyclerFragment extends Fragment {
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_fragment, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CommentPostAdapter adapter = new CommentPostAdapter();
        recyclerView.setAdapter(adapter);

        new LoadPostsTask().execute();

        return rootView;
    }

    private class LoadPostsTask extends AsyncTask<Void, Void, List<Post>>
    {

        @Override
        protected List<Post> doInBackground(Void... voids) {
            return NetworkUtils.getInstance().getAllPosts();
        }

        @Override
        protected void onPostExecute(List<Post> posts) {
            super.onPostExecute(posts);
            CommentPostAdapter adapter = (CommentPostAdapter) recyclerView.getAdapter();
            adapter.setPosts(posts);
        }
    }

    public void showAlbumDialog(Album album)
    {
        new AlertDialog.Builder(getContext()).setMessage("Album Id = " + album.getId() +
                "\nAlbum Title = " + album.getTitle() +
                "\n Album User Id = " + album.getUserId())
                .create()
                .show();
    }
}
