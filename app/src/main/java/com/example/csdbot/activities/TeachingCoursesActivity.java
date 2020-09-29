package com.example.csdbot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.csdbot.R;
import com.example.csdbot.adapters.CourseListAdapter;
import com.example.csdbot.adapters.PostGraduateCourseListAdapter;
import com.example.csdbot.components.Course;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.components.PostGraduateCourse;
import com.example.csdbot.components.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeachingCoursesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<Course> undergraduateCoursesList = new ArrayList<Course>();
    private ArrayAdapter<Course> undergraduateCoursesAdapter;
    private ArrayList<PostGraduateCourse> postgraduateCoursesList = new ArrayList<PostGraduateCourse>();
    private ArrayAdapter<PostGraduateCourse> postgraduateCoursesAdapter;
    private DatabaseHelper myDB;
    private FirebaseAuth firebaseAuth;
    private User user;
    private ProgressDialog loading;

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teaching_courses);
        loading = ProgressDialog.show(TeachingCoursesActivity.this, "",
                "Loading. Please wait...", true);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Teaching Courses");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        /* Find the activity's views
         *      undergraduateCoursesBtn: Undergraduate Courses Button
         *      postgraduateCoursesBtn: Postgraduate Courses Button
         *      undergraduateCoursesLV: Undergraduate Courses List View
         *      postgraduateCoursesLV: Postgraduate Courses List View
         */
        Button undergraduateCoursesBtn = findViewById(R.id.undergraduateTeachingCourses);
        final Button postgraduateCoursesBtn = findViewById(R.id.postgraduateTeachingCourses);
        final ListView undergraduateCoursesLV = findViewById(R.id.undergraduateTeachingCourses_list);
        final ListView postgraduateCoursesLV = findViewById(R.id.postgraduateTeachingCourses_list);

        // Show undergraduate courses list
        undergraduateCoursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undergraduateCoursesLV.setVisibility(View.VISIBLE);
                postgraduateCoursesLV.setVisibility(View.GONE);
            }
        });

        // Show postgraduate courses list
        postgraduateCoursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undergraduateCoursesLV.setVisibility(View.GONE);
                postgraduateCoursesLV.setVisibility(View.VISIBLE);
            }
        });

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Database");

        // Connect to the database and retrieve the undergraduate
        // and postgraduate teaching courses of the user
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if ( myDB != null){
                    user = myDB.getUserByUID(firebaseAuth.getUid());
                    undergraduateCoursesList = user.getUndergraduate_teaching_courses();
                    postgraduateCoursesList = user.getPostgraduate_teaching_courses();

                    undergraduateCoursesAdapter = new CourseListAdapter(TeachingCoursesActivity.this, R.layout.courses_list_item, undergraduateCoursesList);
                    undergraduateCoursesLV.setAdapter(undergraduateCoursesAdapter);
                    undergraduateCoursesAdapter.notifyDataSetChanged();

                    postgraduateCoursesAdapter = new PostGraduateCourseListAdapter(TeachingCoursesActivity.this, R.layout.courses_list_item, postgraduateCoursesList);
                    postgraduateCoursesLV.setAdapter(postgraduateCoursesAdapter);
                    postgraduateCoursesAdapter.notifyDataSetChanged();
                    loading.dismiss();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        undergraduateCoursesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TeachingCoursesActivity.this, CoursePageActivity.class);
                intent.putExtra("Course Name", undergraduateCoursesList.get(position).getName_en());
                startActivity(intent);
            }
        });

        postgraduateCoursesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TeachingCoursesActivity.this, PostGraduateCourseActivity.class);
                intent.putExtra("Course Name", postgraduateCoursesList.get(position).getName_en());
                startActivity(intent);
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
                Intent intent_home = new Intent(TeachingCoursesActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(TeachingCoursesActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(TeachingCoursesActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(TeachingCoursesActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(TeachingCoursesActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(TeachingCoursesActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // --------------------------------------------------------------------------------------------
}
