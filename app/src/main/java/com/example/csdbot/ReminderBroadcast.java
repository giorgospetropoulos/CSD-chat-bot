package com.example.csdbot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.csdbot.components.Course;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.components.Reminder;
import com.example.csdbot.components.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ReminderBroadcast extends BroadcastReceiver {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, userCourseListReference, courseReference;
    private User user;
    private DatabaseHelper myDB;
    private Reminder rem;
    private Course courseToAdd;



    @Override
    public void onReceive(final Context context, final Intent intent) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("Database");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);

                user = myDB.getUserByUID(firebaseAuth.getUid());
                rem = user.getReminderByID( Integer.parseInt(intent.getStringExtra("remID")) );

                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, rem.getHour());
                c.set(Calendar.MINUTE, rem.getMin());
                c.set(Calendar.SECOND,0);
                c.set(Calendar.YEAR, rem.getYear());
                c.set(Calendar.MONTH, rem.getMonth() - 1);
                c.set(Calendar.DATE, rem.getDay());

                NotificationCompat.Builder builder = new NotificationCompat.Builder( context, "notificationChannel")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(rem.getName())
                        .setContentText(rem.getName())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setWhen(c.getTimeInMillis());

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                notificationManager.notify(rem.getId(), builder.build());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
