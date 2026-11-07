package com.example.UIDesignLab;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity
public class UserRecord {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    public UserRecord(@NonNull String name) {
        this.name = name;
        this.createdAt = System.currentTimeMillis();
    }
}

