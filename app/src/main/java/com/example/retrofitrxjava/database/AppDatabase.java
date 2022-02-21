package com.example.retrofitrxjava.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.retrofitrxjava.Article;
import com.example.retrofitrxjava.Product;
import com.example.retrofitrxjava.UserModel;

@Database(entities = {Article.class, /*UserModel.class, */Product.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance = null;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context,
                    AppDatabase.class, "book_manager")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract ArticleDao getStudentDao();
}
