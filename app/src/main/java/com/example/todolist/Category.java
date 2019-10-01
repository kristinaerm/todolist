package com.example.todolist;

import android.graphics.Bitmap;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Category {
    private String name;
    private Integer idIcon;
    private String idCategory;
    private String idUser;

    public Category() {

    }

    public Category(String idCategory,String name, Integer idIcon,  String idUser) {

        this.name = name;
        this.idIcon = idIcon;
        this.idCategory = idCategory;
        this.idUser = idUser;

    }
    public Category(String name, Integer idIcon,  String idUser) {

        this.name = name;
        this.idIcon = idIcon;
        this.idUser = idUser;

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Integer getIdIcon() {
        return idIcon;
    }

    public void setIdIcon(Integer idIcon) {
        this.idIcon = idIcon;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
