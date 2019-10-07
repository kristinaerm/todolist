package com.example.todolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.Group;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.SubMenu;
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

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;

public class MainAndNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LinkedList<Task> tasks = new LinkedList<>();
    private LinkedList<Category> categories = new LinkedList<>();
    LinearLayout layout;
    String description;
    Date datetime = new Date();
    AppCompatActivity th = new AppCompatActivity();
    Menu subMenu;
    //temporary
    int index = 0;
    Category currentCategory = new Category("1","1", R.drawable.add,"1");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_and_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        th = this;
        layout = (LinearLayout) findViewById(R.id.linearLayoutMainNavigation);

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
                                        Task task = new Task(description, currentCategory.getIdCategory(), ((datetime.getDate() > 9) ? (datetime.getDate()) : ("0" + datetime.getDate())) + "." + (((datetime.getMonth()+1) > 9) ? ((datetime.getMonth()+1)) : ("0" + (datetime.getMonth()+1))) + "." + datetime.getYear() + " " + ((datetime.getHours() > 9) ? (datetime.getHours()) : ("0" + datetime.getHours())) + ":" + ((datetime.getMinutes() > 9) ? (datetime.getMinutes()) : ("0" + datetime.getMinutes())));
                                        //TODO: add task in firebase and get id
                                        task.setIdTask(String.valueOf(index));
                                        tasks.add(task);
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

        /*
        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(th, ChangeCategoryActivity.class);
                startActivity(intent);
            }
        });
        */

    }

    public boolean onPrepareOptionsMenu (Menu menu){
        /*
        Toast toast_inside_prepareOptionsMenu = Toast.makeText(getApplicationContext(),
                "inside", Toast.LENGTH_SHORT);
        toast_incide_prepareOptionsMenu.show();
        */
        //TODO: firebase upload categories
        for (int i=0; i< categories.size(); i++){
            subMenu.removeItem(Integer.valueOf(categories.get(i).getIdCategory()));
        }
        if (subMenu.size()<4){
            //TODO: clear except nocategory and add new categories
            subMenu.add(R.id.nav_group_categories,0,Menu.NONE, getString(R.string.enter_task_time)).setIcon(getResources().getDrawable(R.drawable.label));
        }


        return true;
    }

    public void sortAndShowTasks(final LinearLayout linearLayout){
        int previousState=0;
        int currentState=0;
        linearLayout.removeAllViews();
        try {
            tasks = Task.sort(tasks);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Task curr;
        try {
            previousState = tasks.get(0).timeline();
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
                        //TODO: add task in firebase and get id
                        linearLayout.removeView(buttonView);
                        buttonView.setChecked(true);
                        linearLayout.addView(buttonView);
                    }
                }
            });
            checkbox.setTextSize(20);
            checkbox.setText(curr.toString());
            checkbox.setId(Integer.parseInt(curr.getIdTask()));
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
            TextView label = new TextView(th);
            label.setText(tasks.get(tasks.size()-1).timeline());
            label.setTextSize(20);
            layout.addView(label,0);
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
        //TODO: menu
        } else if (id == R.id.nav_add_category) {

        } else if (id == R.id.nav_change_category) {

        } else if (id == R.id.nav_change_category) {

        }else if (id == 0) {
            Toast toast_show_datetime = Toast.makeText(getApplicationContext(),
                    "right", Toast.LENGTH_LONG);
            toast_show_datetime.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
