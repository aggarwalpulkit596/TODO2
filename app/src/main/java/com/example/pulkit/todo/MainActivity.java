package com.example.pulkit.todo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.pulkit.todo.databinding.ActivityMainBinding;
import com.github.yaa110.db.RestorableSQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ActivityMainBinding binding;
    ArrayList<Todo> TodoList;
    TodoListAdapter todoListAdapter;
    final static int NEW_TODO = 1;
    final static int UPDATE_TODO = 2;
    SearchView searchView;
    int pos = 0;
    boolean visible = true;
    boolean flag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        long date = System.currentTimeMillis();
//        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy ");
//        String dateString = sdf.format(date);
//        actionBar.setTitle(dateString);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TodoList = new ArrayList<>();
        todoListAdapter = new TodoListAdapter(this, TodoList);
        binding.todoListView.setAdapter(todoListAdapter);
            binding.todoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                    if(flag) {
                    flag = !flag;
                    if (visible) {
                        view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                        visible = !visible;
                        binding.fab.setVisibility(View.INVISIBLE);
                        binding.tasklayout.setVisibility(View.VISIBLE);
                        binding.taskeditbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                flag = !flag;
                                view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                                visible = !visible;
                                binding.fab.setVisibility(View.VISIBLE);
                                binding.tasklayout.setVisibility(View.INVISIBLE);
                                Intent i = new Intent(MainActivity.this, TodoDetailActivity.class);
                                i.putExtra(IntentConstants.TODO_ID, TodoList.get(position).id);
                                startActivityForResult(i, UPDATE_TODO);
                            }
                        });
                        binding.taskdonebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                flag = !flag;
                                view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                                visible = !visible;
                                binding.fab.setVisibility(View.VISIBLE);
                                binding.tasklayout.setVisibility(View.INVISIBLE);
                                TodoOpenHelper todoopenhelper = TodoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
                                SQLiteDatabase database = todoopenhelper.getReadableDatabase();
                                ContentValues cv = new ContentValues();
                                cv.put(TodoOpenHelper.TODO_ISCHECKED, "true");
                                database.update(TodoOpenHelper.TODO_TABLE_NAME, cv, TodoOpenHelper.TODO_ID + "=" + TodoList.get(position).id, null);
                                alarmCancel(TodoList.get(position).id);
                                Toast.makeText(MainActivity.this, "Task Finished", Toast.LENGTH_SHORT).show();
                                updatetodolist();

                            }
                        });
                        binding.taskdelallbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                flag = !flag;
                                visible = !visible;
                                binding.fab.setVisibility(View.VISIBLE);
                                binding.tasklayout.setVisibility(View.INVISIBLE);
                                TodoList.clear();
                                todoListAdapter.notifyDataSetChanged();
                                Snackbar.make(view, "Deleted Accidentally ??", BaseTransientBottomBar.LENGTH_LONG)
                                        .setAction("Undo", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                updatetodolist();

                                            }
                                        })
                                        .addCallback(new Snackbar.Callback() {
                                            @Override
                                            public void onDismissed(Snackbar snackbar, int event) {
                                                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                                    TodoOpenHelper todoopenhelper = TodoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
                                                    SQLiteDatabase database = todoopenhelper.getWritableDatabase();
                                                    Cursor cursor;
                                                    cursor = database.query(TodoOpenHelper.TODO_TABLE_NAME, null, TodoOpenHelper.TODO_ISCHECKED + "='false' ", null, null, null, null);
                                                    while (cursor.moveToNext()) {
                                                        int id = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID));
                                                        alarmCancel(id);
                                                        database.delete(TodoOpenHelper.TODO_TABLE_NAME, null, null);
                                                    }
                                                }
                                            }
                                        })
                                        .show();
                            }
                        });
                        binding.taskdelbtn.setOnClickListener(new View.OnClickListener()

                        {
                            @Override
                            public void onClick(View v) {
                                flag = !flag;
                                HashMap<String, String> tableRowid = new HashMap<>();
                                TodoOpenHelper todoopenhelper = TodoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
                                tableRowid.put(TodoOpenHelper.TODO_TABLE_NAME, TodoOpenHelper.TODO_ID + "=" + TodoList.get(position).id);
                                final RestorableSQLiteDatabase database = RestorableSQLiteDatabase.getInstance(todoopenhelper, tableRowid);
                                database.delete(TodoOpenHelper.TODO_TABLE_NAME, TodoOpenHelper.TODO_ID + "=" + TodoList.get(position).id, null, "DELETION TAG");
                                updatetodolist();
                                Snackbar.make(view, "Deleted Accidentally ??", BaseTransientBottomBar.LENGTH_LONG)
                                        .setAction("Undo", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                database.restore("DELETION TAG");
                                                updatetodolist();

                                            }
                                        })
                                        .addCallback(new Snackbar.Callback() {
                                            @Override
                                            public void onDismissed(Snackbar snackbar, int event) {
                                                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                                    alarmCancel(TodoList.get(position).id);
                                                }
                                            }
                                        })
                                        .show();
                                view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                                visible = !visible;
                                binding.fab.setVisibility(View.VISIBLE);
                                binding.tasklayout.setVisibility(View.INVISIBLE);


                            }
                        });
                    } else if (!visible) {
                        view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.Grey));
                        visible = !visible;
                        binding.fab.setVisibility(View.VISIBLE);
                        binding.tasklayout.setVisibility(View.INVISIBLE);
                    }
                }
            }

        });
        if(flag) {
            binding.todoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()

            {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view,
                                               final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Confirm");
                    builder.setCancelable(false);
                    builder.setMessage("Are you sure you want to delete ??");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HashMap<String, String> tableRowid = new HashMap<>();
                            TodoOpenHelper todoopenhelper = TodoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
                            tableRowid.put(TodoOpenHelper.TODO_TABLE_NAME, TodoOpenHelper.TODO_ID + "=" + TodoList.get(position).id);
                            final RestorableSQLiteDatabase database = RestorableSQLiteDatabase.getInstance(todoopenhelper, tableRowid);
                            database.delete(TodoOpenHelper.TODO_TABLE_NAME, TodoOpenHelper.TODO_ID + "=" + TodoList.get(position).id, null, "DELETION TAG");
                            updatetodolist();
                            Snackbar.make(view, "Deleted Accidentally ??", BaseTransientBottomBar.LENGTH_LONG)
                                    .setAction("Undo", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            database.restore("DELETION TAG");
                                            updatetodolist();

                                        }
                                    })
                                    .addCallback(new Snackbar.Callback() {
                                        @Override
                                        public void onDismissed(Snackbar snackbar, int event) {
                                            if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                                alarmCancel(TodoList.get(position).id);
                                            }
                                        }
                                    })
                                    .show();
                            view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                            visible = !visible;
                            binding.fab.setVisibility(View.VISIBLE);
                            binding.tasklayout.setVisibility(View.INVISIBLE);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });
        }

        updatetodolist();
        binding.fab.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, TodoDetailActivity.class);
                startActivityForResult(i, NEW_TODO);
            }
        });
        binding.spinnerNav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {
                updatetodolist();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        updatetodolist();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_TODO || requestCode == UPDATE_TODO) {
            if (resultCode == RESULT_OK) {
                updatetodolist();
            } else if (resultCode == RESULT_CANCELED) {

                Toast.makeText(this, "Action Interrupted", Toast.LENGTH_SHORT).show();

            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.tasksfinished) {
            Intent intent = new Intent(this, CompletedTask.class);
            startActivityForResult(intent, 10);
        }

        return true;
    }

    public void updatetodolist() {

        pos = binding.spinnerNav.getSelectedItemPosition();
        TodoOpenHelper todoopenhelper = TodoOpenHelper.getTodoOpenHelperInstance(this);
        TodoList.clear();
        SQLiteDatabase database = todoopenhelper.getReadableDatabase();
        Cursor cursor;
        if (pos == 0) {
            cursor = database.query(TodoOpenHelper.TODO_TABLE_NAME, null, TodoOpenHelper.TODO_ISCHECKED + "='false' ", null, null, null, null);
        } else {
            cursor = database.query(TodoOpenHelper.TODO_TABLE_NAME, null, TodoOpenHelper.TODO_ISCHECKED + "='false' AND " + TodoOpenHelper.TODO_CATEGORY + "=" + (pos - 1), null, null, null, null);
        }
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
            TodoList.add(e);
        }
        cursor.close();

        todoListAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        todoListAdapter.getFilter().filter(newText);
        Log.i("----", newText);
        return true;
    }

    public void alarmCancel(int id) {
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, i, 0);
        am.cancel(pendingIntent);
    }

}
