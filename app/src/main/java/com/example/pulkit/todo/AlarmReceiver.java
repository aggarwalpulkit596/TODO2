package com.example.pulkit.todo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import android.support.v4.app.NotificationCompat;

import android.widget.Toast;


import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction()!=null && intent.getAction().equals("FINISH")){
            int id=intent.getIntExtra(IntentConstants.TODO_ID,-1);
            TodoOpenHelper todoopenhelper = TodoOpenHelper.getTodoOpenHelperInstance(context);
            SQLiteDatabase database = todoopenhelper.getWritableDatabase();
            ContentValues cv=new ContentValues();
            cv.put(TodoOpenHelper.TODO_ISCHECKED,"true");
            database.update(TodoOpenHelper.TODO_TABLE_NAME,cv, TodoOpenHelper.TODO_ID + "=" + id, null);
            Toast.makeText(context,"Tasks Finished",Toast.LENGTH_SHORT).show();
            NotificationManager nManager =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            nManager.cancel(id);
            return;

        }
        // TODO: This method is called when the BroadcastReceiver is receiving
        String title=intent.getStringExtra("title");
        String time=intent.getStringExtra("time");
        int id=intent.getIntExtra(IntentConstants.TODO_ID,-1);
        Intent in = new Intent(context, AlarmReceiver.class);
        in.putExtra(IntentConstants.TODO_ID,id);
        in.setAction("FINISH");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id, in, 0);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(context)
                .setContentTitle("Task at "+time)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.accomplish_icon)
                .setTicker("Notification")
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(title)
                .addAction(0,"FINISH",pendingIntent);


        Intent resultIntent =new Intent(context,TodoDetailActivity.class);
        resultIntent.putExtra("id",id);
        PendingIntent resultPendingIntent=PendingIntent.getActivity(context,2,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager=(NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(id,mBuilder.build());
    }


}
