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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.csdbot.components.Course;
import com.example.csdbot.adapters.CourseListAdapter;
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
import java.util.ArrayList;

public class MyCoursesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView noCoursesText;
    private ImageView sad_face;
    private ListView lv;
    private ArrayList<Course> myCourses;
    private ArrayAdapter<Course> adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private User profileUser;
    private DatabaseHelper myDB;
    private ProgressDialog loading;

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);
        loading = ProgressDialog.show(MyCoursesActivity.this, "",
                "Loading. Please wait...", true);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Courses");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        /* Find the activity's views
         *      lv: The ListView of the the User's Courses
         *      sad_face: ImageView if the user has no enrolled courses
         *      noCoursesText: TextView if the user has no enrolled courses
         */
        lv = findViewById(R.id.lv);
        sad_face = findViewById(R.id.sad_face_myCourses);
        noCoursesText = findViewById(R.id.noCoursesText);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Connect to the database and retrieve the user's enrolled courses
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if (myDB != null) {
                    profileUser = myDB.getUserByUID(firebaseAuth.getUid());
                }
                myCourses = profileUser.getUser_courses();
                adapter = new CourseListAdapter(MyCoursesActivity.this, R.layout.courses_list_item, profileUser.getUser_courses());

                if ( !myCourses.isEmpty() ){
                    sad_face.setVisibility(View.GONE);
                    noCoursesText.setVisibility(View.GONE);
                    lv.setAdapter(adapter);
                } else {
                    sad_face.setVisibility(View.VISIBLE);
                    noCoursesText.setVisibility(View.VISIBLE);
                    lv.setAdapter(adapter);
                }
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                // Go to clicked course's activity
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Course temp = adapter.getItem(position);
                        String tempName = null;
                        if (temp != null) {
                            tempName = temp.getName_en();
                        }
                        if ( Integer.parseInt(temp.getCode_en().substring(temp.getCode_en().length() - 3)) >= 500 ){
                            Intent intent = new Intent(MyCoursesActivity.this, PostGraduateCourseActivity.class);
                            intent.putExtra("Course Name", tempName);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MyCoursesActivity.this, CoursePageActivity.class);
                            intent.putExtra("Course Name", tempName);
                            startActivity(intent);
                        }


                    }
                });
                loading.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyCoursesActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
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
                Intent intent_home = new Intent(MyCoursesActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(MyCoursesActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(MyCoursesActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(MyCoursesActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(MyCoursesActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(MyCoursesActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}
