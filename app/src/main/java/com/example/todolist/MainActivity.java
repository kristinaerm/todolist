package com.example.todolist;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle toggle;
    private LinkedList<Task> tasks = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AppCompatActivity th = this;
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Ввод текста задачи

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(th);
                builder.setTitle(getString(R.string.enter_task));

                // Set up the input
                final EditText input = new EditText(th);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast toast_show_description = Toast.makeText(getApplicationContext(),
                                input.getText(), Toast.LENGTH_SHORT);
                        toast_show_description.show();

                        final View date_dialog_view = View.inflate(th, R.layout.date_layout, null);
                        final AlertDialog date_alert_dialog = new AlertDialog.Builder(th).create();

                        date_dialog_view.findViewById(R.id.button_date).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                DatePicker datePicker = (DatePicker) date_dialog_view.findViewById(R.id.datePicker1);
                                Toast toast_show_date = Toast.makeText(getApplicationContext(),
                                        datePicker.getYear()+"-"+datePicker.getMonth()+"-"+datePicker.getDayOfMonth(), Toast.LENGTH_SHORT);
                                toast_show_date.show();


                                final View time_dialog_view = View.inflate(th, R.layout.time_layout, null);
                                final AlertDialog time_alert_dialog = new AlertDialog.Builder(th).create();

                                time_dialog_view.findViewById(R.id.button_time).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        TimePicker timepicker = (TimePicker) time_dialog_view.findViewById(R.id.timePicker1);
                                        Toast toast_show_time = Toast.makeText(getApplicationContext(),
                                                timepicker.getCurrentHour()+":"+timepicker.getCurrentMinute(), Toast.LENGTH_SHORT);
                                        toast_show_time.show();
                                        time_alert_dialog.dismiss();
                                    }});
                                time_alert_dialog.setView(time_dialog_view);
                                time_alert_dialog.show();

                                date_alert_dialog.dismiss();

                            }});
                        date_alert_dialog.setView(date_dialog_view);
                        date_alert_dialog.show();

                        /*
                        final Dialog datePickerDialog = new Dialog(th);

                        datePickerDialog.setContentView(R.layout.date_layout);
                        datePickerDialog.setTitle(getString(R.string.enter_task_date));
                        datePickerDialog.show();
                        datePickerDialog.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                DatePicker dp = (DatePicker) findViewById(R.id.datePicker1);
                                int day = dp.getDayOfMonth();
                                int month = dp.getMonth();
                                int year = dp.getYear();

                                Toast toast1 = Toast.makeText(getApplicationContext(),
                                        dp.getDayOfMonth()+"-"+dp.getMonth()+"-"+dp.getYear(), Toast.LENGTH_SHORT);
                                toast1.show();

                                datePickerDialog.dismiss();
                                final Dialog timePickerDialog = new Dialog(th);

                                timePickerDialog.setContentView(R.layout.time_layout);
                                timePickerDialog.setTitle(getString(R.string.enter_task_time));
                                timePickerDialog.show();
                                timePickerDialog.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
                                        int getHour = tp.getCurrentHour();
                                        int getMinute = tp.getCurrentMinute();
                                        timePickerDialog.dismiss();
                                        Toast toast2 = Toast.makeText(getApplicationContext(),
                                                "Task will be added!", Toast.LENGTH_SHORT);
                                        toast2.show();
                                    }
                                });
                            }
                        });
                        */


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


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(toggle);

        ListView lv_navigation_drawer = (ListView) findViewById(R.id.lv_navigation_drawer);
        lv_navigation_drawer.setAdapter(new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                new String[]{"Screen 1", "Screen 2", "Screen 3"}));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }
}
