package com.example.todolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.ArrayMap;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;

public class MainAndNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LinkedList<Task> tasks = new LinkedList<>();
    private LinkedList<Category> categories = new LinkedList<>();
    LinearLayout layout;
    String description;
    String nameCategory;
    Date datetime = new Date();
    AppCompatActivity th = new AppCompatActivity();
    Menu subMenu;
    String userId;
    Firebase fire = new Firebase();
    boolean all = true;
    private Database db = new Database(this);
    ArrayMap<Integer,String> menuCategory = new ArrayMap<>();
    //temporary
    int index = 0;
    Category currentCategory = new Category("1","1", R.drawable.add,"1");
    String idCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_and_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Database.setUndeletableCategory(getString(R.string.no_category));
        userId = getIntent().getStringExtra("idUser");
        all = getIntent().getBooleanExtra("all", true);
        idCategory = getIntent().getStringExtra("idCategory");

        //TODO REMOVE

        th = this;
        layout = (LinearLayout) findViewById(R.id.linearLayoutMainNavigation);

        //fire.writeNewCategory(getString(R.string.no_category), R.drawable.done, userId);
        //db.addCategory(new Category("Other", R.drawable.done, userId));

/*
        Query query1 = FirebaseDatabase.getInstance().getReference("category:")
                .orderByChild("idUser")
                .equalTo(userId);
        query1.addListenerForSingleValueEvent(valueEventListener);
        */
/*
        Task task = new Task("123456789", "-LqgORdtQzaw4SCYUMM9", ((datetime.getDate() > 9) ? (datetime.getDate()) : ("0" + datetime.getDate())) + "." + (((datetime.getMonth()+1) > 9) ? ((datetime.getMonth()+1)) : ("0" + (datetime.getMonth()+1))) + "." + datetime.getYear() + " " + ((datetime.getHours() > 9) ? (datetime.getHours()) : ("0" + datetime.getHours())) + ":" + ((datetime.getMinutes() > 9) ? (datetime.getMinutes()) : ("0" + datetime.getMinutes())));
        fire.writeNewTask(task);
*/



        sortAndShowTasks(layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //turn drawable "+" black
        Drawable myFabSrc = getResources().getDrawable(android.R.drawable.ic_input_add);
        myFabSrc.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        fab.setImageDrawable(myFabSrc);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(th);
                builder.setTitle(getString(R.string.enter_task));

                final EditText input = new EditText(th);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast toast_show_description = Toast.makeText(getApplicationContext(),
                                input.getText(), Toast.LENGTH_SHORT);
                        toast_show_description.show();
                        description = input.getText().toString();

                        final View date_dialog_view = View.inflate(th, R.layout.date_layout, null);
                        final AlertDialog date_alert_dialog = new AlertDialog.Builder(th).create();

                        date_dialog_view.findViewById(R.id.button_date).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                DatePicker datePicker = (DatePicker) date_dialog_view.findViewById(R.id.datePicker1);
                                datetime = new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                                Toast toast_show_date = Toast.makeText(getApplicationContext(),
                                        datetime.getYear() + "-" + datetime.getMonth() + "-" + datetime.getDate(), Toast.LENGTH_SHORT);
                                toast_show_date.show();

                                final View time_dialog_view = View.inflate(th, R.layout.time_layout, null);
                                final AlertDialog time_alert_dialog = new AlertDialog.Builder(th).create();

                                time_dialog_view.findViewById(R.id.button_time).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        TimePicker timepicker = (TimePicker) time_dialog_view.findViewById(R.id.timePicker1);
                                        Toast toast_show_time = Toast.makeText(getApplicationContext(),
                                                timepicker.getCurrentHour() + ":" + timepicker.getCurrentMinute(), Toast.LENGTH_SHORT);
                                        toast_show_time.show();
                                        datetime = new Date(datetime.getYear(), datetime.getMonth(), datetime.getDate(), timepicker.getCurrentHour().intValue(), timepicker.getCurrentMinute().intValue());
                                        Toast toast_show_datetime = Toast.makeText(getApplicationContext(),
                                                ((datetime.getDate() > 9) ? (datetime.getDate()) : ("0" + datetime.getDate())) + "." + ((datetime.getMonth() > 9) ? (datetime.getMonth()) : ("0" + datetime.getMonth())) + "." + datetime.getYear() + " " + ((datetime.getHours() > 9) ? (datetime.getHours()) : ("0" + datetime.getHours())) + ":" + ((datetime.getMinutes() > 9) ? (datetime.getMinutes()) : ("0" + datetime.getMinutes())), Toast.LENGTH_LONG);
                                        toast_show_datetime.show();
                                        Task task = new Task(description, idCategory, ((datetime.getDate() > 9) ? (datetime.getDate()) : ("0" + datetime.getDate())) + "." + (((datetime.getMonth()+1) > 9) ? ((datetime.getMonth()+1)) : ("0" + (datetime.getMonth()+1))) + "." + datetime.getYear() + " " + ((datetime.getHours() > 9) ? (datetime.getHours()) : ("0" + datetime.getHours())) + ":" + ((datetime.getMinutes() > 9) ? (datetime.getMinutes()) : ("0" + datetime.getMinutes())));
                                        //TODO: add task in firebase and get id
                                        //task.setIdTask(String.valueOf(index));
                                        tasks.add(task);

                                        //fire.writeNewTask(task);
                                        db.addTask(task);

                                        index++;
                                        sortAndShowTasks(layout);
                                        time_alert_dialog.dismiss();
                                    }
                                });
                                time_alert_dialog.setView(time_dialog_view);
                                time_alert_dialog.show();

                                date_alert_dialog.dismiss();

                            }
                        });
                        date_alert_dialog.setView(date_dialog_view);
                        date_alert_dialog.show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        MenuItem category = menu.getItem(1);
        subMenu = category.getSubMenu();

        //TODO кнопка выхода из приложения и аккаунта
        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  FirebaseAuth. getInstance (). signOut ();
                Intent intent = new Intent(MainAndNavigation.this, MapActivity.class);
                startActivity(intent);
            }
        });


    }

    public boolean onPrepareOptionsMenu (Menu menu){

        Toast toast_inside_prepareOptionsMenu = Toast.makeText(getApplicationContext(),
                "inside "+subMenu.size(), Toast.LENGTH_SHORT);
        toast_inside_prepareOptionsMenu.show();

        categories = db.getCategorybyIdUser(userId);
        if (categories.size()==0){
            db.addNoCategory(new Category(getString(R.string.no_category), R.drawable.label, userId));
            categories = db.getCategorybyIdUser(userId);
        }

        int s = menuCategory.size();
        if (subMenu.size()>2){
            for (int i=0; i< s; i++){
                int id = subMenu.getItem(0).getItemId();
                subMenu.removeItem(id);
                menuCategory.remove(0);
            }
        }
        //TODO: firebase upload categories
        for (int i=0; i< categories.size(); i++){
            //TODO: clear except nocategory and add new categories
            menuCategory.put(i,categories.get(i).getIdCategory());
            subMenu.add(R.id.nav_group_categories,i, Menu.NONE, categories.get(i).getName()).setIcon(getResources().getDrawable(R.drawable.label));
            // установить как-то айди categories.get(i).getIdCategory()
        }

        return true;
    }

    ValueEventListener valueEventListenerTasks = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //tasks.clear();

            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    tasks.add(task);
                }
                //dataAdapterCategoryList.notifyDataSetChanged();

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            categories.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    categories.add(category);
                }
                //dataAdapterCategoryList.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void sortAndShowTasks(final LinearLayout linearLayout){
        int previousState=0;
        int currentState=0;
        linearLayout.removeAllViews();
        tasks.clear();

