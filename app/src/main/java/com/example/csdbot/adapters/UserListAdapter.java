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
    private Context mContext;
    private String courseName;
    private String post;
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
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        courseName = ((Activity) mContext).getIntent().getStringExtra("Course Name");
        post = ((Activity) mContext).getIntent().getStringExtra("Post");


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
            String name = viewHolder.userName.getText().toString();
            if ( userList.get(position).isAdmin() ){
                name = name + " (admin)";
                viewHolder.userName.setText(name);
            }
            if ( post.equals("false")){
                isTeacher = false;
                for( Course temp : userList.get(position).getUndergraduate_teaching_courses() ){
                    if ( temp.getName_en().equals(courseName) ){
                        isTeacher = true;
                        break;
                    }
                }
            } else {
                isTeacher = false;
                for( PostGraduateCourse temp : userList.get(position).getPostgraduate_teaching_courses() ){
                    if ( temp.getName_en().equals(courseName) ){
                        isTeacher = true;
                        break;
                    }
                }
            }
            if ( isTeacher ){
                name = name + " (Teacher)";
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
