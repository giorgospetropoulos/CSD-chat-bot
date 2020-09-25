package com.example.csdbot.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.csdbot.AlarmReceiver;
import com.example.csdbot.components.Course;
import com.example.csdbot.viewholders.CourseReminderViewHolder;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.components.PostGraduateCourse;
import com.example.csdbot.R;
import com.example.csdbot.components.Reminder;
import com.example.csdbot.components.User;
import com.example.csdbot.activities.EditCourseReminderActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.List;
import static android.content.Context.ALARM_SERVICE;

public class CourseReminderListAdapter extends ArrayAdapter<Reminder> {
    private int layout;
    private List<Reminder> courseReminders;
    private DatabaseHelper myDB;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference courseReference;
    private User profileUser;
    private Course course;
    private PostGraduateCourse post_course;
    private Context mContext;
    private Dialog editDialog, cancelDialog, addDialog;


    public CourseReminderListAdapter(@NonNull Context context, int resource, @NonNull List<Reminder> objects) {
        super(context, resource, objects);
        courseReminders = objects;
        layout = resource;
        mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        CourseReminderViewHolder mainViewholder = null;
        final CourseReminderViewHolder viewHolder = new CourseReminderViewHolder();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Connect to the database and retrieve the course's reminders
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                profileUser = myDB.getUserByUID(firebaseAuth.getUid());

                // Check if user is admin or teacher to the course
                // and set the components' visibility accordingly
                if ( ( (Activity) mContext).getIntent().getStringExtra("Post").equals("true") ){
                    post_course = myDB.getPostGraduateCourseByName(( (Activity) mContext).getIntent().getStringExtra("Course Name"));
                    if ( post_course.getTeacherUID() == firebaseAuth.getUid() || profileUser.isAdmin() ){
                        viewHolder.deleteThumbnail.setVisibility(View.VISIBLE);
                        viewHolder.editThumbnail.setVisibility(View.VISIBLE);
                    }
                    if ( !profileUser.hasReminderByID(courseReminders.get(position).getId()) ){
                        viewHolder.addThumbnail.setVisibility(View.VISIBLE);
                    }
                } else {
                    course = myDB.getCourseByName(( (Activity) mContext).getIntent().getStringExtra("Course Name"));
                    if ( course.getTeacherUID() == firebaseAuth.getUid() || profileUser.isAdmin() ){
                        viewHolder.deleteThumbnail.setVisibility(View.VISIBLE);
                        viewHolder.editThumbnail.setVisibility(View.VISIBLE);
                    }
                    if ( !profileUser.hasReminderByID(courseReminders.get(position).getId()) ){
                        viewHolder.addThumbnail.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        //------------------------
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);

            /* Get the viewHolder's views
             *      title: Reminder's username
             *      description: Reminder's description
             *      date: Reminder's date
             *      priority: Reminder's priority
             *      deleteThumbnail: Delete Reminder Button
             *      editThumbnail: Edit Reminder Button
             *      addThumbnail: Add Reminder Button
             */
            viewHolder.title = (TextView) convertView.findViewById(R.id.courseReminderTitle);
            viewHolder.description = (TextView) convertView.findViewById(R.id.courseReminderDescription);
            viewHolder.date = (TextView) convertView.findViewById(R.id.courseReminderDateAndTime);
            viewHolder.priority = (TextView) convertView.findViewById(R.id.courseReminderPriority);
            viewHolder.deleteThumbnail = (ImageView) convertView.findViewById(R.id.deleteCourseReminder);
            viewHolder.editThumbnail = (ImageView) convertView.findViewById(R.id.editCourseReminder);
            viewHolder.addThumbnail = (ImageView)  convertView.findViewById(R.id.addCourseReminder);

            // Set the reminder's data
            viewHolder.title.setText(courseReminders.get(position).getName());
            viewHolder.description.setText(courseReminders.get(position).getDescription());
            viewHolder.date.setText(courseReminders.get(position).getDay() + "/" +
                    courseReminders.get(position).getMonth() + "/" +
                    courseReminders.get(position).getYear() + " ");
            if ( courseReminders.get(position).getHour() < 10 ){
                viewHolder.date.setText(viewHolder.date.getText().toString() + "0" + courseReminders.get(position).getHour() + ":");
            } else {
                viewHolder.date.setText(viewHolder.date.getText().toString() + courseReminders.get(position).getHour() + ":");
            }
            if ( courseReminders.get(position).getMin() < 10 ){
                viewHolder.date.setText(viewHolder.date.getText().toString() + "0" + courseReminders.get(position).getMin());
            } else {
                viewHolder.date.setText(viewHolder.date.getText().toString() + courseReminders.get(position).getMin());
            }
            switch (courseReminders.get(position).getReminder_priority()) {
                case low:
                    viewHolder.priority.setText(" Low");
                    viewHolder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_priority, 0, 0, 0);
                    break;
                case mid:
                    viewHolder.priority.setText(" Medium");
                    viewHolder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.medium_priority, 0, 0, 0);
                    break;
                case high:
                    viewHolder.priority.setText(" High");
                    viewHolder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.high_priority, 0, 0, 0);
                    break;
                default:
                    viewHolder.priority.setText(" Low");
                    viewHolder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_priority, 0, 0, 0);
                    break;
            }

            // Delete reminder
            viewHolder.deleteThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView deletePopUpTitle;
                    Button delete, cancel;
                    cancelDialog = new Dialog(getContext());
                    cancelDialog.setContentView(R.layout.delete_reminder_confirmation);
                    deletePopUpTitle = (TextView) cancelDialog.findViewById(R.id.popUpDelTitle);
                    deletePopUpTitle.setText("Are you sure you wish to delete reminder \"" + courseReminders.get(position).getName() + "\"?");
                    delete = (Button) cancelDialog.findViewById(R.id.delete_confirmation_btn);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ( ( (Activity) mContext).getIntent().getStringExtra("Post").equals("true") ){
                                post_course.deleteCourseReminder(position);
                            } else {
                                course.deleteCourseReminder(position);
                            }
                            databaseReference.setValue(myDB);
                            cancelDialog.dismiss();
                        }
                    });
                    cancel = (Button) cancelDialog.findViewById(R.id.cancel_delete_reminder);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelDialog.dismiss();
                        }
                    });
                    cancelDialog.show();
                }
            });

            // Edit Reminder
            viewHolder.editThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView editPopUpTitle;
                    Button edit, cancel;
                    editDialog = new Dialog(getContext());
                    editDialog.setContentView(R.layout.edit_reminder_confirmation);
                    editPopUpTitle = (TextView) editDialog.findViewById(R.id.popUpTitle);
                    if ( ( (Activity) mContext).getIntent().getStringExtra("Post").equals("true") ){
                        editPopUpTitle.setText("Are you sure you wish to edit reminder \"" +
                                courseReminders.get(position).getName() +
                                "\" " + " of " +
                                post_course.getName_en()+ "\"?");
                    } else {
                        editPopUpTitle.setText("Are you sure you wish to edit reminder \"" +
                                courseReminders.get(position).getName() +
                                "\" " + " of " +
                                course.getName_en()+ "\"?");
                    }
                    edit = (Button) editDialog.findViewById(R.id.edit_confirmation_btn);
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), EditCourseReminderActivity.class);
                            intent.putExtra("remPosition", position);
                            if ( ( (Activity) mContext).getIntent().getStringExtra("Post").equals("true") ){
                                intent.putExtra("courseName", post_course.getName_en());
                                intent.putExtra("Post", "true");
                            } else {
                                intent.putExtra("courseName", course.getName_en());
                                intent.putExtra("Post", "false");
                            }
                            getContext().startActivity(intent);
                            editDialog.dismiss();
                        }
                    });
                    cancel = (Button) editDialog.findViewById(R.id.cancel_edit);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDialog.dismiss();
                        }
                    });
                    editDialog.show();
                }
            });

            // Add Reminder
            viewHolder.addThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView addPopUpTitle;
                    Button add, cancel;
                    addDialog = new Dialog(getContext());
                    addDialog.setContentView(R.layout.add_course_reminder_confirmation);
                    addPopUpTitle = (TextView) addDialog.findViewById(R.id.popUpAddTitle);
                    addPopUpTitle.setText("Are you sure you wish to add reminder \"" +
                            courseReminders.get(position).getName() +
                            "\" to your reminders?");
                    add = (Button) addDialog.findViewById(R.id.add_confirmation_btn);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            profileUser = myDB.getUserByUID(firebaseAuth.getUid());
                            //profileUser.addUser_reminders(new Reminder(course.getCourseReminder(position).getName(),course.getCourseReminder(position).getDay(),course.getCourseReminder(position).getMonth(),course.getCourseReminder(position).getYear(),course.getCourseReminder(position).getHour(),course.getCourseReminder(position).getMin(),course.getCourseReminder(position).getReminder_priority()));
                            if ( ( (Activity) mContext).getIntent().getStringExtra("Post").equals("true") ){
                                profileUser.addUser_reminders(post_course.getCourseReminder(position));
                                setAlarm(post_course.getCourseReminder(position));
                            } else {
                                profileUser.addUser_reminders(course.getCourseReminder(position));
                                setAlarm(course.getCourseReminder(position));
                            }
                            databaseReference.setValue(myDB);
                            addDialog.dismiss();
                        }
                    });
                    cancel = (Button) addDialog.findViewById(R.id.cancel_add);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addDialog.dismiss();
                        }
                    });
                    addDialog.show();
                }
            });

            convertView.setTag(viewHolder);

        } else {
            mainViewholder = (CourseReminderViewHolder) convertView.getTag();

            /* Get the viewHolder's views
             *      title: Reminder's username
             *      description: Reminder's description
             *      date: Reminder's date
             *      priority: Reminder's priority
             *      deleteThumbnail: Delete Reminder Button
             *      editThumbnail: Edit Reminder Button
             *      addThumbnail: Add Reminder Button
             */
            mainViewholder.title = (TextView) convertView.findViewById(R.id.courseReminderTitle);
            mainViewholder.description = (TextView) convertView.findViewById(R.id.courseReminderDescription);
            mainViewholder.date = (TextView) convertView.findViewById(R.id.courseReminderDateAndTime);
            mainViewholder.priority = (TextView) convertView.findViewById(R.id.courseReminderPriority);
            mainViewholder.deleteThumbnail = (ImageView) convertView.findViewById(R.id.deleteCourseReminder);
            mainViewholder.editThumbnail = (ImageView) convertView.findViewById(R.id.editCourseReminder);
            mainViewholder.addThumbnail = (ImageView)  convertView.findViewById(R.id.addCourseReminder);

            // Set the reminder's data
            mainViewholder.title.setText(courseReminders.get(position).getName());
            mainViewholder.description.setText(courseReminders.get(position).getDescription());
            mainViewholder.date.setText(courseReminders.get(position).getDay() + "/" +
                    courseReminders.get(position).getMonth() + "/" +
                    courseReminders.get(position).getYear() + " ");
            if ( courseReminders.get(position).getHour() < 10 ){
                mainViewholder.date.setText(mainViewholder.date.getText().toString() + "0" + courseReminders.get(position).getHour() + ":");
            } else {
                mainViewholder.date.setText(mainViewholder.date.getText().toString() + courseReminders.get(position).getHour() + ":");
            }
            if ( courseReminders.get(position).getMin() < 10 ){
                mainViewholder.date.setText(mainViewholder.date.getText().toString() + "0" + courseReminders.get(position).getMin());
            } else {
                mainViewholder.date.setText(mainViewholder.date.getText().toString() + courseReminders.get(position).getMin());
            }
            switch (courseReminders.get(position).getReminder_priority()) {
                case low:
                    mainViewholder.priority.setText(" Low");
                    mainViewholder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_priority, 0, 0, 0);
                    break;
                case mid:
                    mainViewholder.priority.setText(" Medium");
                    mainViewholder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.medium_priority, 0, 0, 0);
                    break;
                case high:
                    mainViewholder.priority.setText(" High");
                    mainViewholder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.high_priority, 0, 0, 0);
                    break;
                default:
                    mainViewholder.priority.setText(" Low");
                    mainViewholder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_priority, 0, 0, 0);
                    break;
            }

            // Delete Reminder
            mainViewholder.deleteThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView deletePopUpTitle;
                    Button delete, cancel;
                    cancelDialog = new Dialog(getContext());
                    cancelDialog.setContentView(R.layout.delete_reminder_confirmation);
                    deletePopUpTitle = (TextView) cancelDialog.findViewById(R.id.popUpDelTitle);
                    deletePopUpTitle.setText("Are you sure you wish to delete reminder \"" + courseReminders.get(position).getName() + "\"?");
                    delete = (Button) cancelDialog.findViewById(R.id.delete_confirmation_btn);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ( ( (Activity) mContext).getIntent().getStringExtra("Post").equals("true") ){
                                post_course.deleteCourseReminder(position);
                            } else {
                                course.deleteCourseReminder(position);
                            }
                            databaseReference.setValue(myDB);
                            cancelDialog.dismiss();
                        }
                    });
                    cancel = (Button) cancelDialog.findViewById(R.id.cancel_delete_reminder);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelDialog.dismiss();
                        }
                    });
                    cancelDialog.show();
                }
            });

            // Edit Reminder
            mainViewholder.editThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView editPopUpTitle;
                    Button edit, cancel;
                    editDialog = new Dialog(getContext());
                    editDialog.setContentView(R.layout.edit_reminder_confirmation);
                    editPopUpTitle = (TextView) editDialog.findViewById(R.id.popUpTitle);
                    if ( ( (Activity) mContext).getIntent().getStringExtra("Post").equals("true") ){
                        editPopUpTitle.setText("Are you sure you wish to edit reminder \"" +
                                courseReminders.get(position).getName() +
                                "\" " + " of " +
                                post_course.getName_en()+ "\"?");
                    } else {
                        editPopUpTitle.setText("Are you sure you wish to edit reminder \"" +
                                courseReminders.get(position).getName() +
                                "\" " + " of " +
                                course.getName_en()+ "\"?");
                    }
                    edit = (Button) editDialog.findViewById(R.id.edit_confirmation_btn);
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), EditCourseReminderActivity.class);
                            intent.putExtra("remPosition", position);
                            if ( ( (Activity) mContext).getIntent().getStringExtra("Post").equals("true") ){
                                intent.putExtra("courseName", post_course.getName_en());
                                intent.putExtra("Post", "true");
                            } else {
                                intent.putExtra("courseName", course.getName_en());
                                intent.putExtra("Post", "false");
                            }
                            getContext().startActivity(intent);
                            editDialog.dismiss();
                        }
                    });
                    cancel = (Button) editDialog.findViewById(R.id.cancel_edit);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDialog.dismiss();
                        }
                    });
                    editDialog.show();
                }
            });

            // Add Reminder
            mainViewholder.addThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView addPopUpTitle;
                    Button add, cancel;
                    addDialog = new Dialog(getContext());
                    addDialog.setContentView(R.layout.add_course_reminder_confirmation);
                    addPopUpTitle = (TextView) addDialog.findViewById(R.id.popUpAddTitle);
                    addPopUpTitle.setText("Are you sure you wish to add reminder \"" +
                            courseReminders.get(position).getName() +
                            "\" to your reminders?");
                    add = (Button) addDialog.findViewById(R.id.add_confirmation_btn);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            profileUser = myDB.getUserByUID(firebaseAuth.getUid());
                            if ( ( (Activity) mContext).getIntent().getStringExtra("Post").equals("true") ){
                                profileUser.addUser_reminders(post_course.getCourseReminder(position));
                                setAlarm(post_course.getCourseReminder(position));
                            } else {
                                profileUser.addUser_reminders(course.getCourseReminder(position));
                                setAlarm(course.getCourseReminder(position));
                            }
                            databaseReference.setValue(myDB);
                            addDialog.dismiss();
                        }
                    });
                    cancel = (Button) addDialog.findViewById(R.id.cancel_add);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addDialog.dismiss();
                        }
                    });
                    addDialog.show();
                }
            });
        }
        return convertView;
    }

    /**
     * Create event for reminder and use pending intent and a broadcoast
     * receiver to set the notification to the given time.
     *
     * @param reminder the reminder to be set
     */
    public void setAlarm(Reminder reminder){
        // Intent
        Intent reminderIntent = new Intent(mContext, AlarmReceiver.class);
        reminderIntent.putExtra("notificationId", reminder.getId());
        reminderIntent.putExtra("name", reminder.getName());
        reminderIntent.putExtra("message", reminder.getDescription());
        switch (reminder.getReminder_priority()){
            case low:
                reminderIntent.putExtra("priority", "low");
                break;
            case mid:
                reminderIntent.putExtra("priority", "mid");
                break;
            case high:
                reminderIntent.putExtra("priority", "high");
                break;
            default:
                reminderIntent.putExtra("priority", "none");
                break;
        }
        reminderIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        // PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext, reminder.getId(), reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        // AlarmManager
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, reminder.getHour());
        c.set(Calendar.MINUTE, reminder.getMin());
        c.set(Calendar.SECOND,0);
        c.set(Calendar.YEAR, reminder.getYear());
        c.set(Calendar.MONTH, reminder.getMonth()-1);
        c.set(Calendar.DATE, reminder.getDay());

        // Set Alarm
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    c.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
    }


}
