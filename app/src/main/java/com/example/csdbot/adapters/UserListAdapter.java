package com.example.csdbot.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.csdbot.components.Course;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.R;
import com.example.csdbot.components.PostGraduateCourse;
import com.example.csdbot.components.User;
import com.example.csdbot.viewholders.UserViewHolder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {
    private int layout;
    private List<User> userList;
    private DatabaseHelper myDB;
    private Course undergraduateCourse;
    private PostGraduateCourse postGraduateCourse;
    private Context mContext;
    private boolean isTeacher = false;

    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        layout = resource;
        userList = objects;
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Initialize firebase components
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        final String courseName = ((Activity) mContext).getIntent().getStringExtra("Course Name");
        final String post = ((Activity) mContext).getIntent().getStringExtra("Post");
        // Connect to the database and get users list
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if (myDB != null) {
                    userList = myDB.getUserList();
                    if ( post != null){
                        if ( post.equals("false")){
                            undergraduateCourse = myDB.getCourseByName(courseName);
                            if ( undergraduateCourse.getTeacherUID().equals(userList.get(position).getUID()) ){
                                userList.get(position).setTeachingCourse(courseName);
                                databaseReference.setValue(myDB);
                            }
                        } else {
                            postGraduateCourse = myDB.getPostGraduateCourseByName(courseName);
                            if ( postGraduateCourse.getTeacherUID().equals(userList.get(position).getUID()) ){
                                userList.get(position).setTeachingCourse(courseName);
                                databaseReference.setValue(myDB);
                            }
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext() , databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        if ( convertView == null ) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            final UserViewHolder viewHolder = new UserViewHolder();
            /* Get the viewHolder's views
             *      userName: User's username
             *      userImage: User's profile picture
             *      userEmail: User's email
             */
            viewHolder.userName = convertView.findViewById(R.id.teacherName);
            viewHolder.userEmail = convertView.findViewById(R.id.teacherEmail);
            viewHolder.userImage = convertView.findViewById(R.id.userListItemPicture);

            // Set user's data
            viewHolder.userName.setText(userList.get(position).getName());
            if ( userList.get(position).isAdmin() ){
                String name = viewHolder.userName.getText().toString() + " (admin)";
                if ( userList.get(position).getTeachingCourse().equals(courseName) ){
                    name = name + " (Teacher)";
                }
                viewHolder.userName.setText(name);
            }
            String email = "Email: " + userList.get(position).getEmail();
            viewHolder.userEmail.setText(email);
            storageReference.child(userList.get(position).getUID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(viewHolder.userImage);
                }
            });
        }
        return convertView;
    }
}
