package com.example.csdbot.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.csdbot.viewholders.ContactViewHolder;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.R;
import com.example.csdbot.components.User;
import com.example.csdbot.activities.EmailActivity;
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

public class ContactsListAdapter extends ArrayAdapter<User> {

    private int layout;
    private List<User> contacts;
    private FirebaseAuth firebaseAuth;
    private DatabaseHelper myDB;
    private User user;
    private Dialog deleteConfirmatonDialog;


    public ContactsListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        contacts = objects;
        layout = resource;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ContactViewHolder mainViewHolder;

        // Initialize firebase components
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Database");

        // Connect to the database and retrieve the user's data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if (myDB != null) {
                    user = myDB.getUserByUID(firebaseAuth.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            final ContactViewHolder viewHolder = new ContactViewHolder();
            /* Get the viewHolder's views
             *      profileUserName: User's username
             *      profileEmail: User's email
             *      contactPicture: User's profile picture
             *      sendMessage: Send message Button
             *      deleteContact: Delete Contact Button
             */
            viewHolder.profileUserName = (TextView) convertView.findViewById(R.id.contactsUserName);
            viewHolder.profileEmail = (TextView) convertView.findViewById(R.id.contactsUserEmail);
            viewHolder.contactPicture = (ImageView) convertView.findViewById(R.id.contactsProfilePicture);
            viewHolder.sendMessage = (ImageView) convertView.findViewById(R.id.sendMessage);
            viewHolder.deleteContact = (ImageView) convertView.findViewById(R.id.deleteContact);

            // Set user's data
            viewHolder.profileUserName.setText(contacts.get(position).getName());
            viewHolder.profileEmail.setText(contacts.get(position).getEmail());
            storageReference.child(contacts.get(position).getUID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(viewHolder.contactPicture);
                }
            });

            // Send message to User
            viewHolder.sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), EmailActivity.class);
                    intent.putExtra("UID", contacts.get(position).getUID());
                    intent.putExtra("email_to", contacts.get(position).getEmail());
                    getContext().startActivity(intent);
                }
            });

            // Delete user from user's contact list
            viewHolder.deleteContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView deleteContactTitle;
                    final Button deleteContactBtn, cancelBtn;
                    deleteConfirmatonDialog = new Dialog(getContext());
                    deleteConfirmatonDialog.setContentView(R.layout.delete_contact_confirmation);
                    deleteContactTitle = (TextView) deleteConfirmatonDialog.findViewById(R.id.deleteContactConfirmation);
                    String title = "Are you sure you wish to delete " +
                            contacts.get(position).getName() +
                            " from your contacts?";
                    deleteContactTitle.setText(title);
                    deleteContactBtn = (Button) deleteConfirmatonDialog.findViewById(R.id.deleteContactConfirmationBtn);
                    cancelBtn = (Button) deleteConfirmatonDialog.findViewById(R.id.deleteContactCancelationBtn);
                    deleteContactBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            user.getUser_friendlist().remove(position);
                            databaseReference.setValue(myDB);
                            deleteConfirmatonDialog.dismiss();
                        }
                    });

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteConfirmatonDialog.dismiss();
                        }
                    });
                    deleteConfirmatonDialog.show();

                }
            });
            convertView.setTag(viewHolder);
        } else {
            mainViewHolder = (ContactViewHolder) convertView.getTag();
            mainViewHolder.profileUserName.setText(contacts.get(position).getName());
        }

        return convertView;
    }
}
