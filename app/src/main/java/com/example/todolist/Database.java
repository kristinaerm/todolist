package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "todolist";
    private static final String TABLE_TASKS = "task";
    private static final String TABLE_CATEGORY = "category";
    private static final String TABLE_USER = "user";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASK_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + "idTask" + " INTEGER PRIMARY KEY," + "name" + " TEXT,"
                + "idCategory" + " INTEGER, " + "timeDate" + " TEXT," +
                " FOREIGN KEY (" + "idCategory" + ") REFERENCES " + TABLE_CATEGORY + "(" + "idCategory" + "))";


        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + "idCategory" + " INTEGER PRIMARY KEY," + "name" + " TEXT,"
                + "idIcon" + " INTEGER," + "idUser" + " INTEGER," + " FOREIGN KEY (" + "idUser" + ") REFERENCES " + TABLE_USER + "(" + "idUser" + "))";


        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + "idUser" + " INTEGER PRIMARY KEY," + "login" + " TEXT,"
                + "pass" + " TEXT)";


        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_TASK_TABLE);
    }

    // пересоздаст таблицы в БД
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        onCreate(db);
    }

    public void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", task.getName());
        values.put("idCategory", task.getIdCategory());
        values.put("timeDate", task.getTimeDate());

        db.insert(TABLE_TASKS, null, values);
        db.close();
    }

    public void addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("idIcon", category.getIdIcon());
        values.put("idUser", category.getIdUser());

        db.insert(TABLE_CATEGORY, null, values);
        db.close();
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("login", user.getLogin());
        values.put("pass", user.getPass());


        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public Task getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS, new String[]{"idTask",
                        "name", "idCategory", "timeDate"}, "idTask" + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Task task = new Task(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));

        return task;
    }

    public Category getCategory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CATEGORY, new String[]{"idCategory",
                        "name", "idIcon", "idUser"}, "idCategory" + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Category category = new Category(cursor.getString(0), cursor.getString(1), Integer.parseInt(cursor.getString(2)), cursor.getString(3));

        return category;
    }

    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CATEGORY, new String[]{"idUser",
                        "login", "pass"}, "idUser" + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        User user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2));

        return user;
    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<Task>();
        String selectQuery = "SELECT  * FROM " + TABLE_TASKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setIdTask(cursor.getString(0));
                task.setName(cursor.getString(1));
                task.setIdCategory(cursor.getString(2));
                task.setTimeDate(cursor.getString(3));
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        return taskList;
    }

    public List<Category> getAllCategorys() {
        List<Category> categoryList = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setIdCategory(cursor.getString(0));
                category.setName(cursor.getString(1));
                category.setIdIcon(Integer.parseInt(cursor.getString(2)));
                category.setIdUser(cursor.getString(3));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }

        return categoryList;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<User>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setIdUser(cursor.getString(0));
                user.setLogin(cursor.getString(1));
                user.setPass(cursor.getString(2));
                userList.add(user);
            } while (cursor.moveToNext());
        }

        return userList;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", task.getName());
        values.put("idCategory", task.getIdCategory());
        values.put("timeDate", task.getTimeDate());
        return db.update(TABLE_TASKS, values, "idTask" + " = ?",
                new String[]{String.valueOf(task.getIdTask())});
    }

    public int updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("idIcon", category.getIdIcon());
        values.put("idUser", category.getIdUser());
        return db.update(TABLE_CATEGORY, values, "idCategory" + " = ?",
                new String[]{String.valueOf(category.getIdCategory())});
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("login", user.getLogin());
        values.put("pass", user.getPass());
        return db.update(TABLE_USER, values, "idUser" + " = ?",
                new String[]{String.valueOf(user.getIdUser())});
    }

    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, "idTask" + " = ?", new String[]{String.valueOf(task.getIdTask())});
        db.close();
    }

    public void deleteCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORY, "idCategory" + " = ?", new String[]{String.valueOf(category.getIdCategory())});
        db.close();
    }

    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, "idUser" + " = ?", new String[]{String.valueOf(user.getIdUser())});
        db.close();
    }

    public void deleteAllTask() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, null, null);
        db.close();
    }

    public void deleteAllCategory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORY, null, null);
        db.close();
    }

    public void deleteAllUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null);
        db.close();
    }

    public int getTaskCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int getCategoryCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int getUseerCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }
}
