package com.example.pulkit.todo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;


public class CompletedTask extends AppCompatActivity {
    RecyclerView mRecyclerView;
    ArrayList<Todo> mtodo;
    RecyclerAdapter mAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_task);
        setTitle("Finished Task");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mtodo = new ArrayList<>();

        mAdapter = new RecyclerAdapter(this, mtodo, new RecyclerAdapter.TodosClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Todo note = mtodo.get(position);
                Snackbar.make(mRecyclerView, note.getTitle(), Snackbar.LENGTH_LONG).show();
            }

        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                Collections.swap(mtodo, from, to);
                mAdapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                TodoOpenHelper todoopenhelper = TodoOpenHelper.getTodoOpenHelperInstance(CompletedTask.this);
                SQLiteDatabase database = todoopenhelper.getWritableDatabase();
                database.delete(TodoOpenHelper.TODO_TABLE_NAME, TodoOpenHelper.TODO_ID + "=" + mtodo.get(position).id, null);
                updatetodolist();

            }
        });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        updatetodolist();

    }

    public void updatetodolist() {
        int size = mtodo.size();
        TodoOpenHelper todoopenhelper = TodoOpenHelper.getTodoOpenHelperInstance(this);
        mtodo.clear();
        SQLiteDatabase database = todoopenhelper.getReadableDatabase();
        Cursor cursor;
        cursor = database.query(TodoOpenHelper.TODO_TABLE_NAME, null, TodoOpenHelper.TODO_ISCHECKED + "='true' ", null, null, null, null);
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TITLE));
            int id = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID));
            String description = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
            int category = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY));
            int priority = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_PRIORITY));
            String time = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TIME));
            Log.i("Alldata", date + category);
            Todo e = new Todo(id, title, description, priority, category, date, time);
            mtodo.add(e);
        }
        cursor.close();
        mAdapter.notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(size);
    }
}