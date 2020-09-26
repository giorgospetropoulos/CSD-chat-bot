package com.example.csdbot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.csdbot.AlarmReceiver;
import com.example.csdbot.components.Course;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.components.PostGraduateCourse;
import com.example.csdbot.R;
import com.example.csdbot.components.Reminder;
import com.example.csdbot.components.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.Collections;

public class CourseReminderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView reminderName, reminderTime;
    private Button editReminder, addReminder;
    private Dialog editDialog, addDialog;
    private DatabaseHelper myDB;
    private FirebaseAuth firebaseAuth;
    private Course course;
    private PostGraduateCourse post_course;
    private User profileUser;

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_reminder);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("Reminder Name"));
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        /* Find the activity's views
         *      reminderName: The reminder's name
         *      reminderDesc: The reminder's description
         *      reminderTime: The reminder's time
         *      reminderPriority: The reminder's priority
         *      editReminder: Edit Reminder Button
         *      addReminder: Add Reminder to User's Reminders Button
         */
        reminderName = findViewById(R.id.courseReminderPageName);
        TextView reminderDesc = findViewById(R.id.courseReminderPageDesc);
        reminderTime = findViewById(R.id.courseReminderPageTime);
        TextView reminderPriority = findViewById(R.id.courseReminderPagePriority);
        editReminder = findViewById(R.id.editCourseReminderBtn);
        addReminder = findViewById(R.id.addCourseReminderBtn);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Set the data of the reminder
        reminderName.setText(getIntent().getStringExtra("Reminder Name"));
        reminderDesc.setText(getIntent().getStringExtra("Reminder Desc"));
        String prio = getIntent().getStringExtra("Reminder Priority");
        switch ( prio ) {
            case "low":
                String low = " Low";
                reminderPriority.setText(low);
                reminderPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_priority,0, 0, 0);
                break;
            case "mid":
                String mid = " Medium";
                reminderPriority.setText(mid);
                reminderPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.medium_priority,0, 0, 0);
                break;
            case "high":
                String high = " High";
                reminderPriority.setText(high);
                reminderPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.high_priority,0, 0, 0);
                break;
            default:
                String def = " Low";
                reminderPriority.setText(def);
                reminderPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_priority,0, 0, 0);
                break;
        }

        // Connect to the database and retrieve the reminder's data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if (myDB != null) {
                    profileUser = myDB.getUserByUID(firebaseAuth.getUid());
                }
                Reminder tempRem;
                if ( profileUser.hasReminderByID(getIntent().getIntExtra("remID", 0))){
                    addReminder.setVisibility(View.GONE);
                }
                if ( getIntent().getStringExtra("Post").equals("true") ){
                    post_course = myDB.getPostGraduateCourseByName( getIntent().getStringExtra("Course Name"));
                    tempRem = post_course.getCourseReminder(post_course.getCourseReminderPosition(reminderName.getText().toString()));
                    String remTime = tempRem.getDay() + "/" +
                            tempRem.getMonth() + "/" +
                            tempRem.getYear() + " - " +
                            tempRem.getHour() +":";
                    reminderTime.setText(remTime);
                    if ( tempRem.getMin() < 10 ){
                        String remTime2 = reminderTime.getText().toString() + "0" + tempRem.getMin();
                        reminderTime.setText(remTime2);
                    } else {
                        String remTime2 = reminderTime.getText().toString() + tempRem.getMin();
                        reminderTime.setText(remTime2);
                    }

                } else {
                    course = myDB.getCourseByName( getIntent().getStringExtra("Course Name"));
                    tempRem = course.getCourseReminder(course.getCourseReminderPosition(reminderName.getText().toString()));
                    String remTime =tempRem.getDay() + "/" +
                            tempRem.getMonth() + "/" +
                            tempRem.getYear() + " - ";
                    reminderTime.setText(remTime);
                    if ( tempRem.getHour() < 10 ){
                        String remTime2 = reminderTime.getText().toString() + "0" + tempRem.getHour() + ":";
                        reminderTime.setText(remTime2);
                    } else {
                        String remTime2 = reminderTime.getText().toString() + tempRem.getHour() + ":";
                        reminderTime.setText(remTime2);
                    }
                    if ( tempRem.getMin() < 10 ){
                        String remTime2 = reminderTime.getText().toString() + "0" + tempRem.getMin();
                        reminderTime.setText(remTime2);
                    } else {
                        String remTime2 = reminderTime.getText().toString() + tempRem.getMin();
                        reminderTime.setText(remTime2);
                    }
                }
                if ( getIntent().getStringExtra("Post").equals("true") ){
                    if ( firebaseAuth.getUid() == post_course.getTeacherUID() ||
                            profileUser.isAdmin() ||
                            profileUser.getName().equals("admin") ){
                        editReminder.setVisibility(View.VISIBLE);
                    }
                } else {
                    if ( firebaseAuth.getUid() == course.getTeacherUID() ||
                            profileUser.isAdmin() ||
                            profileUser.getName().equals("admin") ){
                        editReminder.setVisibility(View.VISIBLE);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CourseReminderActivity.this , databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        // Edit reminder
        editReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView editPopUpTitle;
                Button edit, cancel;
                editDialog = new Dialog(CourseReminderActivity.this);
                editDialog.setContentView(R.layout.edit_reminder_confirmation);

                editPopUpTitle = editDialog.findViewById(R.id.popUpTitle);
                String title = "Are you sure you wish to edit reminder \""
                        + getIntent().getStringExtra("Reminder Name") + "\"?";
                editPopUpTitle.setText(title);
                edit = editDialog.findViewById(R.id.edit_confirmation_btn);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CourseReminderActivity.this, EditCourseReminderActivity.class);
                        if ( getIntent().getStringExtra("Post").equals("true") ){
                            intent.putExtra("Post","true");
                            intent.putExtra("courseName", getIntent().getStringExtra("Course Name"));
                            intent.putExtra("remPosition", post_course.getCourseReminderPosition(reminderName.getText().toString()));
                        } else {
                            intent.putExtra("Post","false");
                            intent.putExtra("courseName", getIntent().getStringExtra("Course Name"));
                            intent.putExtra("remPosition", course.getCourseReminderPosition(reminderName.getText().toString()));
                        }
                        startActivity(intent);
                        editDialog.dismiss();
                    }
                });
                cancel =  editDialog.findViewById(R.id.cancel_edit);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editDialog.dismiss();
                    }
                });
                editDialog.show();
            }
        });

        // Add reminder to user's reminder list
        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView addPopUpTitle;
                Button add, cancel;
                addDialog = new Dialog(CourseReminderActivity.this);
                addDialog.setContentView(R.layout.add_course_reminder_confirmation);
                addPopUpTitle = addDialog.findViewById(R.id.popUpAddTitle);
                String title = "Are you sure you wish to add reminder \"" +
                        reminderName.getText().toString() +
                        "\" to your reminders?";
                addPopUpTitle.setText(title);
                add = addDialog.findViewById(R.id.add_confirmation_btn);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileUser = myDB.getUserByUID(firebaseAuth.getUid());
                        //profileUser.addUser_reminders(new Reminder(course.getCourseReminder(position).getName(),course.getCourseReminder(position).getDay(),course.getCourseReminder(position).getMonth(),course.getCourseReminder(position).getYear(),course.getCourseReminder(position).getHour(),course.getCourseReminder(position).getMin(),course.getCourseReminder(position).getReminder_priority()));
                        if ( getIntent().getStringExtra("Post").equals("true") ){
                            profileUser.addUser_reminders(post_course.getCourseReminder(post_course.getCourseReminderPosition(reminderName.getText().toString())));
                            setAlarm(post_course.getCourseReminders().get(post_course.getCourseReminderPosition(reminderName.getText().toString())));
                            Collections.sort(profileUser.getUser_reminders(), Reminder.ReminderComparator);
                        } else {
                            profileUser.addUser_reminders(course.getCourseReminder(course.getCourseReminderPosition(reminderName.getText().toString())));
                            setAlarm(course.getCourseReminders().get(course.getCourseReminderPosition(reminderName.getText().toString())));
                            Collections.sort(profileUser.getUser_reminders(), Reminder.ReminderComparator);
                        }
                        databaseReference.setValue(myDB);
                        addDialog.dismiss();
                    }
                });
                cancel = addDialog.findViewById(R.id.cancel_add);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addDialog.dismiss();
                    }
                });
                addDialog.show();
            }
        });
    }

    /**
     * Create event for reminder and use pending intent and a broadcoast
     * receiver to set the notification to the given time.
     *
     * @param reminder the reminder to be set
     */
    public void setAlarm(Reminder reminder){
        // Intent
        Intent reminderIntent = new Intent(CourseReminderActivity.this, AlarmReceiver.class);
        reminderIntent.putExtra("notificationId", reminder.getId());
        reminderIntent.putExtra("name", reminder.getName());
        reminderIntent.putExtra("message", reminder.getDescription());
        switch (reminder.getReminder_priority()){
            case low:
                reminderIntent.putExtra("priority", "low");
                break;
            case mid:
                reminderIntent.putExtra("priority", "mid");
                break;
            case high:
                reminderIntent.putExtra("priority", "high");
                break;
            default:
                reminderIntent.putExtra("priority", "none");
                break;
        }
        reminderIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        // PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                CourseReminderActivity.this, reminder.getId(), reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        // AlarmManager
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, reminder.getHour());
        c.set(Calendar.MINUTE, reminder.getMin());
        c.set(Calendar.SECOND,0);
        c.set(Calendar.YEAR, reminder.getYear());
        c.set(Calendar.MONTH, reminder.getMonth()-1);
        c.set(Calendar.DATE, reminder.getDay());

        // Set Alarm
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    c.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
    }

    // ---------- Slide Menu -------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        if ( drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                Intent intent_home = new Intent(CourseReminderActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(CourseReminderActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(CourseReminderActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(CourseReminderActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(CourseReminderActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(CourseReminderActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}