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
import android.widget.TextView;
import com.example.csdbot.components.Course;
import com.example.csdbot.adapters.CourseListAdapter;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.R;
import com.example.csdbot.components.Reminder;
import com.example.csdbot.adapters.RemindersListAdapter;
import com.example.csdbot.components.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

public class SearchResultsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ListView course_lv, reminder_lv;
    private TextView mainTitle, itemsFound;
    private ArrayList<Course> courseList = new ArrayList<Course>();
    private ArrayAdapter<Course> courseAdapter;
    private ArrayList<Reminder> reminderList = new ArrayList<Reminder>();
    private ArrayAdapter<Reminder> reminderAdapter;
    private DatabaseHelper myDB;
    private FirebaseAuth firebaseAuth;
    private User user;
    private String query;
    private ProgressDialog loading;

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    // --------- Search Engine -------------
    private InputStream isEn, isGr;
    private BufferedReader readerEn, readerGr;
    private HashSet<String> stopWords;
    // -------------------------------------



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        loading = ProgressDialog.show(SearchResultsActivity.this, "",
                "Loading. Please wait...", true);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Search results for: \"" +getIntent().getStringExtra("Query") + "\"");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // -------------------------------------

        // --------- Search Engine -------------
        try {
            stopWords = new HashSet<String>();
            String stop;
            isEn = getResources().getAssets().open("stopwordsEn.txt");
            isGr = getResources().getAssets().open("stopwordsGr.txt");
            readerEn = new BufferedReader(new InputStreamReader(isEn));
            readerGr = new BufferedReader(new InputStreamReader(isGr));
            while ((stop = readerEn.readLine()) != null) {
                stopWords.add(stop);
            }
            while ((stop = readerGr.readLine()) != null) {
                stopWords.add(stop);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        // -------------------------------------

        /* Find the activity's views
         *      courses_btn: Show Courses Button
         *      reminders_btn: Show Reminders Button
         *      itemsFound: Items Found TextView
         *      course_lv: Courses' ListView
         *      reminder_lv: Reminder's ListView
         *      mainTitle: Search Result Title
         *
         */
        Button courses_btn = (Button) findViewById(R.id.search_courses_btn);
        Button reminders_btn = (Button) findViewById(R.id.search_reminders_btn);
        itemsFound = (TextView) findViewById(R.id.itemsFound);
        course_lv = (ListView) findViewById(R.id.course_list);
        reminder_lv = (ListView) findViewById(R.id.reminder_list);
        mainTitle = (TextView) findViewById(R.id.searchedFor);

        // Get Search Query
        query = getIntent().getStringExtra("Query");

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Database");

        // Connect to the database and search for the query given
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if (myDB != null) {
                    user = myDB.getUserByUID(firebaseAuth.getUid());
                }
                String[] seperatedQuery = query.split(" ");
                for( int i = 0 ; i < seperatedQuery.length ; i++ ){
                    if ( !stopWords.contains(seperatedQuery[i]) ){
                        courseList.addAll(myDB.searchInCourses(seperatedQuery[i]));
                        reminderList.addAll(user.searchInReminders(seperatedQuery[i]));
                    }
                }
                String title = "Your results for searching \"" + query + "\"";
                mainTitle.setText(title);
                String noOfItemsFound = Integer.toString(courseList.size() + reminderList.size());
                itemsFound.setText( noOfItemsFound );
                itemsFound.setVisibility(View.VISIBLE);

                courseAdapter = new CourseListAdapter(SearchResultsActivity.this, R.layout.courses_list_item, courseList);
                course_lv.setAdapter(courseAdapter);
                courseAdapter.notifyDataSetChanged();

                reminderAdapter = new RemindersListAdapter(SearchResultsActivity.this, R.layout.reminders_list_item, reminderList);
                reminder_lv.setAdapter(reminderAdapter);
                reminderAdapter.notifyDataSetChanged();

                loading.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Show Courses
        courses_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                course_lv.setVisibility(View.VISIBLE);
                reminder_lv.setVisibility(View.GONE);
            }
        });

        // Show Reminders
        reminders_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                course_lv.setVisibility(View.GONE);
                reminder_lv.setVisibility(View.VISIBLE);
            }
        });

        // Go to clicked course's activity
        course_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course temp = courseAdapter.getItem(position);
                String tempName = null;
                if (temp != null) {
                    tempName = temp.getName_en();
                }
                Intent intent = new Intent(SearchResultsActivity.this, CoursePageActivity.class);
                intent.putExtra("Course Name", tempName);
                startActivity(intent);
            }
        });

        // Go to clicked reminder's activity
        reminder_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Reminder temp =  user.getUser_reminders().get(user.getReminderPosition(reminderAdapter.getItem(position).getName(),
                        reminderAdapter.getItem(position).getDescription(),
                        reminderAdapter.getItem(position).getDay(),
                        reminderAdapter.getItem(position).getMonth(),
                        reminderAdapter.getItem(position).getYear(),
                        reminderAdapter.getItem(position).getHour(),
                        reminderAdapter.getItem(position).getMin())
                );
                Intent intent = new Intent(SearchResultsActivity.this, ReminderActivity.class);
                intent.putExtra("Reminder Name", temp.getName());
                intent.putExtra("Reminder Desc", temp.getDescription());
                intent.putExtra("remID", temp.getId());
                switch (temp.getReminder_priority()){
                    case low:
                        intent.putExtra("Reminder Priority", "low");
                        break;
                    case mid:
                        intent.putExtra("Reminder Priority", "mid");
                        break;
                    case high:
                        intent.putExtra("Reminder Priority", "high");
                        break;
                    default:
                        intent.putExtra("Reminder Priority", "none");
                        break;
                }
                startActivity(intent);
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
                Intent intent_home = new Intent(SearchResultsActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(SearchResultsActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(SearchResultsActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(SearchResultsActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(SearchResultsActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(SearchResultsActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}
