package com.example.csdbot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.csdbot.AlarmReceiver;
import com.example.csdbot.components.DatabaseHelper;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AddReminderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText selectName, selectDescription;
    private TextView selectDate, selectPriority, selectTime;
    private CalendarView calendar;
    private TimePicker timePicker;
    private ImageView checkBtn;
    private RadioGroup priorityGroup;
    private RadioButton selectedButton;
    private int reminderDay, reminderMonth, reminderYear, reminderHour, reminderMin;
    private FirebaseAuth firebaseAuth;
    private User profileUser;
    private DatabaseHelper myDB;
    private DatabaseReference UserdbReference;

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Reminder");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        /* Find the activity's views
         *      selectName: The name of the reminder
         *      selectDescription: The description of the reminder
         *      selectDate: The TextView for the Date of the reminder
         *      selectTime: The TextView for the Time of the reminder
         *      checkBtn: The Check Button under the TimePicker
         *      selectPriority: The TextView for the Priority of the reminder
         *      calendar: The CalendarView for the selection of the reminder's date
         *      timePicker: The TimePicker for the selection of the reminder's time
         *      priorityGroup: The RadioGroup for the selection of the reminder's priority
         *      addRem: The Button to add the reminder
         *      timeNumberPicker: The layout containing the number pickers for time
         *      hourNumberPicker: The Reminder's Hour NumberPicker
         *      minuteNumberPicker: The Reminder's Minute NumberPicker
         */
        selectName = findViewById(R.id.reminderName);
        selectDescription = findViewById(R.id.reminderDescription);
        selectDate = findViewById(R.id.date);
        selectTime = findViewById(R.id.selectTime);
        checkBtn = findViewById(R.id.checkButton);
        selectPriority = findViewById(R.id.priority);
        calendar = findViewById(R.id.calendarViewAddReminder);
        timePicker = findViewById(R.id.timePicker);
        priorityGroup = findViewById(R.id.priorityGroup);
        Button addRem = findViewById(R.id.addNewRem);
        final LinearLayout timeNumberPicker = findViewById(R.id.addReminderNumberPicker);
        final NumberPicker hourNumberPicker = findViewById(R.id.addReminderHourPicker);
        final NumberPicker minuteNumberPicker = findViewById(R.id.addReminderMinutesPicker);

        // Set minimum and maximum values for the number pickers
        hourNumberPicker.setMaxValue(23);
        hourNumberPicker.setMinValue(0);
        minuteNumberPicker.setMaxValue(59);
        minuteNumberPicker.setMinValue(0);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Set the visibility of the (temporarily) not-needed components
        calendar.setVisibility(View.GONE);
        priorityGroup.setVisibility(View.GONE);
        timePicker.setVisibility(View.GONE);
        checkBtn.setVisibility(View.GONE);

        // Connect to the database and get the reminders list of the user
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if ( myDB != null ){
                    profileUser = myDB.getUserByUID(firebaseAuth.getUid());
                }
                UserdbReference = databaseReference.child("userList").child(String.valueOf(myDB.getUserPosition(profileUser)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddReminderActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        // Hide keyboard if the user taps out of it
        findViewById(R.id.linearLayoutAddReminder).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                return true;
            }
        });

        // Get the date when the user clicks on a date
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                reminderDay = dayOfMonth;
                reminderMonth = month + 1;
                reminderYear = year;
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                selectDate.setText(date);
                calendar.setVisibility(View.GONE);
            }
        });

        // Show the Calendar if the user clicks on the date field
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setVisibility(VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.setVisibility(GONE);
                } else {
                    timeNumberPicker.setVisibility(GONE);
                }
                checkBtn.setVisibility(View.GONE);
                priorityGroup.setVisibility(View.GONE);
            }
        });

        // Show the TimePicker if the user clicks on the time field
        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.setVisibility(VISIBLE);
                } else {
                    timeNumberPicker.setVisibility(VISIBLE);
                }
                checkBtn.setVisibility(VISIBLE);
                priorityGroup.setVisibility(View.GONE);
            }
        });

        // Hide the TimePicker
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    reminderHour = timePicker.getHour();
                    reminderMin = timePicker.getMinute();
                } else {
                    reminderHour = hourNumberPicker.getValue();
                    reminderMin = minuteNumberPicker.getValue();
                }
                if (reminderHour >= 10 ){
                    String hour = reminderHour + ":";
                    selectTime.setText( hour );
                } else {
                    String hour = "0" + reminderHour + ":";
                    selectTime.setText( hour );
                }
                if (reminderMin >= 10 ){
                    String time = selectTime.getText().toString()  + reminderMin;
                    selectTime.setText(time);
                } else {
                    String time = selectTime.getText().toString() + "0" + reminderMin;
                    selectTime.setText(time);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.setVisibility(GONE);
                } else {
                    timeNumberPicker.setVisibility(GONE);
                }
                checkBtn.setVisibility(View.GONE);
            }
        });

        // Get the checked radio button
        priorityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedButton = findViewById(checkedId);
                selectPriority.setText(selectedButton.getText().toString());
                priorityGroup.setVisibility(View.GONE);
            }
        });

        // Show The Priority RadioGroup when the user clicks on the priority field
        selectPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.setVisibility(GONE);
                } else {
                    timeNumberPicker.setVisibility(GONE);
                }
                checkBtn.setVisibility(View.GONE);
                priorityGroup.setVisibility(VISIBLE);
            }
        });

        // Add reminder to user's reminder list and update database
        addRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reminder.priority priorityRem;
                switch (selectPriority.getText().toString()) {
                    case "High":
                        priorityRem = Reminder.priority.high;
                        break;
                    case "Medium":
                        priorityRem = Reminder.priority.mid;
                        break;
                    case "Low":
                        priorityRem = Reminder.priority.low;
                        break;
                    default:
                        priorityRem = Reminder.priority.low;
                        break;
                }

                // Initialize reminder
                Reminder tempRem = new Reminder(selectName.getText().toString(), reminderDay, reminderMonth, reminderYear, priorityRem);
                tempRem.setHour(reminderHour);
                tempRem.setMin(reminderMin);
                tempRem.setDescription(selectDescription.getText().toString());
                if( profileUser.remindersListIsEmpty() ){
                    profileUser.setUser_reminders(new ArrayList<Reminder>());
                    profileUser.addUser_reminders(tempRem);
                } else {
                    profileUser.addUser_reminders(tempRem);
                    Collections.sort(profileUser.getUser_reminders(), Reminder.ReminderComparator);
                }

                // Set Reminder and update the database
                setAlarm(tempRem);
                UserdbReference.setValue(profileUser);

                //Start activity of newly created reminder and finish current one
                Intent intent = new Intent(AddReminderActivity.this, RemindersListActivity.class);
                startActivity(intent);
                finish();
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
        Intent reminderIntent = new Intent(AddReminderActivity.this, AlarmReceiver.class);
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
                AddReminderActivity.this, reminder.getId(), reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT
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
                Intent intent_home = new Intent(AddReminderActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(AddReminderActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(AddReminderActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(AddReminderActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(AddReminderActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(AddReminderActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}
