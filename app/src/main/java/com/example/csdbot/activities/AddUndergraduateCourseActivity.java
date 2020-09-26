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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.csdbot.components.Course;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class AddUndergraduateCourseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RadioGroup areaCodes;
    private EditText nameET, codeET, descriptionET, ectsET, urlET;
    private FirebaseAuth firebaseAuth;
    private DatabaseHelper myDB;
    private Course course;
    private Button save;
    private ArrayList<Course> courseList = new ArrayList<Course>();

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_undergraduate_course);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Undergraduate Course");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        /* Find the activity's views
         *      nameET: The name course
         *      codeET: The code of the course
         *      descriptionET: The description of the course
         *      ectsET: The ECTS of the course
         *      urlET: The url of the course's page
         *      areaCodes: The RadioGroup of the area codes
         *      save: The button to save the course
         */
        nameET = findViewById(R.id.addCourseName);
        codeET = findViewById(R.id.addCourseCode);
        descriptionET = findViewById(R.id.addCourseDesc);
        ectsET = findViewById(R.id.addCourseECTS);
        urlET = findViewById(R.id.addCourseUrl);
        areaCodes = findViewById(R.id.addCourseArea);
        save = findViewById(R.id.saveBtn_addCourse);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Connect to the database and get the Undergraduate Courses List
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if (myDB != null) {
                    courseList = myDB.getCourseList();
                }

                // Save course on the list and update database
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String areaCode = "";
                        String areaName = "";
                        String name, code, ects, url, desc;
                        if (areaCodes.getCheckedRadioButtonId() == -1){
                            areaCode = "";
                            areaName = "";
                        } else {
                            switch (areaCodes.getCheckedRadioButtonId()){
                                case R.id.coreCourses:
                                    areaName = "Core Courses";
                                    areaCode = "";
                                    break;
                                case R.id.E1:
                                    areaCode = "Ε1";
                                    areaName = "Elective Courses from Mathematics and Physics";
                                    break;
                                case R.id.E2:
                                    areaCode = "Ε2" ;
                                    areaName = "Elective Courses from Other Sciences";
                                    break;
                                case R.id.E3:
                                    areaCode = "Ε3";
                                    areaName = "Networks and Telecommunications";
                                    break;
                                case R.id.E4:
                                    areaCode = "Ε4";
                                    areaName = "Hardware and Computer Systems";
                                    break;
                                case R.id.E5:
                                    areaCode = "E5";
                                    areaName = "Software Systems and Applications";
                                    break;
                                case R.id.E6:
                                    areaCode = "E6";
                                    areaName = "Information Systems";
                                    break;
                                case R.id.E7:
                                    areaCode = "E7";
                                    areaName = "Computer Vision and Robotics";
                                    break;
                                case R.id.E8:
                                    areaCode = "E8";
                                    areaName = "Algorithms and Computation Theory";
                                    break;
                                case R.id.E9:
                                    areaCode = "E9";
                                    areaName = "Society and Informatics";
                                    break;
                                case R.id.EE:
                                    areaCode ="";
                                    areaName = "Free Elective Courses";
                                    break;
                            }
                        }

                        code = codeET.getText().toString();
                        name = nameET.getText().toString();
                        desc = descriptionET.getText().toString();
                        ects = ectsET.getText().toString();
                        url = urlET.getText().toString();


                        // Create new Undergraduate Course
                        course = new Course(code,
                                name,
                                "-",
                                desc,
                                areaCode,
                                ects);
                        course.setArea_name_en(areaName);
                        course.setUrl(url);

                        // Add new Undergraduate Course to Undergraduate Courses List
                        courseList.add(course);
                        Collections.sort(courseList, Course.CourseComparator);

                        // Update the database
                        databaseReference.setValue(myDB);

                        // Start activity of newly created course and finish current one
                        Intent intent = new Intent(AddUndergraduateCourseActivity.this, CoursePageActivity.class);
                        intent.putExtra("Course Name", course.getName_en());
                        intent.putExtra("Post", "false");
                        startActivity(intent);
                        finish();

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddUndergraduateCourseActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        // Hide keyboard if the user taps out of it
        findViewById(R.id.addCourseLL).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                return true;
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
                Intent intent_home = new Intent(AddUndergraduateCourseActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(AddUndergraduateCourseActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(AddUndergraduateCourseActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(AddUndergraduateCourseActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(AddUndergraduateCourseActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(AddUndergraduateCourseActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}