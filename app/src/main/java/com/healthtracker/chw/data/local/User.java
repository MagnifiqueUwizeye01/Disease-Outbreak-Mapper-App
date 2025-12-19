package com.healthtracker.chw.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    public String uid; // Firebase UID

    public String name;
    public String email;
    public String role;
    public String phone;
    public long createdAt;

    public User(@NonNull String uid, String name, String email, String role, String phone) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.createdAt = System.currentTimeMillis();
    }
}
