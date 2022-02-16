package com.example.retrofitrxjava.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.retrofitrxjava.Article;
import com.example.retrofitrxjava.Product;
import com.example.retrofitrxjava.UserModel;

import java.util.List;

@Dao
public interface ArticleDao {
    @Query("SELECT * FROM Product WHERE idUserModel =:idUser")
    List<Product> getAll(int idUser);

//    @Insert
//    void insert(Article... articles);

    @Update
    void update(Product articles);

    @Delete
    void delete(Product... articles);

    @Query("DELETE FROM Product WHERE idUserModel=:idUser")
    void deleteAll(int idUser);

    @Insert
    void insertUser(UserModel... userModels);

    @Query("SELECT * FROM UserModel WHERE name like :name")
    UserModel checkExistsUser(String name);

    @Query("SELECT * FROM UserModel WHERE name = :user and password = :password")
    UserModel login(String user, String password);

    @Query("SELECT * FROM UserModel WHERE idUser ==:id")
    UserModel getUserModelId(int id);

    @Query("SELECT * FROM UserModel")
    List<UserModel> getAllUser();

    @Delete
    void deleteAccount(UserModel... userModels);

    @Insert
    void insertProduct(Product... product);

}
