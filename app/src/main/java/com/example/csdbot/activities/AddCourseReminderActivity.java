package com.example.csdbot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import com.example.csdbot.components.Course;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.R;
import com.example.csdbot.components.Reminder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Collections;
import static android.view.View.GONE;

public class AddCourseReminderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText selectName, selectDescription;
    private TextView selectDate, selectPriority, selectTime;
    private CalendarView calendar;
    private TimePicker timePicker;
    private ImageView checkBtn;
    private RadioGroup priorityGroup;
    private RadioButton selectedButton;
    private Button addRem;
    private int reminderDay, reminderMonth, reminderYear, reminderHour, reminderMin;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private Course course;
    private DatabaseHelper myDB;

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course_reminder);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Course Reminder");
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
         */
        selectName = (EditText) findViewById(R.id.courseReminderName);
        selectDescription = (EditText) findViewById(R.id.courseReminderDescription);
        selectDate = (TextView) findViewById(R.id.courseReminderDate);
        selectTime = (TextView) findViewById(R.id.selectCourseReminderTime);
        checkBtn = (ImageView) findViewById(R.id.courseReminderCheckButton);
        selectPriority = (TextView) findViewById(R.id.CourseReminderPriority);
        calendar = (CalendarView) findViewById(R.id.calendarViewAddCourseReminder);
        timePicker = (TimePicker) findViewById(R.id.courseReminderTimePicker);
        priorityGroup = (RadioGroup) findViewById(R.id.courseReminderPriorityGroup);
        addRem = (Button) findViewById(R.id.addNewCourseRem);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Set the visibility of the (temporarily) not-needed components
        calendar.setVisibility(GONE);
        priorityGroup.setVisibility(GONE);
        timePicker.setVisibility(GONE);
        checkBtn.setVisibility(GONE);

        // Connect to the database and get the course that the remindes is being set
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                course = myDB.getCourseByName(getIntent().getStringExtra("Course Name"));

                // Hide keyboard if the user taps out of it
                findViewById(R.id.linearLayoutAddCourseReminder).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
                        selectDate.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                        calendar.setVisibility(GONE);
                    }
                });

                // Show the Calendar if the user clicks on the date field
                selectDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        calendar.setVisibility(View.VISIBLE);
                        timePicker.setVisibility(GONE);
                        checkBtn.setVisibility(GONE);
                        priorityGroup.setVisibility(GONE);
                    }
                });

                // Show the TimePicker if the user clicks on the time field
                selectTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        calendar.setVisibility(GONE);
                        timePicker.setVisibility(View.VISIBLE);
                        checkBtn.setVisibility(View.VISIBLE);
                        priorityGroup.setVisibility(GONE);
                    }
                });

                // Hide the TimePicker
                checkBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reminderHour = timePicker.getHour();
                        reminderMin = timePicker.getMinute();
                        if (reminderHour >= 10 ){
                            selectTime.setText( String.valueOf(reminderHour) + ":" );
                        } else {
                            selectTime.setText( "0" + String.valueOf(reminderHour) + ":" );
                        }
                        if (reminderMin >= 10 ){
                            selectTime.setText( selectTime.getText().toString() + String.valueOf(reminderMin));
                        } else {
                            selectTime.setText( selectTime.getText().toString() + "0" + String.valueOf(reminderMin));
                        }
                        timePicker.setVisibility(GONE);
                        checkBtn.setVisibility(GONE);
                    }
                });

                // Get the checked radio button
                priorityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        selectedButton = (RadioButton) findViewById(checkedId);
                        selectPriority.setText(selectedButton.getText().toString());
                        priorityGroup.setVisibility(GONE);
                    }
                });

                // Show The Priority RadioGroup when the user clicks on the priority field
                selectPriority.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calendar.setVisibility(GONE);
                        timePicker.setVisibility(GONE);
                        checkBtn.setVisibility(GONE);
                        priorityGroup.setVisibility(View.VISIBLE);
                    }
                });

                // Add reminder to course and update database
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
                        tempRem.setDescription(selectDescription.getText().toString());
                        tempRem.setHour(reminderHour);
                        tempRem.setMin(reminderMin);

                        // Add Reminder to Course
                        course.setCourseReminder(tempRem);
                        Collections.sort(course.getCourseReminders(), Reminder.ReminderComparator);

                        //Update Database and finish the activity
                        databaseReference.setValue(myDB);
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




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
                Intent intent_home = new Intent(AddCourseReminderActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(AddCourseReminderActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(AddCourseReminderActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(AddCourseReminderActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(AddCourseReminderActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(AddCourseReminderActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}
