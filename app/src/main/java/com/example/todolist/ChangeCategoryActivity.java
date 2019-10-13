package com.example.todolist;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ChangeCategoryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<Category> categoryList;
    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private boolean connected = false;
    private Database db = new Database(this);
    private Firebase firebase = new Firebase();
    private DataAdapterCategoryList dataAdapterCategoryList;
    private List<Category> deletecategoryList = new LinkedList<Category>();
    private String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_category);
        idUser = getIntent().getStringExtra("idUser");
        recyclerView = findViewById(R.id.recyclerView);


        // FirebaseDatabase database = FirebaseDatabase.getInstance();
        // mDatabase = database.getReference();


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
                deletecategoryList.clear();
                Intent intent = new Intent(ChangeCategoryActivity.this, MainAndNavigation.class);
                startActivity(intent);

            }
        });


    }

    //добавление данных в список категории
    private void setInitialData() {
        categoryList = new ArrayList<>();

        System.out.println("Inserting ..");
        //  if (CheckConnection()) {
        //чтение всех элементов категории из firebase
        // firebase.writeNewCategory("home2",R.drawable.done,idUser);
        //  mDatabase.addListenerForSingleValueEvent(valueEventListener);

        //  if (CheckConnection()) {
        //чтение всех элементов категории из firebase
        // firebase.writeNewCategory("home2",R.drawable.done,idUser);
        //  mDatabase.addListenerForSingleValueEvent(valueEventListener);

       /*     Query query = FirebaseDatabase.getInstance().getReference("category:")
                    .orderByChild("idUser")
                    .equalTo(idUser);
            query.addListenerForSingleValueEvent(valueEventListener);*/
        //} else {


        //  db.addCategory(new Category("Home", R.drawable.clear, idUser));
        // categoryList = db.getAllCategorys();
        categoryList = db.getCategorybyIdUser(idUser);
        // db.addCategory(new Category("Work", R.drawable.done, 99999));

        //  db.addCategory(new Category("Home", R.drawable.clear, idUser));
        // categoryList = db.getAllCategorys();
        //categoryList=db.getCategorybyIdUser(idUser);
        // db.addCategory(new Category("Work", R.drawable.done, 99999));
        //}

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
        //действия при нажатии на галочку
        if (item.getItemId() == R.id.action_done) {
            deletecategoryList = dataAdapterCategoryList.listDeleteCategory();
           /* if (CheckConnection()) {
                //чтение всех элементов категории из firebase
                for(int i=0;i<deletecategoryList.size();i++){
                    String s=deletecategoryList.get(i).getIdCategory();
                    mDatabase = FirebaseDatabase.getInstance().getReference()
                            .child("category:").child(deletecategoryList.get(i).getIdCategory().toString());
                    mDatabase.removeValue();
                    mDatabase.addListenerForSingleValueEvent(valueEventListener);

                }
                Toast.makeText(ChangeCategoryActivity.this, "Удалено из firebase", Toast.LENGTH_SHORT).show();
            } else {*/

            for (int i = 0; i < deletecategoryList.size(); i++) {
                db.deleteCategory(deletecategoryList.get(i));

                //  }

                Toast.makeText(ChangeCategoryActivity.this, "Удалено из БД", Toast.LENGTH_SHORT).show();
                // db.addCategory(new Category("Work", R.drawable.done, 99999));
            }
            Intent intent = new Intent(this, MainAndNavigation.class);
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