package com.example.csdbot.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.R;
import com.example.csdbot.components.Reminder;
import com.example.csdbot.viewholders.ReminderViewHolder;
import com.example.csdbot.components.User;
import com.example.csdbot.activities.EditReminderActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import static android.content.Context.ALARM_SERVICE;

public class RemindersListAdapter extends ArrayAdapter<Reminder> {

    private int layout, pos;
    private List<Reminder> myReminders;
    private DatabaseHelper myDB;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private User profileUser;
    private Context mContext;
    private Dialog editDialog, cancelDialog;


    public RemindersListAdapter(@NonNull Context context, int resource, @NonNull List<Reminder> objects) {
        super(context, resource, objects);
        myReminders = objects;
        layout = resource;
        mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        pos = position;
        ReminderViewHolder mainViewholder = null;

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Connect to the database and retrieve the user's data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                profileUser = myDB.getUserByUID(firebaseAuth.getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext() , databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        if ( convertView == null ) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            ReminderViewHolder viewHolder = new ReminderViewHolder();

            /* Get the viewHolder's views
             *      title: Reminder's username
             *      description: Reminder's description
             *      date: Reminder's date
             *      priority: Reminder's priority
             *      deleteThumbnail: Delete Reminder Button
             *      editThumbnail: Edit Reminder Button
             */
            viewHolder.title = (TextView) convertView.findViewById(R.id.reminderTitle);
            viewHolder.description = (TextView) convertView.findViewById(R.id.reminderDescription);
            viewHolder.date = (TextView) convertView.findViewById(R.id.reminderDateAndTime);
            viewHolder.priority = (TextView) convertView.findViewById(R.id.reminderPriority);
            viewHolder.deleteThumbnail = (ImageView) convertView.findViewById(R.id.deleteReminder);
            viewHolder.editThumbnail = (ImageView) convertView.findViewById(R.id.editReminder);

            // Set the reminder's data
            viewHolder.title.setText(myReminders.get(position).getName());
            viewHolder.description.setText("Description: " + myReminders.get(position).getDescription());
            viewHolder.date.setText(myReminders.get(position).getDay() + "/" +
                                    myReminders.get(position).getMonth() + "/" +
                                    myReminders.get(position).getYear() + " ");
            if ( myReminders.get(position).getHour() < 10 ){
                viewHolder.date.setText(viewHolder.date.getText().toString() + "0" + myReminders.get(position).getHour() + ":");
            } else {
                viewHolder.date.setText(viewHolder.date.getText().toString() + myReminders.get(position).getHour() + ":");
            }
            if ( myReminders.get(position).getMin() < 10 ){
                viewHolder.date.setText(viewHolder.date.getText().toString() + "0" + myReminders.get(position).getMin());
            } else {
                viewHolder.date.setText(viewHolder.date.getText().toString() + myReminders.get(position).getMin());
            }
            switch (myReminders.get(position).getReminder_priority()) {
                case low:
                    viewHolder.priority.setText(" Low");
                    viewHolder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_priority,0, 0, 0);
                    break;
                case mid:
                    viewHolder.priority.setText(" Medium");
                    viewHolder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.medium_priority,0, 0, 0);
                    break;
                case high:
                    viewHolder.priority.setText(" High");
                    viewHolder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.high_priority,0, 0, 0);
                    break;
                default:
                    viewHolder.priority.setText(" Low");
                    viewHolder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_priority,0, 0, 0);
                    break;
            }

            // Delete Reminder
            viewHolder.deleteThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView deletePopUpTitle;
                    Button delete, cancel;
                    cancelDialog = new Dialog(getContext());
                    cancelDialog.setContentView(R.layout.delete_reminder_confirmation);
                    deletePopUpTitle = (TextView) cancelDialog.findViewById(R.id.popUpDelTitle);
                    deletePopUpTitle.setText("Are you sure you wish to delete reminder \"" + profileUser.getReminderByID(getItem(position).getId()).getName() + "\"?");
                    delete = (Button) cancelDialog.findViewById(R.id.delete_confirmation_btn);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelAlarm(profileUser.getReminderPosition(profileUser.getReminderByID(getItem(position).getId()).getName()) );
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
                    editPopUpTitle.setText("Are you sure you wish to edit reminder \"" + profileUser.getUser_reminders().get(position).getName() + "\"?");
                    edit = (Button) editDialog.findViewById(R.id.edit_confirmation_btn);
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), EditReminderActivity.class);
                            intent.putExtra("remPosition", position);
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
            convertView.setTag(viewHolder);

            // Check if list is displayed in SearchResultsActivity or CalendarActivity
            // in order to remove the buttons
            if (((Activity) mContext).getIntent().hasExtra("SearchResultsActivity")) {
                if ( ( (Activity) mContext).getIntent().getStringExtra("SearchResultsActivity").equals("true") ) {
                    viewHolder.deleteThumbnail.setVisibility(View.GONE);
                    viewHolder.editThumbnail.setVisibility(View.GONE);
                }
            }

            if ( ( (Activity) mContext).getIntent().hasExtra("Calendar") ){
                if (( (Activity) mContext).getIntent().getStringExtra("Calendar").equals("true") ){
                    viewHolder.deleteThumbnail.setVisibility(View.GONE);
                    viewHolder.editThumbnail.setVisibility(View.GONE);
                }
            }
        } else {
            mainViewholder = (ReminderViewHolder) convertView.getTag();

            /* Get the viewHolder's views
             *      title: Reminder's username
             *      description: Reminder's description
             *      date: Reminder's date
             *      priority: Reminder's priority
             *      deleteThumbnail: Delete Reminder Button
             *      editThumbnail: Edit Reminder Button
             */
            mainViewholder.title = (TextView) convertView.findViewById(R.id.reminderTitle);
            mainViewholder.description = (TextView) convertView.findViewById(R.id.reminderDescription);
            mainViewholder.date = (TextView) convertView.findViewById(R.id.reminderDateAndTime);
            mainViewholder.priority = (TextView) convertView.findViewById(R.id.reminderPriority);
            mainViewholder.deleteThumbnail = (ImageView) convertView.findViewById(R.id.deleteReminder);
            mainViewholder.editThumbnail = (ImageView) convertView.findViewById(R.id.editReminder);

            // Set the reminder's data
            mainViewholder.title.setText(myReminders.get(position).getName());
            mainViewholder.description.setText("Description: " + myReminders.get(position).getDescription());
            mainViewholder.date.setText(myReminders.get(position).getDay() + "/" +
                    myReminders.get(position).getMonth() + "/" +
                    myReminders.get(position).getYear() + " ");
            if ( myReminders.get(position).getHour() < 10 ){
                mainViewholder.date.setText(mainViewholder.date.getText().toString() + "0" + myReminders.get(position).getHour() + ":");
            } else {
                mainViewholder.date.setText(mainViewholder.date.getText().toString() + myReminders.get(position).getHour() + ":");
            }
            if ( myReminders.get(position).getMin() < 10 ){
                mainViewholder.date.setText(mainViewholder.date.getText().toString() + "0" + myReminders.get(position).getMin());
            } else {
                mainViewholder.date.setText(mainViewholder.date.getText().toString() + myReminders.get(position).getMin());
            }
            switch (myReminders.get(position).getReminder_priority()) {
                case low:
                    mainViewholder.priority.setText(" Low");
                    mainViewholder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_priority,0, 0, 0);
                    break;
                case mid:
                    mainViewholder.priority.setText(" Medium");
                    mainViewholder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.medium_priority,0, 0, 0);
                    break;
                case high:
                    mainViewholder.priority.setText(" High");
                    mainViewholder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.high_priority,0, 0, 0);
                    break;
                default:
                    mainViewholder.priority.setText(" Low");
                    mainViewholder.priority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.low_priority,0, 0, 0);
                    break;
            }

            // Delete reminder
            mainViewholder.deleteThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView deletePopUpTitle;
                    Button delete, cancel;
                    cancelDialog = new Dialog(getContext());
                    cancelDialog.setContentView(R.layout.delete_reminder_confirmation);
                    deletePopUpTitle = (TextView) cancelDialog.findViewById(R.id.popUpDelTitle);
                    deletePopUpTitle.setText("Are you sure you wish to delete reminder \"" + profileUser.getUser_reminders().get(position).getName() + "\"?");
                    delete = (Button) cancelDialog.findViewById(R.id.delete_confirmation_btn);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelAlarm(profileUser.getReminderPosition(profileUser.getUser_reminders().get(position).getName()));
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
                    editPopUpTitle.setText("Are you sure you wish to edit reminder \"" + profileUser.getUser_reminders().get(position).getName() + "\"?");
                    edit = (Button) editDialog.findViewById(R.id.edit_confirmation_btn);
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), EditReminderActivity.class);
                            intent.putExtra("remPosition", position);
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

            // Check if list is displayed in SearchResultsActivity or CalendarActivity
            // in order to remove the buttons
            if (((Activity) mContext).getIntent().hasExtra("SearchResultsActivity")) {
                if ( ( (Activity) mContext).getIntent().getStringExtra("SearchResultsActivity").equals("true") ) {
                    mainViewholder.deleteThumbnail.setVisibility(View.GONE);
                    mainViewholder.editThumbnail.setVisibility(View.GONE);
                }
            }

            if ( ( (Activity) mContext).getIntent().hasExtra("Calendar") ){
                if (( (Activity) mContext).getIntent().getStringExtra("Calendar").equals("true") ){
                    mainViewholder.deleteThumbnail.setVisibility(View.GONE);
                    mainViewholder.editThumbnail.setVisibility(View.GONE);
                }
            }
        }
        return convertView;
    }

    /**
     * Cancel the alarm of the deleted reminder
     *
     * @param pos the position of the reminder in the user's reminder list
     */
    public void cancelAlarm(int pos) {
        // Intent
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("notificationId", profileUser.getUser_reminders().get(pos).getId());
        intent.putExtra("name", profileUser.getUser_reminders().get(pos).getName());
        intent.putExtra("message", profileUser.getUser_reminders().get(pos).getDescription());
        switch ( profileUser.getUser_reminders().get(pos).getReminder_priority()){
            case low:
                intent.putExtra("priority", "low");
                break;
            case mid:
                intent.putExtra("priority", "mid");
                break;
            case high:
                intent.putExtra("priority", "high");
                break;
            default:
                intent.putExtra("priority", "none");
                break;
        }
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        // PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(), profileUser.getUser_reminders().get(pos).getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        // AlarmManager
        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(ALARM_SERVICE);

        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
        profileUser.deleteUser_reminder(pos);

    }

}
