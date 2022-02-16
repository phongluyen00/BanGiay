package com.example.retrofitrxjava;

import androidx.annotation.Keep;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Keep
public class UserModel {
    @PrimaryKey(autoGenerate = true)
    private int idUser;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private String password;
    @ColumnInfo
    private int permission;

    public boolean isAdmin() {
        return permission == 0;
    }
}
