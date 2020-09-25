package com.example.csdbot.adapters;

import android.content.Context;
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

import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.R;
import com.example.csdbot.components.User;
import com.example.csdbot.viewholders.UserViewHolder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
    private int layout, pos;
    private List<User> userList;
    private DatabaseHelper myDB;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private User profileUser;
    private Context mContext;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        mContext = context;
        layout = resource;
        userList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        UserViewHolder mainViewholder = null;

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Connect to the database and get users list
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                userList = myDB.getUserList();
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
            viewHolder.userName = (TextView) convertView.findViewById(R.id.teacherName);
            viewHolder.userEmail = (TextView) convertView.findViewById(R.id.teacherEmail);
            viewHolder.userImage = (ImageView) convertView.findViewById(R.id.userListItemPicture);

            // Set user's data
            viewHolder.userName.setText(userList.get(position).getName());
            if ( userList.get(position).isAdmin() ){
                viewHolder.userName.setText(viewHolder.userName.getText().toString() + " (admin)");
            }
            viewHolder.userEmail.setText("Email: " + userList.get(position).getEmail());
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
