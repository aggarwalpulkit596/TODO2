package com.example.pulkit.todo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.pulkit.todo.databinding.ActivityTodoDetailBinding;

import java.util.Calendar;
import java.util.Date;

public class TodoDetailActivity extends AppCompatActivity {

    ActivityTodoDetailBinding binding;
    String title, description, date1, time1;
    int category, priority;
    String newTitle, newDescription, newDate, newTime;
    int newPriority, newCategory;
    int uid;
    long date, time;
    int month, year, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo_detail);
        Intent i = getIntent();
        uid = i.getIntExtra(IntentConstants.TODO_ID, -1);
        if (uid != -1) {
            populate();
        }
        binding.todoDetailDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalendar = Calendar.getInstance();
                month = newCalendar.get(Calendar.MONTH);  // Current month
                year = newCalendar.get(Calendar.YEAR);   // Current year
                day = newCalendar.get(Calendar.DATE);
                showDatePicker(TodoDetailActivity.this, year, month, day);
            }
        });
        binding.todoDetailTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(month == 0){
                    return;
                }
                Calendar mcurrentTime = Calendar.getInstance();
                hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                minute = mcurrentTime.get(Calendar.MINUTE);
                showTimePicker(TodoDetailActivity.this, hour, minute);
            }

        });

        binding.todoDetailPrioritySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //AUTO GENERATED METHOD
            }
        });
        binding.todoDetailCategorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            newTitle = binding.todoDetailTitleTextView.getText().toString();
            if (newTitle.trim().isEmpty()) {
                binding.todoDetailTitleTextView.setError("Can't Be Empty");
                return false;
            }
            newDescription = binding.todoDetailDescriptionTextView.getText().toString();
            newDate = binding.todoDetailDateEditText.getText().toString();
            newTime = binding.todoDetailTimeEditText.getText().toString();
            if (!newDate.trim().isEmpty() && newTime.trim().isEmpty()) {
                binding.todoDetailTimeEditText.setError("Can't Be Empty");
                return false;
            }
            newCategory = binding.todoDetailCategorySpinner.getSelectedItemPosition();
            newPriority = binding.todoDetailPrioritySpinner.getSelectedItemPosition();
            TodoOpenHelper todoOpenHelper = TodoOpenHelper.getTodoOpenHelperInstance(TodoDetailActivity.this);
            SQLiteDatabase database = todoOpenHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(TodoOpenHelper.TODO_TITLE, newTitle);
            cv.put(TodoOpenHelper.TODO_DESCRIPTION, newDescription);
            cv.put(TodoOpenHelper.TODO_PRIORITY, newPriority);
            cv.put(TodoOpenHelper.TODO_CATEGORY, newCategory);
            cv.put(TodoOpenHelper.TODO_DATE, newDate);
            cv.put(TodoOpenHelper.TODO_TIME, newTime);
            cv.put(TodoOpenHelper.TODO_ISCHECKED, "false");
            if (uid == -1)
                database.insert(TodoOpenHelper.TODO_TABLE_NAME, null, cv);
            else {
                database.update(TodoOpenHelper.TODO_TABLE_NAME, cv, TodoOpenHelper.TODO_ID + "=" + uid, null);
            }
            Calendar current = Calendar.getInstance();

            Calendar cal = Calendar.getInstance();
            cal.set(year,
                    month,
                    day,
                    hour,
                    minute,
                    0);
//            if (cal.compareTo(current) <= 0) {
//                //The set Date/Time already passed
//                Toast.makeText(getApplicationContext(),
//                        "Invalid Date/Time",
//                        Toast.LENGTH_LONG).show();
//                return false;}
                setAlarm(cal);
            setResult(RESULT_OK);
            finish();
        } else if (id == R.id.del) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TodoDetailActivity.this);

            builder.setTitle("Confirm");
            builder.setCancelable(false);
            builder.setMessage("Are you sure you want to delete ??");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TodoOpenHelper todoopenhelper = TodoOpenHelper.getTodoOpenHelperInstance(TodoDetailActivity.this);
                    SQLiteDatabase database = todoopenhelper.getWritableDatabase();
                    AlarmManager am = (AlarmManager) TodoDetailActivity.this.getSystemService(Context.ALARM_SERVICE);
                    Intent i = new Intent(TodoDetailActivity.this, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(TodoDetailActivity.this, uid, i, 0);
                    am.cancel(pendingIntent);
                    database.delete(TodoOpenHelper.TODO_TABLE_NAME, TodoOpenHelper.TODO_ID + "=" + uid, null);
                    setResult(RESULT_OK);
                    finish();
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
        return true;
    }

    public void showTimePicker(Context context, int hour, int minute) {

        TimePickerDialog mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Calendar calendar = Calendar.getInstance();
                Log.i("TAG", "Hour " + selectedHour + " min " + selectedMinute);
                calendar.set(year, month, day,selectedHour,selectedMinute);
//                calendar.set(Calendar.HOUR, selectedHour);
//                calendar.set(Calendar.MINUTE, selectedMinute);
                time = calendar.getTime().getTime();
                binding.todoDetailTimeEditText.setText("" + selectedHour + ":" + selectedMinute);
                Date date = new Date(time);
                TodoDetailActivity.this.minute = selectedMinute;
                TodoDetailActivity.this.hour = selectedHour;
                Log.i("TAG", "Date of Alarm " + date);

            }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void showDatePicker(Context context, int initialYear, int initialMonth, int initialDay) {

        // Creating datePicker dialog object
        // It requires context and listener that is used when a date is selected by the user.

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    //This method is called when the user has finished selecting a date.
                    // Arguments passed are selected year, month and day
                    @Override
                    public void onDateSet(DatePicker datepicker, int year, int month, int day) {

                        // To get epoch, You can store this date(in epoch) in database
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        date = calendar.getTime().getTime();

                        // Setting date selected in the edit text
                        binding.todoDetailDateEditText.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, initialYear, initialMonth, initialDay);

        //Call show() to simply show the dialog
        datePickerDialog.show();

    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TodoDetailActivity.this);

        builder.setTitle("Confirm");
        builder.setCancelable(false);
        builder.setMessage("Are you sure you want to discard the changes ??");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(RESULT_CANCELED);
                TodoDetailActivity.super.onBackPressed();
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
    }


    public void populate() {
        TodoOpenHelper todoOpenHelper = TodoOpenHelper.getTodoOpenHelperInstance(TodoDetailActivity.this);
        SQLiteDatabase database = todoOpenHelper.getWritableDatabase();
        Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE_NAME, null, TodoOpenHelper.TODO_ID + "=" + uid, null, null, null, null);
        cursor.moveToNext();
        title = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TITLE));
        description = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_DESCRIPTION));
        category = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY));
        date1 = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
        time1 = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TIME));
        priority = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_PRIORITY));

        binding.todoDetailTitleTextView.setText(title);
        binding.todoDetailDescriptionTextView.setText(description);
        binding.todoDetailCategorySpinner.setSelection(category);
        binding.todoDetailPrioritySpinner.setSelection(priority);
        binding.todoDetailDateEditText.setText(date1);
        binding.todoDetailTimeEditText.setText(time1);
    }

    private void setAlarm(Calendar targetCal) {
        AlarmManager alarmManager = (AlarmManager) TodoDetailActivity.this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(TodoDetailActivity.this, AlarmReceiver.class);
        intent.putExtra(IntentConstants.TODO_ID, uid);
        intent.putExtra("title", newTitle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), uid, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC, targetCal.getTimeInMillis(), pendingIntent);
    }

}
