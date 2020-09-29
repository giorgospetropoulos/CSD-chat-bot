package com.example.csdbot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.csdbot.R;
import com.example.csdbot.components.SpeechRecorder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private ImageView mic;
    private EditText search;
    private FirebaseAuth firebaseAuth;
    private SpeechRecorder speechRecorder;
    private SharedPreferences myPref;
    private String PermissionGranted;

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        // ---------- speech ------------
        mic = findViewById(R.id.mic);
        speechRecorder = new SpeechRecorder(HomeActivity.this);
        speechRecorder.initializeSpeechRecognizer();
        speechRecorder.checkVoiceRecodrPerimission();

        mic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        speechRecorder.startRecording();
                        Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
                        vb.vibrate(50);
                        mic.setImageResource(R.drawable.mic_down);
                        return true;
                    case MotionEvent.ACTION_UP:
                        speechRecorder.stopRecording();
                        mic.setImageResource(R.drawable.mic_up);
                        return false;
                }
                return false;
            }
        });
        //----------------------


        // Check if User has given Mic Recording Permission
        myPref = getApplicationContext().getSharedPreferences("MyPref", 0);
        PermissionGranted = myPref.getString("PermissionGranted","false");
        if ( !PermissionGranted.equals("true") ){
            Intent intent = new Intent(HomeActivity.this, GiveRecPermissionActivity.class);
            startActivity(intent);
        }

        /* Find the activity's views
         *      search: The search box's text
         *      courses: The Courses Button
         *      myCourses: The My Courses Button
         *      reminders: The Reminders Button
         *      calendar: The Calendar Button
         */
        search = findViewById(R.id.search);
        ImageView courses = findViewById(R.id.courses);
        ImageView myCourses = findViewById(R.id.mycourses);
        ImageView reminders = findViewById(R.id.reminders);
        ImageView calendar = findViewById(R.id.calendar);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //checks if user is already logged in and redirects him to login page if he is not logged in
        if (user == null ) {
            finish();
            Intent home = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(home);
        }

        // Search for the text given
        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (search.getRight() - search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        String query = search.getText().toString();
                        Intent intent = new Intent(HomeActivity.this, SearchResultsActivity.class);
                        intent.putExtra("SearchResultsActivity", "true");
                        intent.putExtra("Query", query);
                        startActivity(intent);
                    }
                }
                return false;
            }
        });

        courses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCourses();
            }
        });

        myCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMyCourses();
            }
        });

        reminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToReminders();
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCalendar();
            }
        });

    }




    public void goToCourses(){
        Intent intent = new Intent(this, PostOrUnderGraduateActivity.class);
        startActivity(intent);
    }

    public void goToMyCourses(){
        Intent intent = new Intent(this, MyCoursesActivity.class);
        startActivity(intent);
    }

    public void goToReminders(){
        Intent intent = new Intent(this, RemindersActivity.class);
        startActivity(intent);
    }

    public void goToCalendar(){
        Intent intent = new Intent(this, CalendarActivity.class);
        intent.putExtra("Calendar", "true");
        startActivity(intent);
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
                Intent intent_home = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(HomeActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(HomeActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(HomeActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}
