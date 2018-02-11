package com.jollyremedy.notreddit.data.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.jollyremedy.notreddit.models.Post;

import java.util.List;

@Dao
public interface PostDao {
    @Query("SELECT * FROM posts")
    LiveData<List<Post>> getAll();
}
