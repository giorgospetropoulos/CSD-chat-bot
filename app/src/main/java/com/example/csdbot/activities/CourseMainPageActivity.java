package com.example.csdbot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.csdbot.components.Course;
import com.example.csdbot.adapters.CourseReminderListAdapter;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.components.PostGraduateCourse;
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
import java.util.Collections;

public class CourseMainPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView noRemindersText;
    private ImageView noReminders;
    private Button enroll, disenroll, add, setTeacher, addAll, editCourse;
    private ListView lv;
    private ArrayAdapter<Reminder> adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private User user;
    private DatabaseHelper myDB;
    private Course courseToAdd;
    private PostGraduateCourse postCourseToAdd;
    private Dialog enrollDialog, disenrollDialog, addAllDialog;
    private String courseName;
    private ProgressDialog loading;

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_main_page);

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
         *      noReminders: ImageView if the course has no reminders set
         *      noRemindersText: TextView if the course has no reminders set
         *      lv: ListView of the course's reminders
         *      enroll: Enroll Course Button
         *      disenroll: Disenroll Course Button
         *      add: Add New Course Reminder Button
         *      setTeacher: Set Course Teacher Button
         *      addAll: Add All Course Reminders to My Reminders Button
         *      editCourse: Edit Course Button
         */
        noReminders = findViewById(R.id.noCourseReminders);
        noRemindersText = findViewById(R.id.noCourseRemindersText);
        lv = findViewById(R.id.courseRemindersLV);
        enroll = findViewById(R.id.courseMainPageEnroll);
        disenroll = findViewById(R.id.courseMainPageDisenroll);
        add = findViewById(R.id.addNewCourseReminder);
        setTeacher = findViewById(R.id.setTeacher);
        addAll = findViewById(R.id.courseMainPageAddAll);
        editCourse = findViewById(R.id.editCourseBtn);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Database");

        // Get Course's name
        courseName = getIntent().getStringExtra("Course Name");

        // Start Activity of the clicked reminder
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Reminder temp = adapter.getItem(position);
                Intent intent = new Intent(CourseMainPageActivity.this, CourseReminderActivity.class);
                if (temp != null) {
                    intent.putExtra("remID", temp.getId());
                    intent.putExtra("Post",getIntent().getStringExtra("Post"));
                    intent.putExtra("Reminder Name", temp.getName());
                    intent.putExtra("Reminder Desc", temp.getDescription());
                    intent.putExtra("Course Name", getIntent().getStringExtra("Course Name"));
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

        // Connect to the database and retrieve the course's data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if (myDB != null) {
                    user = myDB.getUserByUID(firebaseAuth.getUid());
                }

                // Check if course is Postgraduate or Undergaduate
                // and retrieve the appropriate data
                if ( getIntent().hasExtra("Post")){
                    if ( getIntent().getStringExtra("Post").equals("false") ){
                        courseToAdd = myDB.getCourseByName(courseName);
                        if ( courseToAdd.getCourseReminders().isEmpty() ){
                            noReminders.setVisibility(View.VISIBLE);
                            noRemindersText.setVisibility(View.VISIBLE);
                        } else {
                            noReminders.setVisibility(View.GONE);
                            noRemindersText.setVisibility(View.GONE);
                        }
                        adapter = new CourseReminderListAdapter(CourseMainPageActivity.this, R.layout.course_reminder_list_item, courseToAdd.getCourseReminders());
                    } else {
                        postCourseToAdd = myDB.getPostGraduateCourseByName(courseName);
                        if ( postCourseToAdd.getCourseReminders().isEmpty() ){
                            noReminders.setVisibility(View.VISIBLE);
                            noRemindersText.setVisibility(View.VISIBLE);
                        } else {
                            noReminders.setVisibility(View.GONE);
                            noRemindersText.setVisibility(View.GONE);
                        }
                        adapter = new CourseReminderListAdapter(CourseMainPageActivity.this, R.layout.course_reminder_list_item, postCourseToAdd.getCourseReminders());

                    }
                }

                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();



                // Check if the User is an admin or the teacher of the course
                // and display the appropriate components
                if ( getIntent().getStringExtra("Post").equals("false") ){
                    if (user.getUID().equals(courseToAdd.getTeacherUID()) || user.getName().equals("admin") || user.isAdmin()) {
                        add.setVisibility(View.VISIBLE);
                        editCourse.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (user.getUID().equals(postCourseToAdd.getTeacherUID()) || user.getName().equals("admin") || user.isAdmin()) {
                        add.setVisibility(View.VISIBLE);
                        editCourse.setVisibility(View.VISIBLE);
                    }
                }

                if ( user.isAdmin() || user.getName().equals("admin") ){
                    setTeacher.setVisibility(View.VISIBLE);
                }

                // Check if the user is enrolled to the course and display
                // enroll or disenroll button accordingly
                if (user.isEnrolledTo(getIntent().getStringExtra("Course Name"))) {
                    enroll.setVisibility(View.GONE);
                    disenroll.setVisibility(View.VISIBLE);
                } else {
                    enroll.setVisibility(View.VISIBLE);
                    disenroll.setVisibility(View.GONE);
                }

                // Add all course reminders to the user's reminders
                addAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView addAllTitle;
                        Button addAllRems, cancelAddAll;
                        addAllDialog = new Dialog(CourseMainPageActivity.this);
                        addAllDialog.setContentView(R.layout.add_all_reminders_confirmation);
                        addAllTitle = addAllDialog.findViewById(R.id.addAllConfirmationTitle);
                        String title = "Are you sure you wish to add all the reminders of "
                                + courseName
                                + " to your reminders? \n\n"
                                + "You will not be enrolled to the course with this action. " +
                                "If you wish to enroll to the course, click the Enroll button.";
                        addAllTitle.setText(title);
                        addAllRems = addAllDialog.findViewById(R.id.addAllBtn);
                        cancelAddAll = addAllDialog.findViewById(R.id.cancelAddAll);

                        addAllRems.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loading = ProgressDialog.show(CourseMainPageActivity.this, "",
                                        "Loading. Please wait...", true);
                                if ( getIntent().getStringExtra("Post").equals("false") ){
                                    for( Reminder temp : courseToAdd.getCourseReminders() ){
                                        if ( !user.hasReminderByID(temp.getId()) ){
                                            user.addUser_reminders(temp);
                                        }
                                    }
                                } else {
                                    for( Reminder temp : postCourseToAdd.getCourseReminders() ){
                                        if ( !user.hasReminderByID(temp.getId()) ){
                                            user.addUser_reminders(temp);
                                        }
                                    }
                                }

                                databaseReference.setValue(myDB);
                                loading.dismiss();
                                addAllDialog.dismiss();

                            }
                        });

                        cancelAddAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addAllDialog.dismiss();
                            }
                        });
                        addAllDialog.show();
                    }
                });

                // Check if course is undergraduate or undergraduate
                // and start the appropriate activity to add a new reminder
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( getIntent().getStringExtra("Post").equals("false") ){
                            Intent intent = new Intent(CourseMainPageActivity.this, AddCourseReminderActivity.class);
                            intent.putExtra("Course Name", courseName);
                            intent.putExtra("Post",getIntent().getStringExtra("Post"));
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(CourseMainPageActivity.this, AddPostGraduateCourseReminderActivity.class);
                            intent.putExtra("Course Name", courseName);
                            intent.putExtra("Post",getIntent().getStringExtra("Post"));
                            startActivity(intent);
                            finish();
                        }

                    }
                });

                // Start activity to set course teacher
                setTeacher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CourseMainPageActivity.this, SetTeacherActivity.class);
                        intent.putExtra("Course Name", courseName);
                        intent.putExtra("Post", getIntent().getStringExtra("Post"));
                        startActivity(intent);
                    }
                });

                // Enroll user to course
                enroll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TextView enrollPopUpTitle;
                        Button enroll_btn, cancel_enroll;
                        enrollDialog = new Dialog(CourseMainPageActivity.this);
                        enrollDialog.setContentView(R.layout.enroll_confirmation);

                        enrollPopUpTitle = enrollDialog.findViewById(R.id.enrollPopUpTitle);
                        String title = "Are you sure you wish to enroll to \""
                                + getIntent().getStringExtra("Course Name")
                                +"\"? All course reminders will be added to your reminders" +
                                " and you will be notified about future reminders as well.";
                        enrollPopUpTitle.setText(title);
                        enroll_btn = enrollDialog.findViewById(R.id.enroll_confirmation_btn);

                        enroll_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loading = ProgressDialog.show(CourseMainPageActivity.this, "",
                                        "Loading. Please wait...", true);
                                if ( getIntent().getStringExtra("Post").equals("false") ){
                                    courseToAdd.addUserToCourseUserList(firebaseAuth.getUid());
                                    user.getUser_courses().add(courseToAdd);
                                } else {
                                    postCourseToAdd.addUserToCourseUserList(firebaseAuth.getUid());
                                    user.getUser_courses().add(postCourseToAdd);
                                }
                                Collections.sort(user.getUser_courses(),Course.CourseComparator);
                                if ( getIntent().getStringExtra("Post").equals("false") ){
                                    if (!courseToAdd.getCourseReminders().isEmpty()) {
                                        for( Reminder temp : courseToAdd.getCourseReminders() ) {
                                            if ( !user.hasReminderByID(temp.getId()) ){
                                                user.addUser_reminders(temp);
                                            }
                                        }
                                    }
                                } else {
                                    if (!postCourseToAdd.getCourseReminders().isEmpty()) {
                                        for( Reminder temp : postCourseToAdd.getCourseReminders() ) {
                                            if ( !user.hasReminderByID(temp.getId()) ){
                                                user.addUser_reminders(temp);
                                            }
                                        }
                                    }
                                }

                                databaseReference.setValue(myDB);
                                loading.dismiss();
                                enrollDialog.dismiss();
                            }
                        });

                        cancel_enroll = enrollDialog.findViewById(R.id.cancel_enroll);
                        cancel_enroll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                enrollDialog.dismiss();
                            }
                        });
                        enrollDialog.show();



                    }
                });

                // Disenroll user from course
                disenroll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView disenrollPopUpTitle;
                        Button disenroll_btn, cancel_disenroll;
                        disenrollDialog = new Dialog(CourseMainPageActivity.this);
                        disenrollDialog.setContentView(R.layout.disenroll_confirmation);

                        disenrollPopUpTitle = disenrollDialog.findViewById(R.id.disenrollPopUpTitle);
                        String title = "Are you sure you wish to disenroll from \""
                                + getIntent().getStringExtra("Course Name")
                                +"\"? You will not be notified for any new reminders.";
                        disenrollPopUpTitle.setText(title);
                        disenroll_btn = disenrollDialog.findViewById(R.id.disenroll_confirmation_btn);

                        disenroll_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loading = ProgressDialog.show(CourseMainPageActivity.this, "",
                                        "Loading. Please wait...", true);
                                //user = myDB.getUserByUID(firebaseAuth.getUid());
                                if ( getIntent().getStringExtra("Post").equals("false") ){
                                    Course courseToRemove = myDB.getCourseByName(getIntent().getStringExtra("Course Name"));
                                    user.removeCourse(courseToRemove);
                                    courseToRemove.removeUserFromCourseUserList(firebaseAuth.getUid());
                                } else {
                                    PostGraduateCourse courseToRemove = myDB.getPostGraduateCourseByName(getIntent().getStringExtra("Course Name"));
                                    user.removeCourse(courseToRemove);
                                    courseToRemove.removeUserFromCourseUserList(firebaseAuth.getUid());
                                }

                                Collections.sort(user.getUser_courses(),Course.CourseComparator);

                                databaseReference.setValue(myDB);
                                loading.dismiss();
                                disenrollDialog.dismiss();
                            }
                        });

                        cancel_disenroll = disenrollDialog.findViewById(R.id.cancel_disenroll);
                        cancel_disenroll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                disenrollDialog.dismiss();
                            }
                        });
                        disenrollDialog.show();
                    }
                });

                // Check if course is undergraduate or postgraduate
                // and start appropriate activity to edit the course
                editCourse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( getIntent().getStringExtra("Post").equals("false") ){
                            Intent intent = new Intent(CourseMainPageActivity.this, EditCourseActivity.class);
                            intent.putExtra("Course Name", getIntent().getStringExtra("Course Name"));
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(CourseMainPageActivity.this, EditPostCourseActivity.class);
                            intent.putExtra("Course Name", getIntent().getStringExtra("Course Name"));
                            startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                Intent intent_home = new Intent(CourseMainPageActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(CourseMainPageActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(CourseMainPageActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(CourseMainPageActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(CourseMainPageActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(CourseMainPageActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // --------------------------------------------------------------------------------------------
}
