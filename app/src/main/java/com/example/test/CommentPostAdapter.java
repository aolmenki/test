package com.example.test;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.model.Album;
import com.example.test.model.Comment;
import com.example.test.model.Post;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TypicodeData> dataList;
    private Map<Post, List<Comment>> mapCommentsToPost;
    private Map<Comment, List<Album>> mapAlbumsToComment;

    private static int POST_TYPE = 1;
    private static int COMMENT_TYPE = 2;
    private static int ALBUM_TYPE = 3;

    public CommentPostAdapter()
    {
        dataList = new ArrayList<>();
        mapCommentsToPost = new HashMap<>();
        mapAlbumsToComment = new HashMap<>();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = dataList.get(position);
        if (item instanceof Post)
        {
            return POST_TYPE;
        }
        if (item instanceof Comment)
        {
            return COMMENT_TYPE;
        }
        else
        {
            return ALBUM_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == POST_TYPE)
        {
            return new PostViewHolder(inflater.inflate(R.layout.post_view_holder,
                    parent, false));
        }
        else if (viewType == COMMENT_TYPE)
        {
            return new CommentViewHolder(inflater.inflate(R.layout.comment_view_holder,
                    parent, false));
        }
        else
            {
            return new AlbumViewHolder(inflater.inflate(R.layout.album_view_holder,
                    parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == POST_TYPE)
        {
            bindPost((PostViewHolder) holder, (Post) dataList.get(position));
        }
        else if (getItemViewType(position) == COMMENT_TYPE)
        {
            bindComment((CommentViewHolder) holder, (Comment) dataList.get(position));
        }
        else
        {
            bindAlbum((AlbumViewHolder) holder, (Album) dataList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{

        TextView postTitleTextView;
        LinearLayout postLayout;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postLayout = itemView.findViewById(R.id.post_layout);
            postTitleTextView = itemView.findViewById(R.id.post_title_text_view);
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView commentNameTextView;
        LinearLayout commentLayout;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentLayout = itemView.findViewById(R.id.comment_layout);
            commentNameTextView = itemView.findViewById(R.id.comment_name_text_view);
        }
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {

        TextView albumTitleTextView;
        LinearLayout albumLayout;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumLayout = itemView.findViewById(R.id.album_layout);
            albumTitleTextView = itemView.findViewById(R.id.album_title_text_view);
        }
    }

    public void setPosts(List<Post> posts) {
        dataList.clear();
        dataList.addAll(posts);
        notifyItemRangeChanged(0, getItemCount());
    }

    private void bindPost(PostViewHolder viewHolder, Post post)
    {
        TextView postTitleTextView = viewHolder.postTitleTextView;
        postTitleTextView.setText(post.getTitle());
        viewHolder.postLayout.setOnClickListener(v -> {
            if (mapCommentsToPost.get(post) == null) {
                postTitleTextView.setTextColor(Color.rgb(255, 165, 0));
                new LoadCommentsTask().execute(post);
            }
            else {
                postTitleTextView.setTextColor(Color.RED);
                List<Comment> comments = mapCommentsToPost.remove(post);
                int itemCount = comments.size();
                dataList.removeAll(comments);
                for (Comment comment : comments) {
                    List<Album> albums = mapAlbumsToComment.remove(comment);
                    if (albums != null)
                    {
                        itemCount += albums.size();
                        dataList.removeAll(albums);
                    }
                }
                notifyItemRangeRemoved(dataList.indexOf(post) + 1, itemCount);
            }
        });
    }

    class LoadCommentsTask extends AsyncTask<Post, Void, List<Comment>>{

        Post post;

        @Override
        protected List<Comment> doInBackground(Post... posts) {
            post = posts[0];
            return NetworkUtils.getInstance().getAllCommentsForPost(post);
        }

        @Override
        protected void onPostExecute(List<Comment> comments) {
            super.onPostExecute(comments);
            if (mapCommentsToPost.get(post) == null) {
                insertComments(post, comments);
            }
        }
    }

    private void insertComments(Post post, List<Comment>comments)
    {
        mapCommentsToPost.put(post, comments);
        dataList.addAll(dataList.indexOf(post) + 1, comments);
        notifyItemRangeInserted(dataList.indexOf(post) + 1, comments.size());
    }

    private void bindComment(CommentViewHolder viewHolder, Comment comment)
    {
        TextView commentNameTextView = viewHolder.commentNameTextView;
        commentNameTextView.setText(comment.getName());
        commentNameTextView.setTextColor(Color.BLUE);
        viewHolder.commentLayout.setOnClickListener(v -> {
            if (mapAlbumsToComment.get(comment) == null) {
                commentNameTextView.setTextColor(Color.GREEN);
                for (Post post : mapCommentsToPost.keySet())
                {
                    if (mapCommentsToPost.get(post) != null &&
                            mapCommentsToPost.get(post).contains(comment))
                    {
                        new LoadAlbumsTask().execute(post, comment);
                    }
                }
            }
            else
            {
                commentNameTextView.setTextColor(Color.BLUE);
                List<Album> albums = mapAlbumsToComment.remove(comment);
                dataList.removeAll(albums);
                notifyItemRangeRemoved(dataList.indexOf(comment) + 1, albums.size());
            }
        });
    }

    class LoadAlbumsTask extends AsyncTask<TypicodeData, Void, List<Album>>{

        Post post;
        Comment comment;

        @Override
        protected List<Album> doInBackground(TypicodeData... postThenComment) {
            post = (Post) postThenComment[0];
            comment = (Comment) postThenComment[1];
            return NetworkUtils.getInstance().getAllAlbumsForPost(post);
        }

        @Override
        protected void onPostExecute(List<Album> albums) {
            super.onPostExecute(albums);
            insertAlbums(comment, albums);
        }
    }

    private void insertAlbums(Comment comment, List<Album> albums)
    {
        mapAlbumsToComment.put(comment, albums);
        dataList.addAll(dataList.indexOf(comment) + 1, albums);
        notifyItemRangeInserted(dataList.indexOf(comment) + 1, albums.size());
    }

    private void bindAlbum(AlbumViewHolder viewHolder, Album album)
    {
        TextView albumTitleTextView = viewHolder.albumTitleTextView;
        albumTitleTextView.setText(album.getTitle());
        albumTitleTextView.setTextColor(Color.GREEN);
        viewHolder.albumLayout.setOnClickListener(v -> {
            toggleAlbumColor(albumTitleTextView);
            new AlertDialog.Builder(albumTitleTextView.getContext()).setMessage("Album Id = " + album.getId() +
                    "\nAlbum Title = " + album.getTitle() +
                    "\n Album User Id = " + album.getUserId()).create().show();
        });
    }

    private void toggleAlbumColor(TextView albumTitleTextView)
    {
        if (albumTitleTextView.getCurrentTextColor() == Color.GREEN)
        {
            albumTitleTextView.setTextColor(Color.BLACK);
        }
        else
        {
            albumTitleTextView.setTextColor(Color.GREEN);
        }
    }

}
