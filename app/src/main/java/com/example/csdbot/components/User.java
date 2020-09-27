package com.example.csdbot.components;

import java.util.ArrayList;
import java.util.Collections;

public class User {
    private String name, email, UID, teachingCourse;
    private ArrayList<Reminder> user_reminders = new ArrayList<Reminder>();
    private ArrayList<Course> user_courses = new ArrayList<Course>();
    private ArrayList<PostGraduateCourse> user_postgraduate_courses = new ArrayList<PostGraduateCourse>();
    private ArrayList<String> teaching_courses = new ArrayList<String>();
    private ArrayList<User> user_friendlist = new ArrayList<User>();
    private boolean isAdmin = false;

    /**
     *      Constructors
     */
    public User() {
    }


    public User(String name, String email, Reminder reminder, Course course) {
        this.name = name;
        this.email = email;
        this.user_reminders.add(reminder);
        this.user_courses.add(course);
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * Add user to user's contact list
     * @param user user to be added
     */
    public void addToUserFriendList(User user) {
        if ( !user.getUID().equals(this.getUID()) ){
            this.user_friendlist.add(user);
        }
    }

    /**
     * Check if user is in user's contacts list,
     * given his/her UID
     * @param UID the user's uid
     * @return the user
     */
    public boolean isFriend(String UID){
        for( User temp : user_friendlist ){
            if ( temp.getUID().equals(UID) ){
                return true;
            }
        }
        return false;
    }

    /**
     * Check if user is enrolled to course
     * @param course course given
     * @return true if user is enrolled to given user
     *          false if user is not enrolled to given user
     */
    public boolean isEnrolledTo(String course) {
        for ( Course temp : user_courses ) {
            if ( course.equals(temp.getName_en())) {
                return true;
            }
        }
        for ( PostGraduateCourse temp : user_postgraduate_courses ) {
            if ( course.equals(temp.getName_en())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove course from user's course list
     * @param course course to be removed
     */
    public void removeCourse(Course course) {
        for( int i = 0 ; i < user_courses.size() ; i++ ) {
            if ( course.getCode_en().equals(user_courses.get(i).getCode_en()) ){
                user_courses.remove(i);
                return;
            }
        }
    }

    /**
     * Check if user has reminder in his reminders list,
     * given the reminder's ID
     * @param ID the reminder's ID
     * @return true if user has reminder
     *          false if user does not have reminder
     */
    public boolean hasReminderByID(int ID) {
        for (Reminder temp : user_reminders ) {
            if ( temp.getId() == ID) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get reminder given his ID
     * @param ID the user's ID
     * @return the reminder with ID
     */
    public Reminder getReminderByID(int ID) {
        for (Reminder temp : user_reminders ) {
            if ( temp.getId() == ID) {
                return temp;
            }
        }
        return null;
    }

    /**
     * Get the reminder's position in the user's list
     * @param name the reminder's name
     * @return the reminder's position
     */
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

    /**
     * Get the reminder's position in the user's list
     * @param name the reminder's name
     * @param description the reminder's description
     * @param day the reminder's day
     * @param month the reminder's month
     * @param year the reminder's year
     * @param hour the reminder's hou r
     * @param min the reminder's min
     * @return the reminder's position
     */
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

    /**
     * Search in user's reminders for query
     * @param query query for search
     * @return the reminders that contain the query
     */
    public ArrayList<Reminder> searchInReminders(String query) {
        ArrayList<Reminder> remindersQueryList = new ArrayList<Reminder>();
        for( Reminder temp : user_reminders ){
            if ( temp.getName().contains(query) ){
                remindersQueryList.add(temp);
            }
        }
        return remindersQueryList;
    }



    /**
     *      Getters and Setters
     */

    public String getTeachingCourse() {
        return teachingCourse;
    }

    public void setTeachingCourse(String teachingCourse) {
        this.teachingCourse = teachingCourse;
    }

    public ArrayList<String> getTeaching_courses() {
        return teaching_courses;
    }

    public void setTeaching_courses(ArrayList<String> teaching_courses) {
        this.teaching_courses = teaching_courses;
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

    public ArrayList<PostGraduateCourse> getUser_postgraduate_courses() {
        return user_postgraduate_courses;
    }

    public void setUser_postgraduate_courses(ArrayList<PostGraduateCourse> user_postgraduate_courses) {
        this.user_postgraduate_courses = user_postgraduate_courses;
    }
}
