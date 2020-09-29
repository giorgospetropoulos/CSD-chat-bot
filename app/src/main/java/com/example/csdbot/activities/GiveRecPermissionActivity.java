package com.example.csdbot.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.example.csdbot.R;

public class GiveRecPermissionActivity extends AppCompatActivity {

    private Button goToSettings;
    private SharedPreferences myPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_rec_permission);

        // Get Shared Preferences
        myPref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = myPref.edit();

        goToSettings = findViewById(R.id.goToPermissions);
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            goToSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( !(ContextCompat.checkSelfPermission(GiveRecPermissionActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) ){
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.example.csdbot"));
                        GiveRecPermissionActivity.this.startActivity(intent);
                    } else {
                        editor.putString("PermissionGranted", "true");
                        editor.commit();
                        Intent intent = new Intent(GiveRecPermissionActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });
        } else {
            goToSettings.setText("Go to Home Page");
            goToSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GiveRecPermissionActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            });
        }
    }


}