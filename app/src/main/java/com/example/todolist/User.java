package com.example.todolist;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String idUser;
    private String login;
    private String pass;

    public User() {

    }

    public User(String idUser, String login, String pass) {

        this.idUser = idUser;
        this.login = login;
        this.pass = pass;
    }

    public User(String login, String pass) {

        this.login = login;
        this.pass = pass;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getPass() {
        return this.pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

}
