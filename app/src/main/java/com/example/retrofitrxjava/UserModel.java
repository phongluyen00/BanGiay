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
    //    @PrimaryKey(autoGenerate = true)
//    private int idUser;
//    @ColumnInfo
    private String name;
    private String email;

    //    @ColumnInfo
    private String password;
    //    @ColumnInfo
//    private int permission;
//    @ColumnInfo
    private String phoneNumber;
    //    @ColumnInfo
    private String address;

    private String money;
    private String image;
    private String documentId;

    public UserModel(String name, String email, String password, String phoneNumber, String address, String money) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.money = money;
    }

    public String getPhoneNumber() {
        return phoneNumber == null ? "New member" : phoneNumber;
    }

    public String getAddress() {
        return address == null ? "New member" : address;
    }

    public String getMoney() {
        return money == null ? "1000" : money;
    }
}
