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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.R;
import com.example.csdbot.components.User;
import com.example.csdbot.adapters.UserListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SetAdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ListView admins_lv;
    private ArrayList<User> teacherList = new ArrayList<User>();
    private ArrayAdapter<User> adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseHelper myDB;
    private Dialog setDialog;

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_admin);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Set Admin");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        /* Find the activity's views
         *      admins_lv: Users ListView
         */
        admins_lv = (ListView) findViewById(R.id.setAdminList);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Connect to the database and retrieve the users list
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                teacherList = myDB.getUserList();
                adapter = new UserListAdapter(SetAdminActivity.this, R.layout.user_list_item, teacherList);
                admins_lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                // Set clicked item as admin
                admins_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        TextView setTeacherTitle;
                        final Button setTeacherBtn, cancelBtn;
                        setDialog = new Dialog(SetAdminActivity.this);
                        setDialog.setContentView(R.layout.teacher_confirmation);
                        setTeacherTitle = setDialog.findViewById(R.id.teacherConfirmation);
                        if ( teacherList.get(position).isAdmin() ){
                            setTeacherTitle.setText("Are you sure you wish to remove user \""
                                    + teacherList.get(position).getName()
                                    + "\" the role of admin?");
                        } else {
                            setTeacherTitle.setText("Are you sure you wish to assign user \""
                                    + teacherList.get(position).getName()
                                    + "\" the role of admin?");
                        }

                        setTeacherBtn = (Button) setDialog.findViewById(R.id.teacherConfirmationBtn);
                        cancelBtn = (Button) setDialog.findViewById(R.id.teacherCancelationBtn);

                        if ( teacherList.get(position).isAdmin() ){
                            setTeacherBtn.setText("Remove");
                        }
                        setTeacherBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if ( teacherList.get(position).isAdmin() ){
                                    myDB.getUserByUID(teacherList.get(position).getUID()).setAdmin(false);
                                } else {
                                    myDB.getUserByUID(teacherList.get(position).getUID()).setAdmin(true);
                                }
                                databaseReference.setValue(myDB);
                                setDialog.dismiss();
                                finish();
                            }
                        });

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setDialog.dismiss();
                            }
                        });
                        setDialog.show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                Intent intent_home = new Intent(SetAdminActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(SetAdminActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(SetAdminActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(SetAdminActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(SetAdminActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(SetAdminActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}
