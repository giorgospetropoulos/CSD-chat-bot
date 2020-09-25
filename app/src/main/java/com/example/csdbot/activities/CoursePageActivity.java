package com.example.csdbot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.csdbot.components.Course;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.R;
import com.example.csdbot.components.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CoursePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView CourseCode, CourseName, CourseTeacher, CourseDesc, CourseField, CourseECTS, CourseURL;
    private Button goToMainCoursePage, deleteCourse;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseHelper myDB;
    private Course course;
    private User user;
    private Dialog deleteDialog;

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_page);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("Course Name"));
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        /* Find the activity's views
         *      goToMainCoursePage: Button to go to the course's main page
         *      deleteCourse: Button to delete course
         *      CourseCode: The course's code
         *      CourseName: The course's name
         *      CourseTeacher: The course's teacher
         *      CourseDesc: The course's description
         *      CourseField: The course's field
         *      CourseECTS: The course's ECTS
         *      CourseURL: The course's Url
         */
        goToMainCoursePage = (Button) findViewById(R.id.courseMainPageButton);
        deleteCourse = (Button) findViewById(R.id.deleteCourse);
        CourseCode = (TextView) findViewById(R.id.coursePageCode);
        CourseName = (TextView) findViewById(R.id.coursePageName);
        CourseTeacher = (TextView) findViewById(R.id.coursePageTeacher);
        CourseDesc = (TextView) findViewById(R.id.coursePageDesc);
        CourseField = (TextView) findViewById(R.id.coursePageField);
        CourseECTS = (TextView) findViewById(R.id.coursePageECTS);
        CourseURL = (TextView) findViewById(R.id.coursePageURL);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = firebaseDatabase.getReference("Database");

        // Connect to the database, retrieve the course's data
        // and set the view accordingly
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                user = myDB.getUserByUID(firebaseAuth.getUid());
                if ( getIntent().hasExtra("Course Code") ){
                    String courseName = myDB.getCourseNameByCode(getIntent().getStringExtra("Course Code"));
                    course = myDB.getCourseByName(courseName);
                } else {
                    course = myDB.getCourseByName(getIntent().getStringExtra("Course Name"));
                }
                if ( course == null ){
                    course = new Course("",
                            "Something went wrong...",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "");
                }
                if (user.getUID().equals(course.getTeacherUID()) || user.getName().equals("admin") || user.isAdmin()) {
                    deleteCourse.setVisibility(View.VISIBLE);
                }

                CourseCode.setText(course.getCode_en());

                CourseName.setText(course.getName_en());

                CourseTeacher.setText(course.getTeacher());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    CourseDesc.setText(Html.fromHtml(course.getDescription_en(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    CourseDesc.setText(Html.fromHtml(course.getDescription_en()));
                }

                if ( course.getArea_name_en().equals("Core Courses")){
                    CourseField.setText(course.getArea_name_en());
                } else {
                    CourseField.setText(course.getArea_code_en() + " - " + course.getArea_name_en());
                }

                CourseECTS.setText(String.valueOf(course.getECTS()));

                CourseURL.setClickable(true);
                CourseURL.setMovementMethod(LinkMovementMethod.getInstance());
                String text = "<a href="+ course.getUrl() + "> " + course.getUrl() + " </a>";
                CourseURL.setText(Html.fromHtml(text));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Go to course's main page
        goToMainCoursePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoursePageActivity.this, CourseMainPageActivity.class);
                if ( getIntent().hasExtra("Course Code") ){
                    String courseName = myDB.getCourseNameByCode(getIntent().getStringExtra("Course Code"));
                    intent.putExtra("Course Name", courseName);
                } else {
                    intent.putExtra("Course Name", getIntent().getStringExtra("Course Name"));
                }
                intent.putExtra("Post", "false");
                startActivity(intent);
            }
        });

        // Delete course from the database
        deleteCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView deletePopUpTitle;
                Button delete, cancel;
                deleteDialog = new Dialog(CoursePageActivity.this);
                deleteDialog.setContentView(R.layout.delete_reminder_confirmation);
                deletePopUpTitle = (TextView) deleteDialog.findViewById(R.id.popUpDelTitle);
                deletePopUpTitle.setText("Are you sure you wish to delete course \""  + course.getName_en() +"\" ?");
                delete = (Button) deleteDialog.findViewById(R.id.delete_confirmation_btn);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        myDB.getCourseList().remove(course);
                        myRef.setValue(myDB);

                        Intent intent = new Intent(CoursePageActivity.this, CoursesActivity.class);
                        startActivity(intent);
                    }
                });
                cancel = (Button) deleteDialog.findViewById(R.id.cancel_delete_reminder);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDialog.dismiss();
                    }
                });
                deleteDialog.show();

            }
        });
    }

    // ----------------------------- Menu Functions --------------------------------------
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
                Intent intent_home = new Intent(CoursePageActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(CoursePageActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(CoursePageActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(CoursePageActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(CoursePageActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(CoursePageActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // --------------------------------------------------------------------------------------------
}
