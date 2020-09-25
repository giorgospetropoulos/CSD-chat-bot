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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.components.PostGraduateCourse;
import com.example.csdbot.R;
import com.example.csdbot.components.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditPostCourseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView code, description, area, ects, couseName;
    private CheckBox A, B, C, D, E, F, G, O;
    private EditText codeET, descriptionET, ectsET;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private User profileUser;
    private PostGraduateCourse course;
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
        setContentView(R.layout.activity_edit_post_course);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Postgraduate Course");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        /* Find the activity's views
         *      couseName: The name course
         *      code: The code's TextView
         *      codeET: The code's EditText
         *      description: The Description's TextView
         *      descriptionET: The description's EditText
         *      ects: The ECTS' TextView
         *      ectsET: The ECTS' EditText
         *      urlET: The url's EditText
         *      area: The area's TextView
         *      A, B, C, D,
         *      E, F, G, O: The RadioButton of each area of study
         *      save: The button to save the course
         */
        couseName = (TextView) findViewById(R.id.editPostCoursePageTitle);
        code = (TextView) findViewById(R.id.editPostCoursePageCodeTitle);
        codeET = (EditText) findViewById(R.id.editPostCoursePageCode);
        description = (TextView) findViewById(R.id.editPostCoursePageDescTitle);
        descriptionET = (EditText) findViewById(R.id.editPostCoursePageDesc);
        area = (TextView) findViewById(R.id.editPostCoursePageFieldTitle);
        A = (CheckBox) findViewById(R.id.postA);
        B = (CheckBox) findViewById(R.id.postB);
        C = (CheckBox) findViewById(R.id.postC);
        D = (CheckBox) findViewById(R.id.postD);
        E = (CheckBox) findViewById(R.id.postE);
        F = (CheckBox) findViewById(R.id.postF);
        G = (CheckBox) findViewById(R.id.postG);
        O = (CheckBox) findViewById(R.id.postO);
        ects = (TextView) findViewById(R.id.editPostCoursePageECTSField);
        ectsET = (EditText) findViewById(R.id.editPostCoursePageECTS);
        save = (Button) findViewById(R.id.saveBtn_editPostCourse);

        // Get and set the Course's name
        couseName.setText(getIntent().getStringExtra("Course Name"));

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Connect to the database and retrieve the course's data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                profileUser = myDB.getUserByUID(firebaseAuth.getUid());
                course = myDB.getPostGraduateCourseByName(getIntent().getStringExtra("Course Name"));
                codeET.setText(course.getCode_en());
                descriptionET.setText(course.getDescription_en());
                if ( course.getArea_codes_en().contains("A")){
                    A.setChecked(true);
                }
                if ( course.getArea_codes_en().contains("B")){
                    B.setChecked(true);
                }
                if ( course.getArea_codes_en().contains("C")){
                    C.setChecked(true);
                }
                if ( course.getArea_codes_en().contains("D")){
                    D.setChecked(true);
                }
                if ( course.getArea_codes_en().contains("E")){
                    E.setChecked(true);
                }
                if ( course.getArea_codes_en().contains("F")){
                    F.setChecked(true);
                }
                if ( course.getArea_codes_en().contains("G")){
                    G.setChecked(true);
                }
                if ( course.getArea_codes_en().contains("Θ")){
                    O.setChecked(true);
                }

                ectsET.setText(course.getECTS());

                // Save course's new data
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        course.setCode_en(codeET.getText().toString());
                        course.setDescription_en(descriptionET.getText().toString());
                        //course.getArea_names_en().add(areaET.getText().toString());
                         if ( A.isChecked() ){
                             if ( !course.getArea_codes_en().contains("A")){
                                 course.getArea_codes_en().add("A");
                                 course.getArea_names_en().add("Micro-electronic Systems Architecture");
                                 course.getArea_codes_gr().add("Α");
                                 course.getArea_names_en().add("Αρχιτεκτονική Μικροηλεκτρονικών Συστημάτων");
                             }
                         } else {
                             if ( course.getArea_codes_en().contains("A")){
                                 course.getArea_codes_en().remove("A");
                                 course.getArea_names_en().remove("Micro-electronic Systems Architecture");
                                 course.getArea_codes_gr().remove("Α");
                                 course.getArea_names_en().remove("Αρχιτεκτονική Μικροηλεκτρονικών Συστημάτων");
                             }
                         }

                        if ( B.isChecked() ){
                            if ( !course.getArea_codes_en().contains("B")){
                                course.getArea_codes_en().add("B");
                                course.getArea_names_en().add("Computer Networks and Telecommunications");
                                course.getArea_codes_gr().add("Β");
                                course.getArea_names_en().add("Δίκτυα Υπολογιστών και Τηλεπικοινωνίες");
                            }
                        } else {
                            if ( course.getArea_codes_en().contains("B")){
                                course.getArea_codes_en().remove("B");
                                course.getArea_names_en().remove("Computer Networks and Telecommunications");
                                course.getArea_codes_gr().remove("Β");
                                course.getArea_names_en().remove("Δίκτυα Υπολογιστών και Τηλεπικοινωνίες");
                            }
                        }

                        if ( C.isChecked() ){
                            if ( !course.getArea_codes_en().contains("C")){
                                course.getArea_codes_en().add("C");
                                course.getArea_names_en().add("Parallel and Distributed Systems");
                                course.getArea_codes_gr().add("Γ");
                                course.getArea_names_en().add("Παράλληλα και Κατανεμημένα Συστήματα");
                            }
                        } else {
                            if ( course.getArea_codes_en().contains("C")){
                                course.getArea_codes_en().remove("C");
                                course.getArea_names_en().remove("Parallel and Distributed Systems");
                                course.getArea_codes_gr().remove("Γ");
                                course.getArea_names_en().remove("Παράλληλα και Κατανεμημένα Συστήματα");
                            }
                        }

                        if ( D.isChecked() ){
                            if ( !course.getArea_codes_en().contains("D")){
                                course.getArea_codes_en().add("D");
                                course.getArea_names_en().add("Information Systems and Human-Computer Interaction");
                                course.getArea_codes_gr().add("Δ");
                                course.getArea_names_en().add("Πληροφοριακά Συστήματα και Αλληλεπίδραση Ανθρώπου Υπολογιστή");
                            }
                        } else {
                            if ( course.getArea_codes_en().contains("D")){
                                course.getArea_codes_en().remove("D");
                                course.getArea_names_en().remove("Information Systems and Human-Computer Interaction");
                                course.getArea_codes_gr().remove("Δ");
                                course.getArea_names_en().remove("Πληροφοριακά Συστήματα και Αλληλεπίδραση Ανθρώπου Υπολογιστή");
                            }
                        }

                        if ( E.isChecked() ){
                            if ( !course.getArea_codes_en().contains("E")){
                                course.getArea_codes_en().add("E");
                                course.getArea_names_en().add("Computational and Cognitive Vision and Robotics");
                                course.getArea_codes_gr().add("Ε");
                                course.getArea_names_en().add("Υπολογιστική και Γνωσιακή Οραση και Ρομποτική");
                            }
                        } else {
                            if ( course.getArea_codes_en().contains("E")){
                                course.getArea_codes_en().remove("E");
                                course.getArea_names_en().remove("Computational and Cognitive Vision and Robotics");
                                course.getArea_codes_gr().remove("Ε");
                                course.getArea_names_en().remove("Υπολογιστική και Γνωσιακή Οραση και Ρομποτική");
                            }
                        }

                        if ( F.isChecked() ){
                            if ( !course.getArea_codes_en().contains("F")){
                                course.getArea_codes_en().add("F");
                                course.getArea_names_en().add("Algorithms and Systems Analysis");
                                course.getArea_codes_gr().add("Ζ");
                                course.getArea_names_en().add("Αλγοριθμική και Ανάλυση Συστημάτων");
                            }
                        } else {
                            if ( course.getArea_codes_en().contains("F")){
                                course.getArea_codes_en().remove("F");
                                course.getArea_names_en().remove("Algorithms and Systems Analysis");
                                course.getArea_codes_gr().remove("Ζ");
                                course.getArea_names_en().remove("Αλγοριθμική και Ανάλυση Συστημάτων");
                            }
                        }

                        if ( G.isChecked() ){
                            if ( !course.getArea_codes_en().contains("G")){
                                course.getArea_codes_en().add("G");
                                course.getArea_names_en().add("Biomedical Informatics and Technology");
                                course.getArea_codes_gr().add("Η");
                                course.getArea_names_en().add("Βιοϊατρική Πληροφορική και Τεχνολογία");
                            }
                        } else {
                            if ( course.getArea_codes_en().contains("G")){
                                course.getArea_codes_en().remove("G");
                                course.getArea_names_en().remove("Biomedical Informatics and Technology");
                                course.getArea_codes_gr().remove("Η");
                                course.getArea_names_en().remove("Βιοϊατρική Πληροφορική και Τεχνολογία");
                            }
                        }

                        if ( O.isChecked() ){
                            if ( !course.getArea_codes_en().contains("Θ")){
                                course.getArea_codes_en().add("Θ");
                                course.getArea_names_en().add("Multimedia Technology");
                                course.getArea_codes_gr().add("Θ");
                                course.getArea_names_en().add("Τεχνολογία Πολυμέσων");
                            }
                        } else {
                            if ( course.getArea_codes_en().contains("Θ")){
                                course.getArea_codes_en().remove("Θ");
                                course.getArea_names_en().remove("Multimedia Technology");
                                course.getArea_codes_gr().remove("Θ");
                                course.getArea_names_en().remove("Τεχνολογία Πολυμέσων");
                            }
                        }

                        course.setECTS(ectsET.getText().toString());

                        // Update the database
                        databaseReference.setValue(myDB);

                        // Start activity of edited course
                        Intent intent = new Intent(EditPostCourseActivity.this, PostGraduateCourseActivity.class);
                        intent.putExtra("Course Name", getIntent().getStringExtra("Course Name"));
                        intent.putExtra("Post", "true");
                        startActivity(intent);
                        finish();

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditPostCourseActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        // Hide keyboard if the user taps out of it
        findViewById(R.id.editPostCourseLL).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
                Intent intent_home = new Intent(EditPostCourseActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(EditPostCourseActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(EditPostCourseActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(EditPostCourseActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(EditPostCourseActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(EditPostCourseActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}