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
import java.util.ArrayList;

public class RemindersListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView noRemindersText;
    private ImageView sad_face;
    private ListView lv;
    private ArrayList<Reminder> myReminders;
    private ArrayAdapter<Reminder> adapter;
    private FirebaseAuth firebaseAuth;
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
        setContentView(R.layout.activity_reminders_list);
        loading = ProgressDialog.show(RemindersListActivity.this, "",
                "Loading. Please wait...", true);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Reminders");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        /* Find the activity's views
         *      lv: The user's remindes ListView
         *      sad_face: ImageView if user has no reminders set
         *      noRemindersText: TextView if user has no reminders set
         */
        lv = (ListView) findViewById(R.id.lv_reminders);
        sad_face = (ImageView) findViewById(R.id.sad_face_reminders);
        noRemindersText = (TextView) findViewById(R.id.noRemindersText);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Connect to the database and retrieve the user's reminders
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                User profileUser = null;
                if (myDB != null) {
                    profileUser = myDB.getUserByUID(firebaseAuth.getUid());
                    myReminders = profileUser.getUser_reminders();
                }
                adapter = new RemindersListAdapter(RemindersListActivity.this, R.layout.reminders_list_item, profileUser.getUser_reminders());

                if ( !myReminders.isEmpty()){
                    sad_face.setVisibility(View.GONE);
                    noRemindersText.setVisibility(View.GONE);
                    lv.setAdapter(adapter);
                } else {
                    sad_face.setVisibility(View.VISIBLE);
                    noRemindersText.setVisibility(View.VISIBLE);
                    lv.setAdapter(adapter);
                }
                    adapter.notifyDataSetChanged();
                loading.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RemindersListActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        // Go to clicked reminder's activity
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Reminder temp = adapter.getItem(position);
                Intent intent = new Intent(RemindersListActivity.this, ReminderActivity.class);
                if (temp != null) {
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
                Intent intent_home = new Intent(RemindersListActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(RemindersListActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(RemindersListActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(RemindersListActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(RemindersListActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(RemindersListActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}
