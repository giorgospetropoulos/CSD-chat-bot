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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.csdbot.components.Course;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.components.PostGraduateCourse;
import com.example.csdbot.R;
import com.example.csdbot.components.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;

public class AddPostgraduateCourseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CheckBox A, B, C, D, E, F, G, O;
    private EditText nameET, codeET, descriptionET, ectsET, urlET;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseHelper myDB;
    private PostGraduateCourse course;
    private Button save;
    private ArrayList<PostGraduateCourse> courseList = new ArrayList<PostGraduateCourse>();

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_postgraduate_course);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Postgraduate Course");
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
         *      A, B, C, D
         *      E, F, G, O: The checkbox of each area of study
         *      save: The button to save the course
         */
        nameET = (EditText) findViewById(R.id.addPostCourseName);
        codeET = (EditText) findViewById(R.id.addPostCourseCode);
        descriptionET = (EditText) findViewById(R.id.addPostCourseDesc);
        ectsET = (EditText) findViewById(R.id.addPostCourseECTS);
        urlET = (EditText) findViewById(R.id.addPostCourseUrl);
        A = (CheckBox) findViewById(R.id.addPostA);
        B = (CheckBox) findViewById(R.id.addPostB);
        C = (CheckBox) findViewById(R.id.addPostC);
        D = (CheckBox) findViewById(R.id.addPostD);
        E = (CheckBox) findViewById(R.id.addPostE);
        F = (CheckBox) findViewById(R.id.addPostF);
        G = (CheckBox) findViewById(R.id.addPostG);
        O = (CheckBox) findViewById(R.id.addPostO);
        save = (Button) findViewById(R.id.saveBtn_addPostCourse);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Connect to the database and get the Postgraduate Courses List
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                courseList = myDB.getPostGraduateCourseList();

                // Save course on the list and update database
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> area_codes_en = new ArrayList<String>();
                        ArrayList<String> area_names_en = new ArrayList<String>();
                        String name = "";
                        String code = "";
                        String desc = "";
                        String ects = "";
                        String url = "";

                        if ( A.isChecked() ){
                            area_codes_en.add("A");
                            area_names_en.add("Micro-electronic Systems Architecture");
                        }
                        if ( B.isChecked() ){
                            area_codes_en.add("B");
                            area_names_en.add("Computer Networks and Telecommunications");
                        }
                        if ( C.isChecked() ){
                            area_codes_en.add("C");
                            area_names_en.add("Parallel and Distributed Systems");
                        }
                        if ( D.isChecked() ){
                            area_codes_en.add("D");
                            area_names_en.add("Information Systems and Human-Computer Interaction");
                        }
                        if ( E.isChecked() ){
                            area_codes_en.add("E");
                            area_names_en.add("Computational and Cognitive Vision and Robotics");
                        }
                        if ( F.isChecked() ){
                            area_codes_en.add("F");
                            area_names_en.add("Algorithms and Systems Analysis");
                        }
                        if ( G.isChecked() ){
                            area_codes_en.add("G");
                            area_names_en.add("Biomedical Informatics and Technology");
                        }
                        if ( O.isChecked() ){
                            area_codes_en.add("Θ");
                            area_names_en.add("Multimedia Technology");
                        }

                        if ( nameET.getText().toString() != null){
                            name = nameET.getText().toString();
                        }
                        if ( codeET.getText().toString() != null){
                            code = codeET.getText().toString();
                        }
                        if ( descriptionET.getText().toString() != null ){
                            desc = descriptionET.getText().toString();
                        }
                        if ( ectsET.getText().toString() != null ){
                            ects = ectsET.getText().toString();
                        }
                        if ( urlET.getText().toString() != null ){
                            url = urlET.getText().toString();
                        }

                        // Create new Postgraduate Course
                        course = new PostGraduateCourse( name,
                                code,
                                desc,
                                url,
                                ects,
                                area_codes_en,
                                area_names_en);

                        // Add new Postgraduate Course to Postgraduate Courses List
                        courseList.add(course);
                        Collections.sort(courseList, Course.CourseComparator);

                        // Update the database
                        databaseReference.setValue(myDB);

                        // Start activity of newly created course and finish current one
                        Intent intent = new Intent(AddPostgraduateCourseActivity.this, PostGraduateCourseActivity.class);
                        intent.putExtra("Course Name", course.getName_en());
                        intent.putExtra("Post", "true");
                        startActivity(intent);
                        finish();

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddPostgraduateCourseActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        // Hide keyboard if the user taps out of it
        findViewById(R.id.addPostCourseLL).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
                Intent intent_home = new Intent(AddPostgraduateCourseActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(AddPostgraduateCourseActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(AddPostgraduateCourseActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(AddPostgraduateCourseActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(AddPostgraduateCourseActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(AddPostgraduateCourseActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}