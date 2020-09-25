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
import android.widget.Toast;
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

public class EditCourseReminderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText selectName, selectDescription;
    private TextView selectDate, selectPriority, selectTime;
    private CalendarView calendar;
    private TimePicker timePicker;
    private ImageView checkBtn;
    private RadioGroup priorityGroup;
    private RadioButton selectedButton;
    private Button save;
    private int priorityDay, priorityMonth, priorityYear, priorityHour, priorityMin, reminderNo;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private User profileUser;
    private Reminder tempRem;
    private DatabaseHelper myDB;
    private Course course;
    private PostGraduateCourse post_course;

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course_reminder);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Course Reminder");
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
         *      save: The Button to add the reminder
         *      reminderNo: the position of the reminder inside the course's reminders list
         */
        selectName = (EditText) findViewById(R.id.editCourseReminderName);
        selectDescription = (EditText) findViewById(R.id.editCourseReminderDescription);
        selectDate = (TextView) findViewById(R.id.editCourseReminderDate);
        selectTime = (TextView) findViewById(R.id.editCourseReminderTime);
        timePicker = (TimePicker) findViewById(R.id.edit_courseReminder_timePicker);
        checkBtn = (ImageView) findViewById(R.id.edit_courseReminder_checkButton);
        selectPriority = (TextView) findViewById(R.id.edit_courseReminder_priority);
        calendar = (CalendarView) findViewById(R.id.calendarViewEditCourseReminder);
        priorityGroup = (RadioGroup) findViewById(R.id.edit__courseReminder_priorityGroup);
        save = (Button) findViewById(R.id.editCourseReminder__save);
        reminderNo = getIntent().getIntExtra("remPosition",0);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Set the visibility of the (temporarily) not-needed components
        calendar.setVisibility(View.GONE);
        priorityGroup.setVisibility(View.GONE);
        timePicker.setVisibility(View.GONE);
        checkBtn.setVisibility(View.GONE);

        // Connect to the database and retrieve the reminder's data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if (getIntent().getStringExtra("Post").equals("true")){
                    post_course = myDB.getPostGraduateCourseByName(getIntent().getStringExtra("courseName"));
                    tempRem = myDB.getPostGraduateCourseByName(getIntent().getStringExtra("courseName")).getCourseReminder(reminderNo);
                } else {
                    course = myDB.getCourseByName(getIntent().getStringExtra("courseName"));
                    tempRem = myDB.getCourseByName(getIntent().getStringExtra("courseName")).getCourseReminder(reminderNo);
                }

                selectName.setText(tempRem.getName());
                selectDescription.setText(tempRem.getDescription());
                selectDate.setText(tempRem.getDay() + "/" + tempRem.getMonth() + "/" + tempRem.getYear());
                if ( tempRem.getHour() < 10 ){
                    selectTime.setText("0" + tempRem.getHour() + ":");
                } else {
                    selectTime.setText( tempRem.getHour() + ":");
                }
                if ( tempRem.getMin() < 10 ){
                    selectTime.setText(selectTime.getText().toString() + "0" + tempRem.getMin());
                } else {
                    selectTime.setText(selectTime.getText().toString() + tempRem.getMin());
                }
                priorityDay = tempRem.getDay();
                priorityMonth = tempRem.getMonth();
                priorityYear = tempRem.getYear();
                priorityHour = tempRem.getHour();
                priorityMin = tempRem.getMin();
                switch (tempRem.getReminder_priority()) {
                    case low:
                        selectPriority.setText("Low");
                        break;
                    case mid:
                        selectPriority.setText("Medium");
                        break;
                    case high:
                        selectPriority.setText("High");
                        break;
                    default:
                        selectPriority.setText("Low");
                        break;
                }
                if (getIntent().getStringExtra("Post").equals("true")){
                    post_course = myDB.getPostGraduateCourseByName(getIntent().getStringExtra("courseName"));
                    tempRem = myDB.getPostGraduateCourseByName(getIntent().getStringExtra("courseName")).getCourseReminder(reminderNo);
                    post_course.getCourseReminders().remove(tempRem);
                } else {
                    course = myDB.getCourseByName(getIntent().getStringExtra("courseName"));
                    tempRem = myDB.getCourseByName(getIntent().getStringExtra("courseName")).getCourseReminder(reminderNo);
                    course.getCourseReminders().remove(tempRem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditCourseReminderActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        // Hide keyboard if the user taps out of it
        findViewById(R.id.course_reminders_linear_layout).setOnTouchListener(new View.OnTouchListener() {
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
                priorityDay = dayOfMonth;
                priorityMonth = month + 1;
                priorityYear = year;
                selectDate.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                calendar.setVisibility(View.GONE);
            }
        });

        // Show the Calendar if the user clicks on the date field
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setVisibility(View.VISIBLE);
                timePicker.setVisibility(View.GONE);
                checkBtn.setVisibility(View.GONE);
                priorityGroup.setVisibility(View.GONE);
            }
        });

        // Show the TimePicker if the user clicks on the time field
        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setVisibility(View.GONE);
                timePicker.setVisibility(View.VISIBLE);
                checkBtn.setVisibility(View.VISIBLE);
                priorityGroup.setVisibility(View.GONE);
            }
        });

        // Hide the TimePicker
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priorityHour = timePicker.getHour();
                priorityMin = timePicker.getMinute();
                if (priorityHour >= 10 ){
                    selectTime.setText( String.valueOf(priorityHour) + ":" );
                } else {
                    selectTime.setText( "0" + String.valueOf(priorityHour) + ":" );
                }
                if (priorityMin >= 10 ){
                    selectTime.setText( selectTime.getText().toString() + String.valueOf(priorityMin));
                } else {
                    selectTime.setText( selectTime.getText().toString() + "0" + String.valueOf(priorityMin));
                }
                timePicker.setVisibility(View.GONE);
                checkBtn.setVisibility(View.GONE);
            }
        });

        // Get the checked radio button
        priorityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedButton = (RadioButton) findViewById(checkedId);
                selectPriority.setText(selectedButton.getText().toString());
                priorityGroup.setVisibility(View.GONE);
            }
        });

        // Show The Priority RadioGroup when the user clicks on the priority field
        selectPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setVisibility(View.GONE);
                timePicker.setVisibility(View.GONE);
                checkBtn.setVisibility(View.GONE);
                priorityGroup.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        // Save updated reminder and update database
        save.setOnClickListener(new View.OnClickListener() {
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
                Reminder tempRem = new Reminder(selectName.getText().toString(), priorityDay, priorityMonth, priorityYear, priorityRem);
                tempRem.setMin(priorityMin);
                tempRem.setHour(priorityHour);
                tempRem.setDescription(selectDescription.getText().toString());
                if (getIntent().getStringExtra("Post").equals("true")){
                    post_course.getCourseReminders().add(tempRem);
                } else {
                    course.getCourseReminders().add(tempRem);
                }

                //Update Database and finish the activity
                databaseReference.setValue(myDB);
                finish();
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
                Intent intent_home = new Intent(EditCourseReminderActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(EditCourseReminderActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(EditCourseReminderActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(EditCourseReminderActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(EditCourseReminderActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(EditCourseReminderActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}
