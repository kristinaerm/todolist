package com.example.todolist;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class Firebase {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();


    public String writeNewCategory(String name, Integer idIcon, String idUser) {
        String idCategory = mDatabase.push().getKey();
        Category category = new Category(idCategory,name, idIcon, idUser);

        mDatabase.child("category:").child(idCategory).setValue(category);
        return idCategory;
    }

    public void updateUser(String idUser, String login, String pass) {
        mDatabase.child(idUser).child("login").setValue(login);
        mDatabase.child(idUser).child("pass").setValue(pass);

    }

    public void updateCategory(Integer idCategory, String name, Integer idIcon, Integer idUser) {
        mDatabase.child(idCategory.toString()).child("name").setValue(name);
        mDatabase.child(idCategory.toString()).child("idIcon").setValue(idIcon);
        mDatabase.child(idCategory.toString()).child("idUser").setValue(idUser);

    }

    public void writeNewUser(String login, String pass) {
        String userId = mDatabase.push().getKey();
        User user = new User(login, pass);

        mDatabase.child("users").child(userId).setValue(user);
    }

    void removeDataCategoryFromDatabase(){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference("category:");
        root.setValue(null);
    }
    void removeDataTaskFromDatabase(){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference("task:");
        root.setValue(null);
    }


}
