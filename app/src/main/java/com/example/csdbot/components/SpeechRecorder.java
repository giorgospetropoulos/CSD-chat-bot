package com.example.csdbot.components;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.csdbot.activities.AddReminderActivity;
import com.example.csdbot.activities.CalendarActivity;
import com.example.csdbot.activities.ContactsActivity;
import com.example.csdbot.activities.CoursePageActivity;
import com.example.csdbot.activities.MyCoursesActivity;
import com.example.csdbot.activities.PostOrUnderGraduateActivity;
import com.example.csdbot.activities.ProfileActivity;
import com.example.csdbot.activities.RemindersActivity;
import com.example.csdbot.activities.RemindersListActivity;
import com.example.csdbot.activities.SearchResultsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class SpeechRecorder {

    private Context context;

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String recording, first_rec = "";
    private ArrayList<String> recording_table= new ArrayList<String>();
    private boolean my, add, new_, set, mathimata_, mathimata, ypenthimiseis, nea, neas, orismos, orise, dimiourgise, dimiourgia, search, go = false;

    /**
     *      Constructor
     */
    public SpeechRecorder(Context context) {
        this.context = context;;
    }

    /**
     *      Getters and Setters
     */
    public String getRecording() {
        return recording;
    }

    public void setRecording(String recording) {
        this.recording = recording;
    }

    /**
     *      Initialize Speech Recognizer
     */
    public void initializeSpeechRecognizer(){


        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault() );
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "el");

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matchesFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if ( matchesFound != null ){
                    recording = matchesFound.get(0);
                    first_rec = recording;
                    callAction(recording);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }

    /**
     *      Start recording
     */
    public void startRecording(){
        speechRecognizer.startListening(speechRecognizerIntent);
        recording = "";
    }

    /**
     *      Stop recording
     */
    public void stopRecording(){
        speechRecognizer.stopListening();
        recording = "";
    }

    /**
     *      Check if user has given voice record
     *      permission to the app
     */
    public void checkVoiceRecodrPerimission(){
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            if ( !(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) ){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.example.csdbot"));
                context.startActivity(intent);
            }
        }
    }


    /**
     * Call action depending on the recorded string
     * @param recording the recorded string
     */
    public void callAction(String recording){
        recording_table = new ArrayList<String>(Arrays.asList(recording.split(" ",2)));
        switch (recording_table.get(0)){
            case "my":
                my = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "go":
                go = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "πήγαινε":
                go = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "Πήγαινε":
                go = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "πήγαινέ":
                go = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "Πήγαινέ":
                go = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "πάμε":
                go = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "new":
                new_ = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "add":
                add = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "set":
                set = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "Set":
                set = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "courses":
                if ( my ){
                    Intent goToMyCourses = new Intent(context, MyCoursesActivity.class);
                    my = false;
                    context.startActivity(goToMyCourses);
                } else {
                    Intent goToCourses = new Intent(context, PostOrUnderGraduateActivity.class);
                    context.startActivity(goToCourses);
                }
                break;
            case "reminders":
                if ( my ){
                    Intent goToMyReminders = new Intent(context, RemindersListActivity.class);
                    my = false;
                    context.startActivity(goToMyReminders);
                } else {
                    Intent goToReminders = new Intent(context, RemindersActivity.class);
                    context.startActivity(goToReminders);
                }
                break;
            case "reminder":
                if ( add || new_ || set || nea || neas || orismos || orise || dimiourgise || dimiourgia){
                    Intent goToAddReminder = new Intent(context, AddReminderActivity.class);
                    add = false;
                    new_ = false;
                    context.startActivity(goToAddReminder);
                } else {
                    Intent goToReminders = new Intent(context, RemindersActivity.class);
                    context.startActivity(goToReminders);
                }
                break;
            case "calendar":
                Intent goToCalendar = new Intent(context, CalendarActivity.class);
                goToCalendar.putExtra("Calendar", "true");
                context.startActivity(goToCalendar);
                break;
            case "profile":
                Intent goToProfile = new Intent(context, ProfileActivity.class);
                context.startActivity(goToProfile);
                break;
            case "contacts":
                Intent goToContacts = new Intent(context, ContactsActivity.class);
                context.startActivity(goToContacts);
                break;
            case "Search":
                search = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "search":
                search = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "for":
                if ( search ){
                    search = false;
                    Intent goToSearch = new Intent(context, SearchResultsActivity.class);
                    goToSearch.putExtra("Query",recording_table.get(1));
                    goToSearch.putExtra("SearchResultsActivity", "true");
                    context.startActivity(goToSearch);

                } else {
                    if ( recording_table.size() > 1){
                        callAction(recording_table.get(1));
                    } else{
                        Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case "Ψάξε":
                search = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "ψάξε":
                search = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "για":
                if ( search ){
                    search = false;
                    Intent goToSearch = new Intent(context, SearchResultsActivity.class);
                    goToSearch.putExtra("Query",recording_table.get(1));
                    goToSearch.putExtra("SearchResultsActivity", "true");
                    context.startActivity(goToSearch);

                } else {
                    if ( recording_table.size() > 1){
                        callAction(recording_table.get(1));
                    } else{
                        Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case "ημερολόγιο":
                Intent goToCalendarGr = new Intent(context, CalendarActivity.class);
                goToCalendarGr.putExtra("Calendar", "true");
                context.startActivity(goToCalendarGr);
                break;
            case "προφίλ":
                Intent goToProfileGr = new Intent(context, ProfileActivity.class);
                context.startActivity(goToProfileGr);
                break;
            case "επαφές":
                Intent goToContactsGr = new Intent(context, ContactsActivity.class);
                context.startActivity(goToContactsGr);
                break;
            case "υπενθυμίσεις":
                ypenthimiseis = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Intent goToReminders = new Intent(context, RemindersActivity.class);
                    ypenthimiseis = false;
                    context.startActivity(goToReminders);
                }
                break;
            case "μαθήματά":
                mathimata_ = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "μαθήματα":
                mathimata = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Intent goToCourses = new Intent(context, PostOrUnderGraduateActivity.class);
                    mathimata_ = false;
                    mathimata = false;
                    context.startActivity(goToCourses);
                }
                break;
            case "μου":
                if ( (mathimata_ || mathimata) && !ypenthimiseis ){
                    Intent goToMyCourses = new Intent(context, MyCoursesActivity.class);
                    mathimata_ = false;
                    mathimata = false;
                    context.startActivity(goToMyCourses);
                } else if ( !mathimata_ && !mathimata && ypenthimiseis ){
                    Intent goToMyReminders = new Intent(context, RemindersListActivity.class);
                    context.startActivity(goToMyReminders);
                }
                break;
            case "νέα":
                nea = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "νέας":
                neas = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "ορισμός":
                orismos = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "Όρισε":
                orise = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "όρισε":
                orise = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "Δημιούργησε":
                dimiourgise = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "δημιούργησε":
                dimiourgise = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "δημιουργία":
                dimiourgia = true;
                if ( recording_table.size() > 1){
                    callAction(recording_table.get(1));
                } else{
                    Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                }
                break;
            case "υπενθύμιση":
                if ( nea || neas || orismos || orise || dimiourgise || dimiourgia ){
                    Intent goToAddReminder = new Intent(context, AddReminderActivity.class);
                    nea = false;
                    neas = false;
                    orismos = false;
                    orise = false;
                    dimiourgise = false;
                    dimiourgia = false;
                    context.startActivity(goToAddReminder);
                } else {
                    Intent goToReminders = new Intent(context, RemindersActivity.class);
                    context.startActivity(goToReminders);
                }
                break;
            case "υπενθύμισης":
                if ( nea || neas || orismos || orise || dimiourgise || dimiourgia ){
                    Intent goToAddReminder = new Intent(context, AddReminderActivity.class);
                    nea = false;
                    neas = false;
                    orismos = false;
                    orise = false;
                    dimiourgise = false;
                    dimiourgia = false;
                    context.startActivity(goToAddReminder);
                } else {
                    Intent goToReminders = new Intent(context, RemindersActivity.class);
                    context.startActivity(goToReminders);
                }
                break;
            default:
                if ( recording_table.get(0).matches("[0-9]{3}")){
                    if ( go ){
                        go = false;
                        Intent goToCourse = new Intent(context, CoursePageActivity.class);
                        goToCourse.putExtra("Course Code",recording_table.get(0));
                        context.startActivity(goToCourse);
                        break;
                    } else {
                        if ( recording_table.size() > 1){
                            callAction(recording_table.get(1));
                        } else{
                            Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                        }
                    }
                }
                if ( search ){
                    search = false;
                    Intent goToSearch = new Intent(context, SearchResultsActivity.class);
                    goToSearch.putExtra("Query",recording_table.get(1));
                    goToSearch.putExtra("SearchResultsActivity", "true");
                    context.startActivity(goToSearch);
                } else {
                    if ( recording_table.size() > 1){
                        callAction(recording_table.get(1));
                    } else{
                        //is this if necessary?
                        if ( (mathimata_ || mathimata) && !ypenthimiseis ){
                            Intent goToMyCourses = new Intent(context, MyCoursesActivity.class);
                            mathimata_ = false;
                            mathimata = false;
                            context.startActivity(goToMyCourses);
                        } else {
                            my = false;
                            add = false;
                            new_ = false;
                            set = false;
                            mathimata_ = false;
                            mathimata = false;
                            ypenthimiseis = false;
                            nea = false;
                            neas = false;
                            orismos = false;
                            orise = false;
                            dimiourgise = false;
                            dimiourgia = false;
                            search = false;
                            go = false;
                            Toast.makeText(context, "No results found for " + first_rec ,Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;
        }
    }

}
