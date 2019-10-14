package com.example.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedList;

public class MainAndNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Extras
    public static final String ID_USER = "com.example.todolist.MainAndNavigation.IdUser";
    public static final String ALL_TASKS = "com.example.todolist.MainAndNavigation.AllTasks";
    public static final String ID_CATEGORY_TO_SELECT = "com.example.todolist.MainAndNavigation.IdCategoryToSelect";

    //For tasks and categories
    private LinkedList<Task> tasks = new LinkedList<>();
    private LinkedList<Category> categories = new LinkedList<>();
    private ArrayMap<Integer, String> menuCategory = new ArrayMap<>();
    private Menu subMenu;
    private Database db = new Database(this);

    //Elements and dialogs
    private AppCompatActivity th = new AppCompatActivity();
    private LinearLayout layout;
    private View date_dialog_view;
    private AlertDialog date_alert_dialog;
    private DatePicker datePicker;
    private View time_dialog_view;
    private AlertDialog time_alert_dialog;
    private EditText inputDescr;
    private EditText inputCategory;
    private Toolbar toolbar;

    //New task
    private String description;
    private String nameCategory;
    private Calendar datetime = Calendar.getInstance();

    //Extra info from other activities
    private String userId;
    private boolean all = true;
    private String idCategory = "";

    //For notifications
    private static int countForNotifications = 0;
    private static int countForPendings = 0;
    private LinkedList<AlarmManager> alarmManagers = new LinkedList<>();
    private LinkedList<PendingIntent> pendingIntents = new LinkedList<>();

    //For saving instance
    private boolean isEnteringText = false;
    private boolean isEnteringCategory = false;
    private boolean isPickingDate = false;
    private boolean isPickingTime = false;
    private String enteredText = null;
    private String enteredCategory = null;
    private Calendar pickedTime = null;
    private Calendar pickedDate = null;


    public static int getCountForNotifications() {
        return countForNotifications;
    }

    public static void IncrementCountForNotifications() {
        countForNotifications++;
    }


    public static int getCountForPendings() {
        return countForPendings;
    }

    public static void IncrementCountForPendings() {
        countForPendings++;
    }


    private void startAlarm(Calendar c, String title, String text) {
        if (c.before(Calendar.getInstance())) return;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManagers.add(alarmManager);

        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra(AlertReceiver.TITLE, title);
        intent.putExtra(AlertReceiver.TEXT, text);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, getCountForPendings(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntents.add(pendingIntent);
        IncrementCountForPendings();

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarms() {
        for (int i = 0; i < tasks.size(); i++) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(this, AlertReceiver.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, 0);

            alarmManager.cancel(pendingIntent);
        }
        countForPendings = 0;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_and_navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Database.setUndeletableCategory(getString(R.string.no_category_in_db));

        //Receive extras
        userId = getIntent().getStringExtra(ID_USER);
        all = getIntent().getBooleanExtra(ALL, true);
        idCategory = getIntent().getStringExtra(ID_CATEGORY_TO_SELECT);

        //Initialize elements
        th = this;
        layout = (LinearLayout) findViewById(R.id.linearLayoutMainNavigation);

        sortAndShowTasks(layout);

        //FAB customization
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Drawable myFabSrc = getResources().getDrawable(android.R.drawable.ic_input_add);
        myFabSrc.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        fab.setImageDrawable(myFabSrc);
        fab.setOnClickListener(fabClickListener);

        //Drawer initialization
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Navigation initialization
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Menu initialization
        Menu menu = navigationView.getMenu();
        MenuItem category = menu.getItem(1);
        subMenu = category.getSubMenu();

        //Toolbar customization
        String categoryNameToShowInToolbarTitle = all ? getString(R.string.all_tasks) : db.getCategoryNameById(idCategory);
        categoryNameToShowInToolbarTitle = (categoryNameToShowInToolbarTitle.equals(getString(R.string.no_category_in_db)))?getString(R.string.no_category):categoryNameToShowInToolbarTitle;
        toolbar.setTitle(categoryNameToShowInToolbarTitle);

    }

    View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showInputDescriptionDialog(null);
        }
    };

    private void showInputDescriptionDialog(String initial) {
        isEnteringText = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(th);
        builder.setTitle(getString(R.string.enter_task));

        inputDescr = new EditText(th);
        inputDescr.setInputType(InputType.TYPE_CLASS_TEXT);
        inputDescr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enteredText = inputDescr.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        builder.setView(inputDescr);
        builder.setCancelable(false);

        builder.setPositiveButton(getString(R.string.OK), textOKListener);
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isEnteringText = false;
                enteredText = null;
                dialog.cancel();
            }
        });
        if (initial != null) {
            inputDescr.setText(initial);
        }
        builder.show();
    }

    private DialogInterface.OnClickListener textOKListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            isEnteringText = false;
            enteredText = null;

            description = inputDescr.getText().toString();

            isPickingDate = true;
            showInputDateDialog(null);
        }
    };


    private void showInputDateDialog(Calendar initial) {
        isEnteringText = false;
        enteredText = null;
        date_dialog_view = View.inflate(th, R.layout.date_layout, null);
        date_alert_dialog = new AlertDialog.Builder(th).create();

        date_dialog_view.findViewById(R.id.button_date).setOnClickListener(dateInputOKListener);
        date_dialog_view.findViewById(R.id.button_date_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPickingDate = false;
                pickedDate = null;
                isEnteringText = false;
                enteredText = null;
                date_alert_dialog.dismiss();
            }
        });

        datePicker = (DatePicker) date_dialog_view.findViewById(R.id.datePicker1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {

                pickedDate = Calendar.getInstance();
                pickedDate.set(Calendar.YEAR, year);
                pickedDate.set(Calendar.MONTH, month);
                pickedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        });
        if (initial != null) {
            datePicker.updateDate(initial.get(Calendar.YEAR), initial.get(Calendar.MONTH), initial.get(Calendar.DAY_OF_MONTH));
        }
        date_alert_dialog.setCancelable(false);
        date_alert_dialog.setView(date_dialog_view);
        date_alert_dialog.show();
    }

    private View.OnClickListener dateInputOKListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isPickingDate = false;

            DatePicker datePicker = (DatePicker) date_dialog_view.findViewById(R.id.datePicker1);
            datetime.set(Calendar.YEAR, datePicker.getYear());
            datetime.set(Calendar.MONTH, datePicker.getMonth());
            datetime.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

            isPickingTime = true;
            showInputTimeDialog(null);

            date_alert_dialog.dismiss();

        }
    };

    private void showInputTimeDialog(Calendar initial) {
        isPickingDate = false;
        pickedDate = null;
        isEnteringText = false;
        enteredText = null;

        time_dialog_view = View.inflate(th, R.layout.time_layout, null);
        time_alert_dialog = new AlertDialog.Builder(th).create();

        TimePicker timepicker = (TimePicker) time_dialog_view.findViewById(R.id.timePicker1);
        timepicker.setIs24HourView(true);
        if (initial != null) {
            timepicker.setCurrentHour(new Integer(initial.get(Calendar.HOUR_OF_DAY)));
            timepicker.setCurrentMinute(new Integer(initial.get(Calendar.MINUTE)));
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                pickedTime = Calendar.getInstance();
                pickedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                pickedTime.set(Calendar.MINUTE, minute);
            }
        });
        time_dialog_view.findViewById(R.id.button_time).setOnClickListener(timeInputOKListener);
        time_dialog_view.findViewById(R.id.button_time_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPickingTime = false;
                pickedTime = null;
                isPickingDate = false;
                pickedDate = null;
                isEnteringText = false;
                enteredText = null;
                time_alert_dialog.dismiss();
            }
        });
        time_alert_dialog.setCancelable(false);
        time_alert_dialog.setView(time_dialog_view);
        time_alert_dialog.show();
    }

    private View.OnClickListener timeInputOKListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            isPickingTime = false;
            TimePicker timepicker = (TimePicker) time_dialog_view.findViewById(R.id.timePicker1);

            datetime.set(Calendar.HOUR_OF_DAY, timepicker.getCurrentHour().intValue());
            datetime.set(Calendar.MINUTE, timepicker.getCurrentMinute().intValue());

            Task task = new Task(description, idCategory, ((datetime.get(Calendar.DAY_OF_MONTH) > 9) ? (datetime.get(Calendar.DAY_OF_MONTH)) : ("0" + datetime.get(Calendar.DAY_OF_MONTH))) + "." + (((datetime.get(Calendar.MONTH) + 1) > 9) ? ((datetime.get(Calendar.MONTH) + 1)) : ("0" + (datetime.get(Calendar.MONTH) + 1))) + "." + datetime.get(Calendar.YEAR) + " " + ((datetime.get(Calendar.HOUR_OF_DAY) > 9) ? (datetime.get(Calendar.HOUR_OF_DAY)) : ("0" + datetime.get(Calendar.HOUR_OF_DAY))) + ":" + ((datetime.get(Calendar.MINUTE) > 9) ? (datetime.get(Calendar.MINUTE)) : ("0" + datetime.get(Calendar.MINUTE))));
            tasks.add(task);
            db.addTask(task);

            sortAndShowTasks(layout);
            time_alert_dialog.dismiss();
        }
    };

    private static final String ENTERED_TEXT = "com.example.todolist.MainAndNavigation.EnteredText";
    private static final String ENTERED_DATE = "com.example.todolist.MainAndNavigation.EnteredDate";
    private static final String ENTERED_TIME = "com.example.todolist.MainAndNavigation.EnteredTime";
    private static final String ENTERED_CATEGORY = "com.example.todolist.MainAndNavigation.EnteredCategory";
    private static final String IS_ENTERING_TEXT = "com.example.todolist.MainAndNavigation.IsEnteringText";
    private static final String IS_ENTERING_DATE = "com.example.todolist.MainAndNavigation.IsEnteringDate";
    private static final String IS_ENTERING_TIME = "com.example.todolist.MainAndNavigation.IsEnteringTime";
    private static final String IS_ENTERING_CATEGORY = "com.example.todolist.MainAndNavigation.IsEnteringCategory";
    private static final String DESCRIPTION = "com.example.todolist.MainAndNavigation.Description";
    private static final String NAME_CATEGORY = "com.example.todolist.MainAndNavigation.NameCategory";
    private static final String DATETIME = "com.example.todolist.MainAndNavigation.DateTime";
    private static final String USER_ID = "com.example.todolist.MainAndNavigation.UserId";
    private static final String ALL = "com.example.todolist.MainAndNavigation.All";
    private static final String ID_CATEGORY = "com.example.todolist.MainAndNavigation.IdCategory";
    private static final String COUNT_FOR_PENDING = "com.example.todolist.MainAndNavigation.CountForPend";
    private static final String COUNT_FOR_NOTIFICATION = "com.example.todolist.MainAndNavigation.CountForNotif";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ENTERED_TEXT, enteredText);
        outState.putSerializable(ENTERED_DATE, pickedDate);
        outState.putSerializable(ENTERED_TIME, pickedTime);
        outState.putString(ENTERED_CATEGORY, enteredCategory);
        outState.putBoolean(IS_ENTERING_CATEGORY, isEnteringCategory);
        outState.putBoolean(IS_ENTERING_TEXT, isEnteringText);
        outState.putBoolean(IS_ENTERING_DATE, isPickingDate);
        outState.putBoolean(IS_ENTERING_TIME, isPickingTime);
        outState.putString(DESCRIPTION, description);
        outState.putString(NAME_CATEGORY, nameCategory);
        outState.putSerializable(DATETIME, datetime);
        outState.putString(USER_ID, userId);
        outState.putBoolean(ALL, all);
        outState.putString(ID_CATEGORY, idCategory);
        outState.putInt(COUNT_FOR_PENDING, countForPendings);
        outState.putInt(COUNT_FOR_NOTIFICATION, countForNotifications);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        description = savedInstanceState.getString(DESCRIPTION);
        nameCategory = savedInstanceState.getString(NAME_CATEGORY);
        userId = savedInstanceState.getString(USER_ID);
        idCategory = savedInstanceState.getString(ID_CATEGORY);
        countForPendings = savedInstanceState.getInt(COUNT_FOR_PENDING);
        countForNotifications = savedInstanceState.getInt(COUNT_FOR_NOTIFICATION);
        datetime = (Calendar) savedInstanceState.getSerializable(DATETIME);
        all = savedInstanceState.getBoolean(ALL);
        isEnteringCategory = savedInstanceState.getBoolean(IS_ENTERING_CATEGORY);
        isEnteringText = savedInstanceState.getBoolean(IS_ENTERING_TEXT);
        isPickingDate = savedInstanceState.getBoolean(IS_ENTERING_DATE);
        isPickingTime = savedInstanceState.getBoolean(IS_ENTERING_TIME);

        sortAndShowTasks(layout);

        String entered = savedInstanceState.getString(ENTERED_TEXT);
        if (isEnteringText) {
            enteredText = entered;
            showInputDescriptionDialog(entered);
        }
        Calendar enteredDate = (Calendar) savedInstanceState.getSerializable(ENTERED_DATE);
        if (isPickingDate) {
            pickedDate = enteredDate;
            showInputDateDialog(enteredDate);
        }
        Calendar enteredTime = (Calendar) savedInstanceState.getSerializable(ENTERED_TIME);
        if (isPickingTime) {
            pickedTime = enteredTime;
            showInputTimeDialog(enteredTime);
        }
        String enteredCat = savedInstanceState.getString(ENTERED_CATEGORY);
        if (isEnteringCategory) {
            enteredCategory = enteredCat;
            showInputCategoryDialog(enteredCat);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {

        categories = db.getCategorybyIdUser(userId);

        if (categories.size() == 0) {
            db.addNoCategory(new Category(getString(R.string.no_category_in_db), R.drawable.label, userId));
            categories = db.getCategorybyIdUser(userId);
        }

        if (subMenu.size() > 2) {
            clearMenu(subMenu, menuCategory);
        }
        for (int i = 0; i < categories.size(); i++) {
            menuCategory.put(i, categories.get(i).getIdCategory());
            String categoryNameToShow = (categories.get(i).getName().equals(getString(R.string.no_category_in_db)))?getString(R.string.no_category):categories.get(i).getName();
            subMenu.add(R.id.nav_group_categories, i, Menu.NONE, categoryNameToShow).setIcon(getResources().getDrawable(R.drawable.label));
        }

        return true;
    }

    public int clearMenu (Menu menu, ArrayMap<Integer, String> menuCat){
        int s = menuCat.size();
        for (int i = 0; i < s; i++) {
            int id = menu.getItem(0).getItemId();
            menu.removeItem(id);
            menuCat.remove(0);
        }
        return menuCat.size();
    }

    public void sortAndShowTasks(final LinearLayout linearLayout) {
        int previousState = 0;
        int currentState = 0;

        linearLayout.removeAllViews();
        cancelAlarms();
        tasks.clear();

        String catName = all ? getString(R.string.all_tasks) : db.getCategoryNameById(idCategory);
        toolbar.setTitle(catName);

        categories = db.getCategorybyIdUser(userId);

        if (categories.size() == 0) {
            db.addNoCategory(new Category(getString(R.string.no_category_in_db), R.drawable.label, userId));
            categories = db.getCategorybyIdUser(userId);
        }

        if (idCategory.equals("")) {
            int k = 0;

            while ((k < categories.size()) && (!categories.get(k).getName().equals(getString(R.string.no_category_in_db)))) {
                k++;
            }
            if (k < categories.size())
                idCategory = categories.get(k).getIdCategory();
        }

        if (all) {
            for (int i = 0; i < categories.size(); i++) {
                db.getTasksByCategory(categories.get(i).getIdCategory(), tasks);
            }
        } else {
            tasks = db.getTasksByCategory(idCategory);
        }

        try {
            tasks = Task.sort(tasks);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            if (tasks.size() != 0) {
                previousState = tasks.get(0).timeline();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Task curr;
        for (int i = 0; i < tasks.size(); i++) {
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
                        linearLayout.removeView(buttonView);
                        db.deleteTask((String) buttonView.getTag());
                        sortAndShowTasks(layout);
                    }
                }
            });
            checkbox.setTextSize(20);
            checkbox.setText(curr.toString());
            checkbox.setTag(curr.getIdTask());

            if (previousState != currentState) {
                TextView label = new TextView(th);
                label.setText(previousState);
                label.setTextSize(20);
                if (previousState == Task.TODAY) {
                    label.setTextColor(getResources().getColor(R.color.colorAccent));
                } else if (previousState == Task.EXPIRED) {
                    label.setTextColor(getResources().getColor(R.color.colorExpired));
                }
                layout.addView(label, 0);
                previousState = currentState;
            }
            linearLayout.addView(checkbox, 0);

            try {
                startAlarm(curr.getTimeDateCalendar(), curr.toString(), getString(R.string.notification_title));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        try {
            if (tasks.size() - 1 >= 0) {
                TextView label = new TextView(th);
                int time = tasks.get(tasks.size() - 1).timeline();
                label.setText(time);
                label.setTextSize(20);
                if (time == Task.TODAY) {
                    label.setTextColor(getResources().getColor(R.color.colorAccent));
                } else if (time == Task.EXPIRED) {
                    label.setTextColor(getResources().getColor(R.color.colorExpired));
                }
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
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
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
        if (id == R.id.action_settings_log_out) {
            //TODO: Settings of log out
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInputCategoryDialog(String initial) {
        isEnteringCategory = true;

        AlertDialog.Builder category_builder = new AlertDialog.Builder(th);
        category_builder.setTitle(getString(R.string.enter_category));

        inputCategory = new EditText(th);
        inputCategory.setInputType(InputType.TYPE_CLASS_TEXT);
        inputCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enteredCategory = inputCategory.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (initial != null) {
            inputCategory.setText(initial);
        }

        category_builder.setView(inputCategory);

        category_builder.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enteredCategory = null;
                isEnteringCategory = false;
                nameCategory = inputCategory.getText().toString();
                db.addCategory(new Category(nameCategory, R.drawable.label, userId));
                invalidateOptionsMenu();
            }
        });

        category_builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                enteredCategory = null;
                isEnteringCategory = false;
                dialog.cancel();
            }
        });

        category_builder.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all_tasks) {
            all = true;
            idCategory = "";
            sortAndShowTasks(layout);

        } else if (id == R.id.nav_add_category) {
            showInputCategoryDialog(null);

        } else if (id == R.id.nav_change_category) {
            Intent intent = new Intent(this, ChangeCategoryActivity.class);
            intent.putExtra(ID_USER, userId);//передаю в изменение активити id пользователя который вошел
            startActivity(intent);

        } else if (id == R.id.nav_maps) {
            //TODO maps

        } else {
            String catId = menuCategory.get(id);
            all = false;
            idCategory = catId;
            sortAndShowTasks(layout);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
