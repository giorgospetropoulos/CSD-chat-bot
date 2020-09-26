package com.example.csdbot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import java.util.Calendar;
import java.util.Locale;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.vo.DateData;

public class CalendarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ListView lv;
    private ArrayAdapter<Reminder> adapter;
    private MCalendarView calendar;
    private TextView  calendarTitle;
    private FirebaseAuth firebaseAuth;
    private User profileUser;
    private ArrayList<Reminder> reminderList;
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
        setContentView(R.layout.activity_calendar);
        loading = ProgressDialog.show(CalendarActivity.this, "",
                "Loading. Please wait...", true);

        // ---------- Slide Menu --------------
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Calendar");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // ------------------------------------

        /* Find the activity's views
         *      add: The add new reminder button
         *      lv: The ListView of the selected date's reminders
         *      calendar: The CalendarView
         *      calendarTitle: The Calendar Title (Month and Year)
         */
        ImageView add = findViewById(R.id.addReminder);
        lv = findViewById(R.id.calendarReminderListView);
        calendar = findViewById(R.id.calendarView);
        calendarTitle = findViewById(R.id.calendarTitle);

        // Set CalendarView Title
        calendar.hasTitle(false);
        String calendar_title = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                + " " + Calendar.getInstance().get(Calendar.YEAR);
        calendarTitle.setText(calendar_title);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        /*
         *      -----   Set Event Listeners     -----
         */

        // Start activity of clicked reminder
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Reminder temp = adapter.getItem(position);
                Intent intent = new Intent(CalendarActivity.this, ReminderActivity.class);
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

        // Start activity to add new reminder
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAdd();
            }
        });

        // Change the Calendar title accordingly
        calendar.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                switch (month){
                    case 1:
                        String January = "January " + year;
                        calendarTitle.setText(January);
                        break;
                    case 2:
                        String February = "February " + year;
                        calendarTitle.setText(February);
                        break;
                    case 3:
                        String March = "March " + year;
                        calendarTitle.setText(March);
                        break;
                    case 4:
                        String April = "April " + year;
                        calendarTitle.setText(April);
                        break;
                    case 5:
                        String May = "May " + year;
                        calendarTitle.setText(May);
                        break;
                    case 6:
                        String June = "June " + year;
                        calendarTitle.setText(June);
                        break;
                    case 7:
                        String July = "July " + year;
                        calendarTitle.setText(July);
                        break;
                    case 8:
                        String August = "August " + year;
                        calendarTitle.setText(August);
                        break;
                    case 9:
                        String September = "September " + year;
                        calendarTitle.setText(September);
                        break;
                    case 10:
                        String October = "October " + year;
                        calendarTitle.setText(October);
                        break;
                    case 11:
                        String November = "November " + year;
                        calendarTitle.setText(November);
                        break;
                    case 12:
                        String December = "December " + year;
                        calendarTitle.setText(December);
                        break;
                    default:
                        break;
                }
            }
        });

        // Get selected date and display selected date's reminders
        calendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                ArrayList<Reminder> dateReminders = new ArrayList<Reminder>();
                String clickedDate = Integer.toString(date.getYear()) +
                        date.getMonth() +
                        date.getDay();
                for(int i = 0 ; i < reminderList.size() ; i++){
                    String tempDate = Integer.toString(reminderList.get(i).getYear()) +
                            reminderList.get(i).getMonth() +
                            reminderList.get(i).getDay();
                    if ( tempDate.equals(clickedDate) ) {
                        dateReminders.add(reminderList.get(i));
                    }
                }

                adapter = new RemindersListAdapter(CalendarActivity.this, R.layout.reminders_list_item, dateReminders);
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });


        // Connect to database and retrieve the user's reminders
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if (myDB != null) {
                    profileUser = myDB.getUserByUID(firebaseAuth.getUid());
                }
                reminderList = profileUser.getUser_reminders();

                for ( int i = 0 ; i < profileUser.getUser_reminders().size() ; i++) {
                    if ( profileUser.getUser_reminders().get(i).getReminder_priority() == Reminder.priority.low){
                        calendar.markDate(new DateData(profileUser.getUser_reminders().get(i).getYear(), profileUser.getUser_reminders().get(i).getMonth(), profileUser.getUser_reminders().get(i).getDay()).setMarkStyle(new MarkStyle(MarkStyle.DOT, Color.GREEN)));
                    } else if ( profileUser.getUser_reminders().get(i).getReminder_priority() == Reminder.priority.mid){
                        calendar.markDate(new DateData(profileUser.getUser_reminders().get(i).getYear(), profileUser.getUser_reminders().get(i).getMonth(), profileUser.getUser_reminders().get(i).getDay()).setMarkStyle(new MarkStyle(MarkStyle.DOT, Color.YELLOW)));
                    } else if ( profileUser.getUser_reminders().get(i).getReminder_priority() == Reminder.priority.high){
                        calendar.markDate(new DateData(profileUser.getUser_reminders().get(i).getYear(), profileUser.getUser_reminders().get(i).getMonth(), profileUser.getUser_reminders().get(i).getDay()).setMarkStyle(new MarkStyle(MarkStyle.DOT, Color.RED)));
                    }
                }

                loading.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CalendarActivity.this , databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *  Go to Add New Reminder Activity
     */
    public void goToAdd(){
        Intent intent = new Intent(this, AddReminderActivity.class);
        startActivity(intent);
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
                Intent intent_home = new Intent(CalendarActivity.this, HomeActivity.class);
                startActivity(intent_home);
                return true;
            case R.id.nav_profile:
                Intent intent_profile = new Intent(CalendarActivity.this, ProfileActivity.class);
                startActivity(intent_profile);
                return true;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(CalendarActivity.this, MyCoursesActivity.class);
                startActivity(intent_courses);
                return true;
            case R.id.nav_reminders:
                Intent intent_reminders = new Intent(CalendarActivity.this, RemindersListActivity.class);
                startActivity(intent_reminders);
                return true;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(CalendarActivity.this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                finish();
                Intent returnToSignUp = new Intent(CalendarActivity.this, MainActivity.class);
                startActivity(returnToSignUp);
                return true;
            default:
                return false;
        }
    }

    // --------------------------------------------------------------------------------------------
}
