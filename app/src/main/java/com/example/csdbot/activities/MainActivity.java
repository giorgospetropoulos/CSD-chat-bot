package com.example.csdbot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csdbot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Button login_btn;
    private EditText username,password;
    private  TextView signUp_btn, forgotPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = new ProgressDialog(this);

        /* Find the activity's views
         *      username: The user's email field
         *      password: The user's password
         *      login_btn: The Log In Button
         *      signUp_btn: The Sign Up Button
         *      forgotPassword: The Forgot Password Button
         */
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login_btn = (Button) findViewById(R.id.login);
        signUp_btn = (TextView) findViewById(R.id.signUp);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);

        // Initialize firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //checks if user is already logged in and redirects him to home page if he is logged in
        if (user != null ) {
            finish();
            Intent home = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(home);
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(username.getText().toString(), password.getText().toString());
            }
        });

        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToReset();
            }
        });
    }

    /**
     * Log In User to the App
     * @param userName user's email
     * @param userPassword user's password
     */
    public void login(String userName, String userPassword) {

        progress.setMessage("Authenticating...");
        progress.show();

        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){
                   progress.dismiss();
                   checkEmailVerification();
               } else {
                   Toast.makeText(MainActivity.this, "Log In Failed. Please type in your password and email again.", Toast.LENGTH_SHORT).show();
                   progress.dismiss();
               }
            }
        });
    }

    // Start Sign Up Activity
    public void signUp() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    // Check if email is verified
    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        boolean emailFlag = firebaseUser.isEmailVerified();
        if ( emailFlag ) {
            finish();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "You need to verify your email first", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

    // Start Forgot Password Activity
    public void goToReset() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

}
