package com.healthtracker.chw.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Query("SELECT * FROM users WHERE uid = :uid")
    User getUser(String uid);

    @Query("SELECT * FROM users LIMIT 1")
    User getCurrentUser(); // Handy if single user device, or getting the last logged in
}
