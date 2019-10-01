package com.example.todolist;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChangeCategoryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<Category> categoryList;
    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private boolean connected = false;
    DataAdapterCategoryList dataAdapterCategoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_category);
        recyclerView = findViewById(R.id.recyclerView);


        setInitialData();

        dataAdapterCategoryList = new DataAdapterCategoryList(this, categoryList);
        recyclerView.setAdapter(dataAdapterCategoryList);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change category of notes");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // обработчик кнопки крестика


            }
        });


    }

    //добавление данных в список категории
    private void setInitialData() {
        categoryList = new ArrayList<>();
        Database db = new Database(this);
        Firebase f = new Firebase();
        System.out.println("Inserting ..");
        if (CheckConnection()) {
            //чтение всех элементов категории из firebase
            //String idCat = f.writeNewCategory("Home1", R.drawable.clear, 919999);
            mDatabase = FirebaseDatabase.getInstance().getReference("category:");
            mDatabase.addListenerForSingleValueEvent(valueEventListener);
        } else {

        //    db.addCategory(new Category("Home", R.drawable.clear, "9954999"));
            categoryList = db.getAllCategorys();
            // db.addCategory(new Category("Work", R.drawable.done, 99999));
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_change_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            Intent intent = new Intent(this, CategoryActivity.class);
            startActivity(intent);
        }
        return true;
    }


    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            categoryList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    categoryList.add(category);
                }
                dataAdapterCategoryList.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    //проверяет доступ в интернет
    public boolean CheckConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else {
            return false;
        }
    }
}