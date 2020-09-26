package com.example.csdbot.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.csdbot.viewholders.ContactViewHolder;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.R;
import com.example.csdbot.components.User;
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

public class AddContactsListAdapter extends ArrayAdapter<User> {

    private int layout;
    private List<User> contacts;
    private Context mContext;
    private DatabaseHelper myDB;
    private FirebaseAuth firebaseAuth;
    private User profileUser;
    private Dialog addConfirmatonDialog;
    private ProgressDialog loading;

    public AddContactsListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        layout = resource;
        contacts = objects;
        firebaseAuth = FirebaseAuth.getInstance();
        for( int i = 0 ; i < contacts.size() ; i++ ){
            if ( contacts.get(i).getUID().equals(firebaseAuth.getUid()) ){
                contacts.remove(i);
                break;
            }
        }
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ContactViewHolder mainViewHolder;
        final ContactViewHolder viewHolder = new ContactViewHolder();

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Connect to the database and retrieve the user's data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if (myDB != null) {
                    profileUser = myDB.getUserByUID(firebaseAuth.getUid());
                }

                // Check if user is already a friend with current user
                // and if he is hide the add contact button
                if ( profileUser.isFriend(contacts.get(position).getUID()) ){
                    viewHolder.addContactImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext() , databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);

            /* Get the viewHolder's views
             *      profileUserName: User's username
             *      contactPicture: User's profile picture
             *      addContactImage: Add Contact Button
             */
            viewHolder.profileUserName = (TextView) convertView.findViewById(R.id.contactResultName);
            viewHolder.addContactImage = (ImageView) convertView.findViewById(R.id.addContactResult);
            viewHolder.contactPicture = (ImageView) convertView.findViewById(R.id.addContactImage);

            // Set user's username and profile picture
            viewHolder.profileUserName.setText(contacts.get(position).getName());
            storageReference.child(contacts.get(position).getUID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(viewHolder.contactPicture);
                }
            });

            // Add contact to user's contact list and update the database
            viewHolder.addContactImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView addContactTitle;
                    final Button addContactBtn, cancelBtn;
                    addConfirmatonDialog = new Dialog(getContext());
                    addConfirmatonDialog.setContentView(R.layout.add_contact_confirmation);
                    addContactTitle = (TextView) addConfirmatonDialog.findViewById(R.id.contactConfirmation);
                    String title = "Are you sure you wish to add " +
                            contacts.get(position).getName() +
                            "to your contacts?";
                    addContactTitle.setText(title);

                    addContactBtn = (Button) addConfirmatonDialog.findViewById(R.id.addContactConfirmationBtn);
                    cancelBtn = (Button) addConfirmatonDialog.findViewById(R.id.addContactCancelationBtn);

                    addContactBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addConfirmatonDialog.dismiss();
                            loading = ProgressDialog.show(( (Activity) mContext), "",
                                    "Loading. Please wait...", true);
                            profileUser.addToUserFriendList(contacts.get(position));
                            loading.dismiss();
                            databaseReference.setValue(myDB);
                        }
                    });

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addConfirmatonDialog.dismiss();
                        }
                    });
                    addConfirmatonDialog.show();
                }
            });
            convertView.setTag(viewHolder);
        } else {
            mainViewHolder = (ContactViewHolder) convertView.getTag();

            /* Get the viewHolders views
             *      profileUserName: User's username
             *      contactPicture: User's profile picture
             *      addContactImage: Add Contact Button
             */
            mainViewHolder.profileUserName = (TextView) convertView.findViewById(R.id.contactResultName);
            mainViewHolder.addContactImage = (ImageView) convertView.findViewById(R.id.addContactResult);
            mainViewHolder.contactPicture = (ImageView) convertView.findViewById(R.id.addContactImage);

            // Set user's username and profile picture
            mainViewHolder.profileUserName.setText(contacts.get(position).getName());
            storageReference.child(contacts.get(position).getUID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(viewHolder.contactPicture);
                }
            });

            // Add contact to user's contact list and update the database
            mainViewHolder.addContactImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView addContactTitle;
                    final Button addContactBtn, cancelBtn;
                    addConfirmatonDialog = new Dialog(getContext());
                    addConfirmatonDialog.setContentView(R.layout.add_contact_confirmation);
                    addContactTitle = (TextView) addConfirmatonDialog.findViewById(R.id.contactConfirmation);
                    String title = "Are you sure you wish to add " +
                            contacts.get(position).getName() +
                            "to your contacts?";
                    addContactTitle.setText(title);

                    addContactBtn = (Button) addConfirmatonDialog.findViewById(R.id.addContactConfirmationBtn);
                    cancelBtn = (Button) addConfirmatonDialog.findViewById(R.id.addContactCancelationBtn);

                    addContactBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            profileUser.addToUserFriendList(contacts.get(position));
                            addConfirmatonDialog.dismiss();
                            databaseReference.setValue(myDB);
                        }
                    });

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addConfirmatonDialog.dismiss();
                        }
                    });
                    addConfirmatonDialog.show();
                }
            });
        }
        return convertView;
    }
}
