package com.example.csdbot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.csdbot.components.DatabaseHelper;
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

public class ReminderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView reminderTime;
    private Dialog editDialog;
    private DatabaseHelper myDB;
    private FirebaseAuth firebaseAuth;
    private User profileUser;

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("Reminder Name"));
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        /* Find the activity's views
         *      reminderName: The reminder's name
         *      reminderDesc: The reminder's description
         *      reminderTime: The reminder's time
         *      reminderPriority: The reminder's priority
         *      editReminder: Edit Reminder Button
         */
        TextView reminderName = findViewById(R.id.reminderPageName);
        TextView reminderDesc = findViewById(R.id.reminderPageDesc);
        TextView reminderPriority = findViewById(R.id.reminderPagePriority);
        reminderTime = findViewById(R.id.reminderPageTime);
        Button editReminder = findViewById(R.id.editReminderBtn);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Set the data of the reminder
        reminderName.setText(getIntent().getStringExtra("Reminder Name"));
        reminderDesc.setText(getIntent().getStringExtra("Reminder Desc"));
        switch (getIntent().getStringExtra("Reminder Priority")) {
            case "low":
                String low = " Low";
                reminderPriority.setText(low);
                reminderPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_priority,0, 0, 0);
                break;
            case "mid":
                String mid = " Medium";
                reminderPriority.setText(mid);
                reminderPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.medium_priority,0, 0, 0);
                break;
            case "high":
                String high = " High";
                reminderPriority.setText(high);
                reminderPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.high_priority,0, 0, 0);
                break;
            default:
                String def = " Low";
                reminderPriority.setText(def);
                reminderPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_priority,0, 0, 0);
                break;
        }

        // Connect to the database and retrieve the reminder's data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                profileUser = myDB.getUserByUID(firebaseAuth.getUid());
                Reminder tempRem = profileUser.getReminderByID(getIntent().getIntExtra("remID",0));
                String time = tempRem.getDay() + "/" +
                        tempRem.getMonth() + "/" +
                        tempRem.getYear() + " - ";
                reminderTime.setText(time);
                if ( tempRem.getHour() < 10 ){
                    time = time  + "0" + tempRem.getHour() + ":";
                    reminderTime.setText(time);
                } else {
                    time = time + tempRem.getHour() + ":";
                    reminderTime.setText(time);
                }
                if ( tempRem.getMin() < 10 ){
                    time = time + "0" + tempRem.getMin();
                    reminderTime.setText(time);
                } else {
                    time = time + tempRem.getMin();
                    reminderTime.setText(time);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReminderActivity.this , databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        // Edit reminder
        editReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView editPopUpTitle;
                Button edit, cancel;
                editDialog = new Dialog(ReminderActivity.this);
                editDialog.setContentView(R.layout.edit_reminder_confirmation);

                editPopUpTitle = editDialog.findViewById(R.id.popUpTitle);
                String title = "Are you sure you wish to edit reminder \"" +
                        getIntent().getStringExtra("Reminder Name") + "\"?";
                editPopUpTitle.setText(title);
                edit = editDialog.findViewById(R.id.edit_confirmation_btn);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ReminderActivity.this, EditReminderActivity.class);
                        intent.putExtra("remPosition", profileUser.getReminderPosition(getIntent().getStringExtra("Reminder Name")));
                        startActivity(intent);
                        editDialog.dismiss();
                    }
                });
                cancel = editDialog.findViewById(R.id.cancel_edit);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editDialog.dismiss();
                    }
                });
                editDialog.show();
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
                Intent intent_home = new Intent(ReminderActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(ReminderActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(ReminderActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(ReminderActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(ReminderActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(ReminderActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}