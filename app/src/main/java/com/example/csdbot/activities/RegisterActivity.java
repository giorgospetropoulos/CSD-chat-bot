package com.example.csdbot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.csdbot.components.DatabaseHelper;
import com.example.csdbot.R;
import com.example.csdbot.components.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private TextView userName, userPassword, userEmail;
    private Button register_btn;
    private FirebaseAuth firebaseAuth;
    private DatabaseHelper myDB;
    private DatabaseReference myRef;
    private FirebaseUser firebaseUser;
    private boolean success = false;
    private ProgressDialog sendingDialog;
    private Dialog verification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /* Find the activity's views
         *      userName: The user's username
         *      userPassword: The user's password
         *      userEmail: The user's email
         *      already: Already a User button
         *      register_btn: Sign Up button
         */
        userName = findViewById(R.id.username_register);
        userPassword = findViewById(R.id.password_register);
        userEmail = findViewById(R.id.email_register);
        TextView already = findViewById(R.id.already);
        register_btn = findViewById(R.id.register_btn);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("Database");

        sendingDialog = new ProgressDialog(RegisterActivity.this);
        sendingDialog.setMessage("Contacting the server. Please wait...");

        // Register user to the database
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( validate() ){
                    sendingDialog.show();
                    final String user_name = userName.getText().toString().trim();
                    final String user_password = userPassword.getText().toString().trim();
                    final String user_email = userEmail.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if ( task.isSuccessful() ){
                                firebaseUser = firebaseAuth.getCurrentUser();
                                if ( firebaseUser != null ){
                                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if ( task.isSuccessful() ) {
                                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        myDB = dataSnapshot.getValue(DatabaseHelper.class);
                                                        User tempUser = new User(user_name,user_email);
                                                        tempUser.setUID(firebaseAuth.getUid());
                                                        myDB.addToUserList(tempUser);
                                                        myRef.setValue(myDB);
                                                        sendingDialog.dismiss();
                                                        TextView addContactTitle;
                                                        final Button addContactBtn, cancelBtn;
                                                        verification = new Dialog(RegisterActivity.this);
                                                        verification.setContentView(R.layout.add_contact_confirmation);
                                                        addContactTitle = verification.findViewById(R.id.contactConfirmation);
                                                        addContactTitle.setText("You signed up successfully. A verification email has been sent to your email.\nPlease check your junk folder as well.");
                                                        addContactBtn = verification.findViewById(R.id.addContactConfirmationBtn);
                                                        String continuebtn = "Continue";
                                                        addContactBtn.setText(continuebtn);
                                                        cancelBtn = verification.findViewById(R.id.addContactCancelationBtn);
                                                        cancelBtn.setVisibility(View.GONE);
                                                        addContactBtn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                verification.dismiss();
                                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                                firebaseAuth.signOut();
                                                                startActivity(intent);
                                                            }
                                                        });
                                                        verification.show();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "An error occurred and the verification mail was not sent. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Your Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        // Go to Log In Activity
        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Validate the credentials given
     * @return true if success
     *          false if failure
     */
    public boolean validate(){
        final ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
        dialog.setMessage("Validating. Please wait...");
        dialog.show();
        final String name = userName.getText().toString();
        final String email = userEmail.getText().toString();
        final String password = userPassword.getText().toString();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDB = dataSnapshot.getValue(DatabaseHelper.class);
                if (myDB != null) {
                    if( myDB.userExists(name)){
                        Toast.makeText(getBaseContext(), "This username already exists. Please choose another one", Toast.LENGTH_SHORT).show();
                    } else if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
                        Toast.makeText(getBaseContext(), "Please enter all the details", Toast.LENGTH_SHORT).show();
                    } else {
                        success = true;
                    }
                }
                dialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if ( success ){
            if (register_btn.getText().toString().equals("Validate") ){
                String signUp = "Sign Up";
                register_btn.setText(signUp);
            }
        }
        return success;
    }
}
