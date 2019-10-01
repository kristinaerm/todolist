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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle toggle;
    private LinkedList<Task> tasks = new LinkedList<>();
    LinearLayout layout;
    String description;
    Date datetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (LinearLayout)findViewById(R.id.linearCheckboxLayout);

        final AppCompatActivity th = this;
        FloatingActionButton fab = findViewById(R.id.fab);
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
                                Toast toast_show_date = Toast.makeText(getApplicationContext(),
                                        datePicker.getYear() + "-" + datePicker.getMonth() + "-" + datePicker.getDayOfMonth(), Toast.LENGTH_SHORT);
                                toast_show_date.show();
                                datetime = new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                                final View time_dialog_view = View.inflate(th, R.layout.time_layout, null);
                                final AlertDialog time_alert_dialog = new AlertDialog.Builder(th).create();

                                time_dialog_view.findViewById(R.id.button_time).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        TimePicker timepicker = (TimePicker) time_dialog_view.findViewById(R.id.timePicker1);
                                        Toast toast_show_time = Toast.makeText(getApplicationContext(),
                                                timepicker.getCurrentHour() + ":" + timepicker.getCurrentMinute(), Toast.LENGTH_SHORT);
                                        toast_show_time.show();
                                        datetime = new Date(datetime.getYear(),datetime.getMonth(), datetime.getDay(), timepicker.getCurrentHour().intValue(), timepicker.getCurrentMinute().intValue());
                                        final CheckBox checkbox = new CheckBox(th);
                                        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                                                if (isChecked){
                                                    layout.removeView(buttonView);
                                                    buttonView.setChecked(true);
                                                    layout.addView(buttonView);
                                                }
                                            }
                                        });
                                        checkbox.setTextSize(20);
                                        checkbox.setText(description+"\n"+datetime.getDate()+"."+datetime.getMonth()+"."+datetime.getYear()+" "+datetime.getHours()+":"+((datetime.getMinutes()>9)?(datetime.getMinutes()):("0"+datetime.getMinutes())));
                                        //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        layout.addView(checkbox, 0);
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