/*
        Query query1 = FirebaseDatabase.getInstance().getReference("category:")
                .orderByChild("idUser")
                .equalTo(userId);
        query1.addListenerForSingleValueEvent(valueEventListener);
*/
        categories = db.getCategorybyIdUser(userId);

        if (categories.size()==0){
            db.addNoCategory(new Category(getString(R.string.no_category), R.drawable.label, userId));
            categories = db.getCategorybyIdUser(userId);
        }


        if (idCategory.equals("")){
            int k =0;

            while ((k<categories.size())&&(!categories.get(k).getName().equals(getString(R.string.no_category)))){
                k++;
            }
            if (k<categories.size())
                idCategory=categories.get(k).getIdCategory();
        }

        if (all){
            //for (int i=0; i<categories.size(); i++){
                /*
                Query query = FirebaseDatabase.getInstance().getReference("task:")
                        .orderByChild("idCategory")
                        .equalTo(categories.get(i).getIdCategory());
                query.addListenerForSingleValueEvent(valueEventListenerTasks);
                */
                for (int i=0; i<categories.size(); i++){
                    db.getTasksByCategory(categories.get(i).getIdCategory(), tasks);
                }
            //}
        } else {
            /*
            Query query = FirebaseDatabase.getInstance().getReference("task")
                    .orderByChild("idCategory")
                    .equalTo(idCategory);
            query.addListenerForSingleValueEvent(valueEventListenerTasks);
            */
            tasks = db.getTasksByCategory(idCategory);
        }


        try {
            tasks = Task.sort(tasks);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Task curr;
        try {
            //TODO проверка не пуст ли
            if(tasks.size()!=0){
                previousState = tasks.get(0).timeline();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (int i=0; i<tasks.size(); i++){
            curr = tasks.get(i);
            try {
                currentState = tasks.get(i).timeline();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            CheckBox checkbox = new CheckBox(th);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //TODO: delete task from firebase by tag
                        linearLayout.removeView(buttonView);
                        db.deleteTask((String)buttonView.getTag());
                        sortAndShowTasks(layout);
                    }
                }
            });
            checkbox.setTextSize(20);
            checkbox.setText(curr.toString());
            checkbox.setTag(curr.getIdTask());
            //checkbox.setId(Integer.parseInt(curr.getIdTask()));
            if(previousState!=currentState){
                TextView label = new TextView(th);
                label.setText(previousState);
                label.setTextSize(20);
                layout.addView(label,0);
                previousState=currentState;
            }
            linearLayout.addView(checkbox, 0);
        }
        try {
            if (tasks.size()-1>=0) {
                TextView label = new TextView(th);
                label.setText(tasks.get(tasks.size() - 1).timeline());
                label.setTextSize(20);
                layout.addView(label, 0);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_and_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //TODO: Settings of language
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all_tasks) {
            all=true;
            idCategory="";
            sortAndShowTasks(layout);
        } else if (id == R.id.nav_add_category) {

            //TODO добавить добавление категории в фаей и базу и обновить меню
            final AlertDialog.Builder builder = new AlertDialog.Builder(th);
            builder.setTitle(getString(R.string.enter_category));

            final EditText input = new EditText(th);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nameCategory = input.getText().toString();
                    db.addCategory(new Category(nameCategory,R.drawable.label,userId));
                    invalidateOptionsMenu();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        } else if (id == R.id.nav_change_category) {
            Intent intent = new Intent(this, ChangeCategoryActivity.class);
            intent.putExtra("idUser", userId);//передаю в изменение активити id пользователя который вошел
            startActivity(intent);
        } else {
            String catId = menuCategory.get(id);
            all=false;
            idCategory=catId;
            sortAndShowTasks(layout);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
