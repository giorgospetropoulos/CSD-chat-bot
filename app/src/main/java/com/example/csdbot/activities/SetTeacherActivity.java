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
import com.example.csdbot.components.Course;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.R;
import com.example.csdbot.components.PostGraduateCourse;
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

public class SetTeacherActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ListView teachers_lv;
    private ArrayList<User> teacherList = new ArrayList<User>();
    private ArrayAdapter<User> adapter;
    private FirebaseAuth firebaseAuth;
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
        setContentView(R.layout.activity_set_teacher);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Set Teacher");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        /* Find the activity's views
         *      teachers_lv: Users ListView
         */
        teachers_lv = findViewById(R.id.setTeacherList);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Connect to the database and retrieve the users list
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                final Course undergraduateCourse;
                final PostGraduateCourse postGraduateCourse;
                if (myDB != null) {
                    String post = getIntent().getStringExtra("Post");
                    if ( post != null ){
                        // if course is a postgraduate course
                        if ( post.equals("true") ) {
                            postGraduateCourse = myDB.getPostGraduateCourseByName(getIntent().getStringExtra("Course Name"));
                            teachers_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                    TextView setTeacherTitle;
                                    final Button setTeacherBtn, cancelBtn;
                                    setDialog = new Dialog(SetTeacherActivity.this);
                                    setDialog.setContentView(R.layout.teacher_confirmation);
                                    setTeacherTitle = setDialog.findViewById(R.id.teacherConfirmation);
                                    if ( postGraduateCourse.getTeacherUID().equals(teacherList.get(position).getUID()) ){
                                        String title = "Are you sure you wish to remove user \""
                                                + teacherList.get(position).getName()
                                                + "\" as the teacher of "
                                                + postGraduateCourse.getName_en()
                                                + "?";
                                        setTeacherTitle.setText(title);
                                        setTeacherBtn = setDialog.findViewById(R.id.teacherConfirmationBtn);
                                        setTeacherBtn.setText("Remove");
                                        setTeacherBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                postGraduateCourse.setTeacher("");
                                                postGraduateCourse.setTeacherUID(String.valueOf(0));
                                                teacherList.get(position).setTeachingCourse("");
                                                teacherList.get(position).getPostgraduate_teaching_courses().remove(postGraduateCourse);
                                                //Set user and course databaseReference
                                                databaseReference.setValue(myDB);
                                                setDialog.dismiss();
                                                finish();
                                            }
                                        });
                                    } else {
                                        String title = "Are you sure you wish to assign user \""
                                                + teacherList.get(position).getName()
                                                + "\" as the teacher of "
                                                + postGraduateCourse.getName_en()
                                                + "?";
                                        setTeacherTitle.setText(title);
                                        setTeacherBtn = setDialog.findViewById(R.id.teacherConfirmationBtn);
                                        setTeacherBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                postGraduateCourse.setTeacher(teacherList.get(position).getName());
                                                postGraduateCourse.setTeacherUID(teacherList.get(position).getUID());
                                                teacherList.get(position).getPostgraduate_teaching_courses().add(postGraduateCourse);
                                                databaseReference.setValue(myDB);
                                                setDialog.dismiss();
                                                finish();
                                            }
                                        });
                                    }

                                    cancelBtn = setDialog.findViewById(R.id.teacherCancelationBtn);
                                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            setDialog.dismiss();
                                        }
                                    });
                                    setDialog.show();
                                }
                            });
                        } else {
                            // if course is a undergraduate course
                            undergraduateCourse = myDB.getCourseByName(getIntent().getStringExtra("Course Name"));
                            // Set clicked item as teacher of course
                            teachers_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                    TextView setTeacherTitle;
                                    final Button setTeacherBtn, cancelBtn;
                                    setDialog = new Dialog(SetTeacherActivity.this);
                                    setDialog.setContentView(R.layout.teacher_confirmation);
                                    setTeacherTitle = setDialog.findViewById(R.id.teacherConfirmation);
                                    if ( undergraduateCourse.getTeacherUID().equals(teacherList.get(position).getUID()) ){
                                        String title = "Are you sure you wish to remove user \""
                                                + teacherList.get(position).getName()
                                                + "\" as the teacher of "
                                                + undergraduateCourse.getName_en()
                                                + "?";
                                        setTeacherTitle.setText(title);
                                        setTeacherBtn = setDialog.findViewById(R.id.teacherConfirmationBtn);
                                        setTeacherBtn.setText("Remove");
                                        setTeacherBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                undergraduateCourse.setTeacher("");
                                                undergraduateCourse.setTeacherUID(String.valueOf(0));
                                                teacherList.get(position).setTeachingCourse("");
                                                teacherList.get(position).getUndergraduate_teaching_courses().remove(undergraduateCourse);
                                                databaseReference.setValue(myDB);
                                                setDialog.dismiss();
                                                finish();
                                            }
                                        });
                                    } else {
                                        String title = "Are you sure you wish to assign user \""
                                                + teacherList.get(position).getName()
                                                + "\" as the teacher of "
                                                + undergraduateCourse.getName_en()
                                                + "?";
                                        setTeacherTitle.setText(title);
                                        setTeacherBtn = setDialog.findViewById(R.id.teacherConfirmationBtn);
                                        setTeacherBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                undergraduateCourse.setTeacher(teacherList.get(position).getName());
                                                undergraduateCourse.setTeacherUID(teacherList.get(position).getUID());
                                                teacherList.get(position).getUndergraduate_teaching_courses().add(undergraduateCourse);
                                                databaseReference.setValue(myDB);
                                                setDialog.dismiss();
                                                finish();
                                            }
                                        });
                                    }

                                    cancelBtn = setDialog.findViewById(R.id.teacherCancelationBtn);



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
                    }

                    teacherList = myDB.getUserList();
                    adapter = new UserListAdapter(SetTeacherActivity.this, R.layout.user_list_item, teacherList);
                    teachers_lv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                }



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
                Intent intent_home = new Intent(SetTeacherActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(SetTeacherActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(SetTeacherActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(SetTeacherActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(SetTeacherActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(SetTeacherActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}
