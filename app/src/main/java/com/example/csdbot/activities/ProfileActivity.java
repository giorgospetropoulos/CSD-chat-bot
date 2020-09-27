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
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.csdbot.components.Course;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.components.PostGraduateCourse;
import com.example.csdbot.R;
import com.example.csdbot.components.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView profileName;
    private ImageView profileImage;
    private Button updateCourses, setAdmin, addNewCourse;
    private FirebaseAuth firebaseAuth;
    private DatabaseHelper myDB;
    private ArrayList<Course> courseList = new ArrayList<Course>();
    private ArrayList<PostGraduateCourse> postGraduateCourseList = new ArrayList<PostGraduateCourse>();
    private ArrayList<Course> teachingCourseList = new ArrayList<Course>();
    private ArrayList<PostGraduateCourse> teachingPostGraduateCourseList = new ArrayList<PostGraduateCourse>();
    private DatabaseReference databaseReference;
    private ProgressDialog loading;
    private Dialog chooseCourseType;
    private User profileUser;

    // ---------- Slide Menu --------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    // ------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        /* Find the activity's views
         *      profileName: The user's username
         *      profileImage: The user's profile picture
         *      profileCourses: Go to user's courses button
         *      profileReminders: Go to user's reminder button
         *      profileDetails: Go to user's profile details button
         *      profileContacts: Go to user's contacts
         *      updateCourses: Update Courses Button
         *      setAdmin: Set Admin Button
         *      addNewCourse: Add New Course Button
         */
        profileName = findViewById(R.id.profileName);
        profileImage = findViewById(R.id.profileImage);
        Button profileCourses = findViewById(R.id.profileCourses);
        Button profileReminders = findViewById(R.id.profileReminders);
        Button profileDetails = findViewById(R.id.profileDetails);
        Button profileContacts = findViewById(R.id.profileContacts);
        final Button teachingCourses = findViewById(R.id.viewTeachingCoursesBtn);
        updateCourses = findViewById(R.id.populate);
        setAdmin = findViewById(R.id.setAdmin);
        addNewCourse = findViewById(R.id.addNewCourse);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference("Database");
        StorageReference storageReference = firebaseStorage.getReference();
        loading = ProgressDialog.show(ProfileActivity.this, "",
                "Loading. Please wait...", true);
        storageReference.child(firebaseAuth.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        // Connect to the database and retrieve the user's data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if (myDB != null) {
                    profileUser = myDB.getUserByUID(firebaseAuth.getUid());
                    profileName.setText(profileUser.getName());
                    courseList = myDB.getCourseList();
                    teachingCourseList = profileUser.getTeaching_courses();
                    teachingPostGraduateCourseList = profileUser.getPostgraduate_teaching_courses();
                }
                loading.dismiss();
                if ( profileUser.isAdmin() || profileUser.getName().equals("admin")){
                    updateCourses.setVisibility(View.VISIBLE);
                    setAdmin.setVisibility(View.VISIBLE);
                    addNewCourse.setVisibility(View.VISIBLE);
                }

                if ( !teachingCourseList.isEmpty() || !teachingPostGraduateCourseList.isEmpty()){
                    teachingCourses.setVisibility(View.VISIBLE);
                }


                updateCourses.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getUndergraduteCoursesJSON();
                        getPostgraduteCoursesJSON();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        // Go to user's courses
        profileCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCourses = new Intent(ProfileActivity.this, MyCoursesActivity.class);
                startActivity(goToCourses);
            }
        });

        // Go to user's reminder
        profileReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCourses = new Intent(ProfileActivity.this, RemindersListActivity.class);
                startActivity(goToCourses);
            }
        });

        // Go to user's profile details
        profileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCourses = new Intent(ProfileActivity.this, ProfileDetailsActivity.class);
                startActivity(goToCourses);
            }
        });

        // Go to user's contacts
        profileContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToContacts = new Intent(ProfileActivity.this, ContactsActivity.class);
                startActivity(goToContacts);
            }
        });

        // Go to user's teaching courses
        teachingCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, TeachingCoursesActivity.class);
                startActivity(intent);
            }
        });

        // Go to Set Admin Activity
        setAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSetAdmin = new Intent(ProfileActivity.this, SetAdminActivity.class);
                startActivity(goToSetAdmin);
            }
        });

        // Go to Add new course activity
        addNewCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView addAllTitle;
                Button undergraduate, postgarduate;
                chooseCourseType = new Dialog(ProfileActivity.this);
                chooseCourseType.setContentView(R.layout.add_all_reminders_confirmation);
                addAllTitle = chooseCourseType.findViewById(R.id.addAllConfirmationTitle);
                String title = "Do you wish to add a new undergraduate or postgraduate course? ";
                addAllTitle.setText(title);
                undergraduate = chooseCourseType.findViewById(R.id.addAllBtn);
                String under = "Undergraduate";
                undergraduate.setText(under);
                postgarduate = chooseCourseType.findViewById(R.id.cancelAddAll);
                String post = "Postgraduate";
                postgarduate.setText(post);
                undergraduate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ProfileActivity.this, AddUndergraduateCourseActivity.class);
                        startActivity(intent);
                        chooseCourseType.dismiss();
                    }
                });
                postgarduate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ProfileActivity.this, AddPostgraduateCourseActivity.class);
                        startActivity(intent);
                        chooseCourseType.dismiss();
                    }
                });
                chooseCourseType.show();
            }
        });



    }

    /**
     * Get all undergraduate courses via JSON file
     */
    public void getUndergraduteCoursesJSON(){
        String json;
        try {
            InputStream is = getAssets().open("courses_undergraduate.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);
            for ( int i = 0 ; i < jsonArray.length() ; i++ ){
                JSONObject obj = jsonArray.getJSONObject(i);
                Course course = new Course(obj.getString("code_en"),
                        obj.getString("name_en"),
                        obj.getString("description_en"),
                        obj.getString("area_code_en"),
                        obj.getString("url"),
                        obj.getString("area_name_en"),
                        obj.getString("code_gr"),
                        obj.getString("name_gr"),
                        obj.getString("description_gr"),
                        obj.getString("area_code_gr"),
                        obj.getString("area_name_gr"),
                        obj.getString("ects"));
                if ( !myDB.hasCourse(course) ){
                    courseList.add(course);
                }
            }
            Collections.sort(courseList, Course.CourseComparator);
            databaseReference.setValue(myDB);
        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Get all postgraduate courses via JSON file
     */
    public void getPostgraduteCoursesJSON(){
        String json;
        try {
            InputStream is = getAssets().open("courses_postgraduate.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);
            for ( int i = 0 ; i < jsonArray.length() ; i++ ){
                JSONObject obj = jsonArray.getJSONObject(i);
                PostGraduateCourse course = new PostGraduateCourse(obj.getString("code_en"),
                        obj.getString("name_en"),
                        obj.getString("description_en"),
                        obj.getString("area_code_en"),
                        obj.getString("url"),
                        obj.getString("area_name_en"),
                        obj.getString("code_gr"),
                        obj.getString("name_gr"),
                        obj.getString("description_gr"),
                        obj.getString("area_code_gr"),
                        obj.getString("area_name_gr"),
                        obj.getString("ects"));
                if ( myDB.hasCourse(course) ){
                    PostGraduateCourse temp = myDB.getPostGraduateCourseByName(course.getName_en());
                    if ( !temp.getArea_codes_en().isEmpty() ) {
                        if ( !temp.getArea_codes_en().contains(obj.getString("area_code_en")) ){
                            temp.getArea_codes_en().add(obj.getString("area_code_en"));
                        }
                    }
                    if ( !temp.getArea_codes_gr().isEmpty() ) {
                        if ( !temp.getArea_codes_gr().contains(obj.getString("area_code_gr")) ){
                            temp.getArea_codes_gr().add(obj.getString("area_code_gr"));
                        }
                    }
                    if ( !temp.getArea_names_en().isEmpty() ) {
                        if ( !temp.getArea_names_en().contains(obj.getString("area_name_en")) ){
                            temp.getArea_names_en().add(obj.getString("area_name_en"));
                        }
                    }
                    if ( !temp.getArea_names_en().isEmpty() ) {
                        if ( !temp.getArea_names_gr().contains(obj.getString("area_name_gr")) ){
                            temp.getArea_names_gr().add(obj.getString("area_name_gr"));
                        }
                    }
                } else {
                    myDB.getPostGraduateCourseList().add(course);
                }
            }
            Collections.sort(postGraduateCourseList, Course.CourseComparator);
            databaseReference.setValue(myDB);
        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }
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
                Intent intent_home = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(ProfileActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(ProfileActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(ProfileActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }
    // -------------------------------------------------------------------------------------------------
}
