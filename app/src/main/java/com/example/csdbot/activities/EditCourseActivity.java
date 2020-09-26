package com.example.csdbot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
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

import java.util.Objects;

public class EditCourseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RadioGroup areaCodes;
    private RadioButton core, E1, E2, E3, E4, E5, E6, E7, E8, E9, EE;
    private EditText codeET, descriptionET, ectsET;
    private FirebaseAuth firebaseAuth;
    private User profileUser;
    private Course course;
    private DatabaseHelper myDB;
    private Button save;

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Undergraduate Course");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        /* Find the activity's views
         *      couseName: The name course
         *      codeET: The code's EditText
         *      descriptionET: The description's EditText
         *      ectsET: The ECTS' EditText
         *      urlET: The url's EditText
         *      area: The area's TextView
         *      areaCodes: The RadioGroup of the area codes
         *      core, E1, E2, E3, E4,
         *      E5, E6, E7, E8, E9, EE: The RadioButton of each area of study
         *      save: The button to save the course
         */
        TextView couseName = findViewById(R.id.editCoursePageTitle);
        codeET = findViewById(R.id.editCoursePageCode);
        descriptionET = findViewById(R.id.editCoursePageDesc);
        ectsET = findViewById(R.id.editCoursePageECTS);
        areaCodes = findViewById(R.id.courseAreas);
        core = findViewById(R.id.coreCourses);
        E1 = findViewById(R.id.E1);
        E2 = findViewById(R.id.E2);
        E3 = findViewById(R.id.E3);
        E4 = findViewById(R.id.E4);
        E5 = findViewById(R.id.E5);
        E6 = findViewById(R.id.E6);
        E7 = findViewById(R.id.E7);
        E8 = findViewById(R.id.E8);
        E9 = findViewById(R.id.E9);
        EE = findViewById(R.id.EE);
        save = findViewById(R.id.saveBtn_editCourse);

        // Get and set the Course's name
        couseName.setText(getIntent().getStringExtra("Course Name"));

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Connect to the database and retrieve the course's data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if (myDB != null) {
                    profileUser = myDB.getUserByUID(firebaseAuth.getUid());
                    course = myDB.getCourseByName(getIntent().getStringExtra("Course Name"));
                }
                codeET.setText(course.getCode_en());
                descriptionET.setText(course.getDescription_en());
                if ( course.getArea_name_en().equals("Core Courses") ){
                    core.setChecked(true);
                }
                if ( course.getArea_name_en().equals("Ε1") ){
                    E1.setChecked(true);
                }
                if ( course.getArea_code_en().equals("Ε2") ){
                    E2.setChecked(true);
                }
                if ( course.getArea_code_en().equals("Ε3") ){
                    E3.setChecked(true);
                }
                if ( course.getArea_code_en().equals("Ε4") ){
                    E4.setChecked(true);
                }
                if ( course.getArea_code_en().equals("E5") ){
                    E5.setChecked(true);
                }
                if ( course.getArea_code_en().equals("E6") ){
                    E6.setChecked(true);
                }
                if ( course.getArea_code_en().equals("E7") ){
                    E7.setChecked(true);
                }
                if ( course.getArea_code_en().equals("E8") ){
                    E8.setChecked(true);
                }
                if ( course.getArea_code_en().equals("E9") ){
                    E9.setChecked(true);
                }
                if ( course.getArea_name_en().equals("Free Elective Courses") ){
                    EE.setChecked(true);
                }
                ectsET.setText(course.getECTS());

                // Save course's new data
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        course.setCode_en(codeET.getText().toString());
                        course.setDescription_en(descriptionET.getText().toString());
                        //course.setArea_name_en(areaET.getText().toString());
                        switch (areaCodes.getCheckedRadioButtonId()){
                            case R.id.coreCourses:
                                course.setArea_name_en("Core Courses");
                                course.setArea_name_gr("Μαθήματα κορμού");
                                break;
                            case R.id.E1:
                                course.setArea_code_en("Ε1");
                                course.setArea_name_en("Elective Courses from Mathematics and Physics");
                                course.setArea_code_gr("Ε1");
                                course.setArea_name_gr("Μαθήματα Επιλογής Μαθηματικών και Φυσικών Επιστημών");
                                break;
                            case R.id.E2:
                                course.setArea_code_en("Ε2");
                                course.setArea_name_en("Elective Courses from Other Sciences");
                                course.setArea_code_gr("Ε2");
                                course.setArea_name_gr("Μαθήματα Επιλογής Άλλων Επιστημών");
                                break;
                            case R.id.E3:
                                course.setArea_code_en("Ε3");
                                course.setArea_name_en("Networks and Telecommunications");
                                course.setArea_code_gr("Ε3");
                                course.setArea_name_gr("Τηλεπικοινωνίες και Δίκτυα");
                                break;
                            case R.id.E4:
                                course.setArea_code_en("Ε4");
                                course.setArea_name_en("Hardware and Computer Systems");
                                course.setArea_code_gr("Ε4");
                                course.setArea_name_gr("Υλικό και Συστήματα Υπολογιστών");
                                break;
                            case R.id.E5:
                                course.setArea_code_en("E5");
                                course.setArea_name_en("Software Systems and Applications");
                                course.setArea_code_gr("Ε5");
                                course.setArea_name_gr("Συστήματα Λογισμικού και Εφαρμογές");
                                break;
                            case R.id.E6:
                                course.setArea_code_en("E6");
                                course.setArea_name_en("Information Systems");
                                course.setArea_code_gr("Ε6");
                                course.setArea_name_gr("Πληροφοριακά Συστήματα");
                                break;
                            case R.id.E7:
                                course.setArea_code_en("E7");
                                course.setArea_name_en("Computer Vision and Robotics");
                                course.setArea_code_gr("Ε7");
                                course.setArea_name_gr("Υπολογιστική Όραση και Ρομποτική");
                                break;
                            case R.id.E8:
                                course.setArea_code_en("E8");
                                course.setArea_name_en("Algorithms and Computation Theory");
                                course.setArea_code_gr("Ε8");
                                course.setArea_name_gr("Αλγοριθμική και Θεωρία Υπολογισμού");
                                break;
                            case R.id.E9:
                                course.setArea_code_en("E9");
                                course.setArea_name_en("Society and Informatics");
                                course.setArea_code_gr("Ε9");
                                course.setArea_name_gr("Πληροφορική και Κοινωνία");
                                break;
                            case R.id.EE:
                                course.setArea_name_en("Free Elective Courses");
                                course.setArea_name_gr("Μαθήματα Ελεύθερης Επιλογής");
                                break;
                        }

                        course.setECTS(ectsET.getText().toString());

                        // Update the database
                        databaseReference.setValue(myDB);

                        // Start activity of edited course
                        Intent intent = new Intent(EditCourseActivity.this, CoursePageActivity.class);
                        intent.putExtra("Course Name", getIntent().getStringExtra("Course Name"));
                        intent.putExtra("Post", "false");
                        startActivity(intent);
                        finish();

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditCourseActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        // Hide keyboard if the user taps out of it
        findViewById(R.id.editCourseLL).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                return true;
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
                Intent intent_home = new Intent(EditCourseActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(EditCourseActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(EditCourseActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(EditCourseActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(EditCourseActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(EditCourseActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------

}
