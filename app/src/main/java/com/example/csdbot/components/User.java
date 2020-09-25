package com.example.csdbot.components;

import java.util.ArrayList;
import java.util.Collections;

public class User {
    private String name, email, UID;
    private ArrayList<Reminder> user_reminders = new ArrayList<Reminder>();
    private ArrayList<Course> user_courses = new ArrayList<Course>();
    private ArrayList<PostGraduateCourse> user_postgraduate_courses = new ArrayList<PostGraduateCourse>();
    private ArrayList<User> user_friendlist = new ArrayList<User>();
    private boolean isAdmin = false;
    private int friendListInit = 0;

    public User() {
    }


    public User(String name, String email, Reminder reminder, Course course) {
        this.name = name;
        this.email = email;
        this.user_reminders.add(reminder);
        this.user_courses.add(course);
        this.friendListInit = 0;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.friendListInit = 0;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Reminder> getUser_reminders() {
        return user_reminders;
    }

    public void setUser_reminders(ArrayList<Reminder> user_reminders) {
        this.user_reminders = user_reminders;
    }

    public void addUser_reminders(Reminder user_reminder) {
        this.user_reminders.add(user_reminder);
        Collections.sort(user_reminders, Reminder.ReminderComparator);
    }

    public void  deleteUser_reminder(int i){
        this.user_reminders.remove(i);
    }

    public Reminder getUser_reminder(Reminder user_reminder) {
        for (Reminder temp : user_reminders) {
            if ( temp.getName().equals(user_reminder.getName() )) {
                return temp;
            }
        }
        return null;
    }

    public boolean remindersListIsEmpty(){
        if( user_reminders == null ){
            return true;
        } else {
            return false;
        }
        //return (user_reminders == null ? true : false);
    }

    public ArrayList<Course> getUser_courses() {
        return user_courses;
    }

    public void setUser_courses(ArrayList<Course> user_courses) {
        this.user_courses = user_courses;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public ArrayList<User> getUser_friendlist() {
        return user_friendlist;
    }

    public void setUser_friendlist(ArrayList<User> user_friendlist) {
        this.user_friendlist = user_friendlist;
    }



    public void addToUserFriendList(User user) {
        if ( !user.getUID().equals(this.getUID()) ){
            this.friendListInit = 1;
            this.user_friendlist.add(user);
        }

    }

    public int getFriendListInit() {
        return friendListInit;
    }

    public void setFriendListInit(int friendListInit) {
        this.friendListInit = friendListInit;
    }

    public ArrayList<PostGraduateCourse> getUser_postgraduate_courses() {
        return user_postgraduate_courses;
    }

    public void setUser_postgraduate_courses(ArrayList<PostGraduateCourse> user_postgraduate_courses) {
        this.user_postgraduate_courses = user_postgraduate_courses;
    }

    public boolean isFriend(String UID){
        for( User temp : user_friendlist ){
            if ( temp.getUID().equals(UID) ){
                return true;
            }
        }
        return false;
    }

    public boolean isEnrolledToPost(String course) {
        for ( PostGraduateCourse temp : user_postgraduate_courses ) {
            if ( course.equals(temp.getName_en())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEnrolledTo(String course) {
        for ( Course temp : user_courses ) {
            if ( course.equals(temp.getName_en())) {
                return true;
            }
        }
        return false;
    }

    public void removeCourse(Course course) {
        for( int i = 0 ; i < user_courses.size() ; i++ ) {
            if ( course.getCode_en().equals(user_courses.get(i).getCode_en()) ){
                user_courses.remove(i);
                return;
            }
        }
    }

    public void removePostGraduateCourse(PostGraduateCourse course) {
        for( int i = 0 ; i < user_postgraduate_courses.size() ; i++ ) {
            if ( course.getCode_en().equals(user_postgraduate_courses.get(i).getCode_en()) ){
                user_postgraduate_courses.remove(i);
                return;
            }
        }
    }

    public boolean hasReminderByID(int ID) {
        for (Reminder temp : user_reminders ) {
            if ( temp.getId() == ID) {
                return true;
            }
        }

        return false;
    }

    public Reminder getReminderByID(int ID) {
        for (Reminder temp : user_reminders ) {
            if ( temp.getId() == ID) {
                return temp;
            }
        }

        return null;
    }

    public int getReminderPosition(String name){
        int i = 0;
        for( Reminder temp : user_reminders ){
            if ( temp.getName().equals(name)){
                return i;
            }
            i++;
        }
        return i;
    }

    public int getReminderPosition(String name, String description, int day, int month, int year, int hour, int min){
        int i = 0;
        for( Reminder temp : user_reminders ){
            if ( temp.getName().equals(name)
                    && temp.getDescription().equals(description)
                    && temp.getMin() == min
                    && temp.getHour() == hour
                    && temp.getDay() == day
                    && temp.getHour() == hour
                    && temp.getMonth() == month
                    && temp.getYear() == year
            ){
                return i;
            }
            i++;
        }
        return i;
    }

    public ArrayList<Reminder> searchInReminders(String query) {
        ArrayList<Reminder> remindersQueryList = new ArrayList<Reminder>();
        for( Reminder temp : user_reminders ){
            if ( temp.getName().contains(query) ){
                remindersQueryList.add(temp);
            }
        }
        return remindersQueryList;
    }
}
