package com.example.pulkit.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoOpenHelper extends SQLiteOpenHelper {

    public final static String TODO_TABLE_NAME = "todo";
    public final static String TODO_TITLE = "title";
    public final static String TODO_DESCRIPTION = "description";
    public final static String TODO_ID = "_id";
    public final static String TODO_PRIORITY = "priority";
    public final static String TODO_CATEGORY = "category";
    public final static String TODO_DATE = "date";
    public final static String TODO_TIME = "time";
    public static final String TODO_ISCHECKED="isChecked";



    public static TodoOpenHelper todoOpenHelper;

    public static TodoOpenHelper getTodoOpenHelperInstance(Context context) {
        if (todoOpenHelper == null) {
            todoOpenHelper = new TodoOpenHelper(context);
        }
        return todoOpenHelper;
    }


    private TodoOpenHelper(Context context) {
        super(context, "todo.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + TODO_TABLE_NAME +" ( " + TODO_ID +
                " integer primary key autoincrement, " + TODO_TITLE +" text, "
                + TODO_DESCRIPTION + " text, "+ TODO_ISCHECKED + " text default false, " + TODO_CATEGORY+ " integer, " + TODO_PRIORITY+ " integer, "
                + TODO_TIME + " text, " + TODO_DATE + " text);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
