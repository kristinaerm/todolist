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
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Calendar;
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
    ArrayMap<Integer, String> menuCategory = new ArrayMap<>();
    //temporary
    String idCategory = "";
    NotificationHelper notificationHelper;
    boolean isEnteringText = false;
    boolean isPickingDate = false;
    boolean isPickingTime = false;
    boolean isEnteringCategory = false;
    String enteredText = null;
    Date pickedTime = null;
    Date pickedDate = null;
    String enteredCategory = null;
    private static int countForNotifications = 0;
    private static int countForPendings = 0;
    LinkedList<AlarmManager> alarmManagers = new LinkedList<>();
    LinkedList<PendingIntent> pendingIntents = new LinkedList<>();
    View date_dialog_view;
    AlertDialog date_alert_dialog;
    DatePicker datePicker1;
    View time_dialog_view;
    AlertDialog time_alert_dialog;
    EditText inputDescr;
    EditText inputCategory;
    Toolbar toolbar;



    /*
    public void sendOnChannelTask(String title, String text){
        NotificationCompat.Builder nb = notificationHelper.getChannelTaskNotification(title,text);
    notificationHelper.getNotificationManager().notify(-11, nb.build());
    }
    */


    public static int getCountForNotifications() {
        return countForNotifications;
    }

    public static void IncCount() {
        countForNotifications++;
    }


    public static int getCountForPendings() {
        return countForPendings;
    }

    public static void IncCount1() {
        countForPendings++;
    }


    private void startAlarm(Calendar c, String title, String text) {
        if (c.before(Calendar.getInstance())) return;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManagers.add(alarmManager);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, getCountForPendings(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntents.add(pendingIntent);
        IncCount1();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }

    private void cancelAlarms() {

        //TODO check
        for (int i = 0; i < tasks.size(); i++) {
            //try{
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, 0);
            alarmManager.cancel(pendingIntent);
            //} catch (IndexOutOfBoundsException ex){

            //}
        }
        countForPendings = 0;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_and_navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        notificationHelper = new NotificationHelper(this);
        /*
        sendOnChannelTask("main", "main");
        */
        /*
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, 2);
        startAlarm(c);
        Calendar cc = Calendar.getInstance();
        cc.add(Calendar.SECOND, 7);
        startAlarm(cc);
        cancelAlarms();
        */

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


        fab.setOnClickListener(fabClickListener);

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
        String catName = all?getString(R.string.all_tasks):db.getCategoryNameById(idCategory);
        toolbar.setTitle(catName);

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
        //enteredText = "";

        inputDescr = new EditText(th);
        inputDescr.setInputType(InputType.TYPE_CLASS_TEXT);
        inputDescr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enteredText = inputDescr.getText().toString();
                Toast toast_show_description = Toast.makeText(getApplicationContext(),
                        enteredText, Toast.LENGTH_SHORT);
                toast_show_description.show();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        builder.setView(inputDescr);
        builder.setCancelable(false);

        builder.setPositiveButton("OK", textOKListener);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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


    DialogInterface.OnClickListener textOKListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            isEnteringText = false;
            enteredText = null;

            description = inputDescr.getText().toString();

            isPickingDate = true;
            showInputDateDialog(null);
        }
    };


    private void showInputDateDialog(Date initial) {
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
        datePicker1 = (DatePicker) date_dialog_view.findViewById(R.id.datePicker1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //pickedDate = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker1.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {

                pickedDate = new Date(year, month, dayOfMonth);
            }
        });
        if (initial != null) {
            datePicker1.updateDate(initial.getYear(), initial.getMonth(), initial.getDate());
        }
        date_alert_dialog.setCancelable(false);
        date_alert_dialog.setView(date_dialog_view);
        date_alert_dialog.show();
    }


    View.OnClickListener dateInputOKListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isPickingDate = false;
            DatePicker datePicker = (DatePicker) date_dialog_view.findViewById(R.id.datePicker1);
            datetime = new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

            Toast toast_show_date = Toast.makeText(getApplicationContext(),
                    datetime.getYear() + "-" + datetime.getMonth() + "-" + datetime.getDate(), Toast.LENGTH_SHORT);
            toast_show_date.show();

            isPickingTime = true;
            showInputTimeDialog(null);

            date_alert_dialog.dismiss();

        }
    };

    private void showInputTimeDialog(Date initial) {
        isPickingDate = false;
        pickedDate = null;
        isEnteringText = false;
        enteredText = null;

        time_dialog_view = View.inflate(th, R.layout.time_layout, null);
        time_alert_dialog = new AlertDialog.Builder(th).create();

        TimePicker timepicker = (TimePicker) time_dialog_view.findViewById(R.id.timePicker1);
        timepicker.setIs24HourView(true);
        if (initial != null) {
            timepicker.setCurrentHour(new Integer(initial.getHours()));
            timepicker.setCurrentMinute(new Integer(initial.getMinutes()));
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        //pickedTime = new Date(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.HOUR),c.get(Calendar.MINUTE));
        timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                pickedTime = new Date(2019, 1, 1, hourOfDay, minute);
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

    View.OnClickListener timeInputOKListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            isPickingTime = false;
            TimePicker timepicker = (TimePicker) time_dialog_view.findViewById(R.id.timePicker1);
            Toast toast_show_time = Toast.makeText(getApplicationContext(),
                    timepicker.getCurrentHour() + ":" + timepicker.getCurrentMinute(), Toast.LENGTH_SHORT);
            toast_show_time.show();
            datetime = new Date(datetime.getYear(), datetime.getMonth(), datetime.getDate(), timepicker.getCurrentHour().intValue(), timepicker.getCurrentMinute().intValue());
            Toast toast_show_datetime = Toast.makeText(getApplicationContext(),
                    ((datetime.getDate() > 9) ? (datetime.getDate()) : ("0" + datetime.getDate())) + "." + ((datetime.getMonth() > 9) ? (datetime.getMonth()) : ("0" + datetime.getMonth())) + "." + datetime.getYear() + " " + ((datetime.getHours() > 9) ? (datetime.getHours()) : ("0" + datetime.getHours())) + ":" + ((datetime.getMinutes() > 9) ? (datetime.getMinutes()) : ("0" + datetime.getMinutes())), Toast.LENGTH_LONG);
            toast_show_datetime.show();
            Task task = new Task(description, idCategory, ((datetime.getDate() > 9) ? (datetime.getDate()) : ("0" + datetime.getDate())) + "." + (((datetime.getMonth() + 1) > 9) ? ((datetime.getMonth() + 1)) : ("0" + (datetime.getMonth() + 1))) + "." + datetime.getYear() + " " + ((datetime.getHours() > 9) ? (datetime.getHours()) : ("0" + datetime.getHours())) + ":" + ((datetime.getMinutes() > 9) ? (datetime.getMinutes()) : ("0" + datetime.getMinutes())));
            //TODO: add task in firebase and get id
            tasks.add(task);

            //fire.writeNewTask(task);
            db.addTask(task);

            sortAndShowTasks(layout);
            time_alert_dialog.dismiss();
        }
    };

    private static final String ENTERED_TEXT = "EnteredText";
    private static final String ENTERED_DATE = "EnteredDate";
    private static final String ENTERED_TIME = "EnteredTime";
    private static final String ENTERED_CATEGORY = "EnteredCategory";
    private static final String IS_ENTERING_TEXT = "IsEnteringText";
    private static final String IS_ENTERING_DATE = "IsEnteringDate";
    private static final String IS_ENTERING_TIME = "IsEnteringTime";
    private static final String IS_ENTERING_CATEGORY = "IsEnteringCategory";
    private static final String DESCRIPTION = "Description";
    private static final String NAME_CATEGORY = "NameCategory";
    private static final String DATETIME = "DateTime";
    private static final String USER_ID = "UserId";
    private static final String ALL = "All";
    private static final String ID_CATEGORY = "IdCategory";
    private static final String COUNT_FOR_PENDING = "CountForPend";
    private static final String COUNT_FOR_NOTIFICATION = "CountForNotif";

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


        //cancelAlarms();

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
        datetime = (Date) savedInstanceState.getSerializable(DATETIME);
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
        Date enteredDate = (Date) savedInstanceState.getSerializable(ENTERED_DATE);
        if (isPickingDate ) {
            pickedDate = enteredDate;
            showInputDateDialog(enteredDate);
        }
        Date enteredTime = (Date) savedInstanceState.getSerializable(ENTERED_TIME);
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

        Toast toast_inside_prepareOptionsMenu = Toast.makeText(getApplicationContext(),
                "inside " + subMenu.size(), Toast.LENGTH_SHORT);
        toast_inside_prepareOptionsMenu.show();

        categories = db.getCategorybyIdUser(userId);
        if (categories.size() == 0) {
            db.addNoCategory(new Category(getString(R.string.no_category), R.drawable.label, userId));
            categories = db.getCategorybyIdUser(userId);
        }

        int s = menuCategory.size();
        if (subMenu.size() > 2) {
            for (int i = 0; i < s; i++) {
                int id = subMenu.getItem(0).getItemId();
                subMenu.removeItem(id);
                menuCategory.remove(0);
            }
        }
        //TODO: firebase upload categories
        for (int i = 0; i < categories.size(); i++) {
            //TODO: clear except nocategory and add new categories
            menuCategory.put(i, categories.get(i).getIdCategory());
            subMenu.add(R.id.nav_group_categories, i, Menu.NONE, categories.get(i).getName()).setIcon(getResources().getDrawable(R.drawable.label));
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

    public void sortAndShowTasks(final LinearLayout linearLayout) {
        int previousState = 0;
        int currentState = 0;
        linearLayout.removeAllViews();
        cancelAlarms();
        tasks.clear();

        String catName = all?getString(R.string.all_tasks):db.getCategoryNameById(idCategory);
        toolbar.setTitle(catName);

/*
        Query query1 = FirebaseDatabase.getInstance().getReference("category:")
                .orderByChild("idUser")
                .equalTo(userId);
        query1.addListenerForSingleValueEvent(valueEventListener);
*/
        categories = db.getCategorybyIdUser(userId);

        if (categories.size() == 0) {
            db.addNoCategory(new Category(getString(R.string.no_category), R.drawable.label, userId));
            categories = db.getCategorybyIdUser(userId);
        }


        if (idCategory.equals("")) {
            int k = 0;

            while ((k < categories.size()) && (!categories.get(k).getName().equals(getString(R.string.no_category)))) {
                k++;
            }
            if (k < categories.size())
                idCategory = categories.get(k).getIdCategory();
        }

        if (all) {
            //for (int i=0; i<categories.size(); i++){
                /*
                Query query = FirebaseDatabase.getInstance().getReference("task:")
                        .orderByChild("idCategory")
                        .equalTo(categories.get(i).getIdCategory());
                query.addListenerForSingleValueEvent(valueEventListenerTasks);
                */
            for (int i = 0; i < categories.size(); i++) {
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
            if (tasks.size() != 0) {
                previousState = tasks.get(0).timeline();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
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
                        //TODO: delete task from firebase by tag
                        linearLayout.removeView(buttonView);
                        db.deleteTask((String) buttonView.getTag());
                        sortAndShowTasks(layout);
                    }
                }
            });
            checkbox.setTextSize(20);
            checkbox.setText(curr.toString());
            checkbox.setTag(curr.getIdTask());
            //checkbox.setId(Integer.parseInt(curr.getIdTask()));
            if (previousState != currentState) {
                TextView label = new TextView(th);
                label.setText(previousState);
                label.setTextSize(20);
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
            //TODO CHECK
            //super.onBackPressed();
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
        if (id == R.id.action_settings) {
            //TODO: Settings of language
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInputCategoryDialog(String initial) {
        isEnteringCategory = true;
        AlertDialog.Builder category_builder = new AlertDialog.Builder(th);
        category_builder.setTitle(getString(R.string.enter_category));
        //enteredCategory = "";

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

        category_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enteredCategory = null;
                isEnteringCategory = false;
                nameCategory = inputCategory.getText().toString();
                db.addCategory(new Category(nameCategory, R.drawable.label, userId));
                invalidateOptionsMenu();
            }
        });
        category_builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

            //TODO добавить добавление категории в фаей и базу и обновить меню
            showInputCategoryDialog(null);

        } else if (id == R.id.nav_change_category) {
            Intent intent = new Intent(this, ChangeCategoryActivity.class);
            intent.putExtra("idUser", userId);//передаю в изменение активити id пользователя который вошел
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
