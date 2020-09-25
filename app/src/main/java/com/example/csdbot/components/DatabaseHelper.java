package com.example.csdbot.components;

import java.util.ArrayList;

public class DatabaseHelper {

    private ArrayList<User> userList;
    private ArrayList<Course> courseList;
    private ArrayList<PostGraduateCourse> postGraduateCourseList = new ArrayList<PostGraduateCourse>();



    public DatabaseHelper(){
        this.userList = new ArrayList<User>();
        this.courseList = new ArrayList<Course>();
    }

    public DatabaseHelper(ArrayList<User> userList, ArrayList<Course> courseList) {
        this.userList = userList;
        this.courseList = courseList;
    }

    public boolean userExists(String name){
        for(User temp : userList) {
            if ( temp.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }

    public void addToUserList(User user){
        this.userList.add(user);
    }

    public void removeFromUserList(int position) {
        this.userList.remove(position);
    }

    public User getFromUserList(int position) {
        return this.userList.get(position);
    }

    public User getUserByUID(String UID) {
        for ( User tempUser : this.userList) {
            if ( UID.equals(tempUser.getUID()) ) {
                return tempUser;
            }
        }
        return null;
    }

    public int getUserPosition (User user) {
        for( int i = 0 ; i < this.userList.size() ; i++ ) {
            if ( user.getUID().equals(this.userList.get(i).getUID())) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Course> getCourseList() {
        return this.courseList;
    }

    public void setCourseList(ArrayList<Course> courseList) {
        this.courseList = courseList;
    }

    public void addToCourseList(Course course){
        this.courseList.add(course);
    }

    public void removeFromCourseList(int position) {
        this.courseList.remove(position);
    }

    public Course getFromCourseList(int position) {
        return this.courseList.get(position);
    }

    public ArrayList<PostGraduateCourse> getPostGraduateCourseList() {
        return postGraduateCourseList;
    }

    public void setPostGraduateCourseList(ArrayList<PostGraduateCourse> postGraduateCourseList) {
        this.postGraduateCourseList = postGraduateCourseList;
    }

    public Course getCourseByName(String name){
        for( Course temp : courseList ) {
            if(temp.getName_en().equals(name) || temp.getName_gr().equals(name)) {
                return temp;
            }
        }
        return null;
    }

    public PostGraduateCourse getPostGraduateCourseByName(String name){
        for( PostGraduateCourse temp : postGraduateCourseList ) {
            if(temp.getName_en().equals(name) || temp.getName_gr().equals(name)) {
                return temp;
            }
        }
        return null;
    }

    public boolean hasCourse(Course course) {
        for( Course temp : courseList ){
            if ( temp.getCode_en().equals(course.getCode_en()) || temp.getCode_gr().equals(course.getCode_gr()) ) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCourse(PostGraduateCourse course) {
        for( Course temp : postGraduateCourseList ){
            if ( temp.getCode_en().equals(course.getCode_en()) || temp.getCode_gr().equals(course.getCode_gr()) ) {
                return true;
            }
        }
        return false;
    }

    public int getCoursePosition (Course course) {
        for( int i = 0 ; i < this.courseList.size() ; i++ ) {
            if ( course.getName_en().equals(this.courseList.get(i).getName_en())) {
                return i;
            }
        }
        return -1;
    }

    public String getCourseNameByCode(String code){
        for( Course temp : courseList ){
            if ( temp.getCode_en().contains(code) || temp.getCode_gr().contains(code) ){
                return  temp.getName_en();
            }
        }
        return "";
    }

    public ArrayList<Course> searchInCourses(String query){
        ArrayList<Course> courseQueryList = new ArrayList<Course>();
        for( Course temp : courseList ){
            if ( temp.getCode_en().contains(query) ||
                    temp.getName_en().contains(query) ||
                    temp.getDescription_en().contains(query) ||
                    temp.getArea_code_en().contains(query) ||
                    temp.getArea_name_en().contains(query) ||
                    temp.getCode_gr().contains(query) ||
                    temp.getName_gr().contains(query) ||
                    temp.getDescription_gr().contains(query) ||
                    temp.getArea_code_gr().contains(query) ||
                    temp.getArea_name_gr().contains(query) //||
                    //temp.getTeacher().contains(query)
            ){
                courseQueryList.add(temp);
            }
        }
        return  courseQueryList;
    }

    public ArrayList<User> searchInUsers(String query){
        ArrayList<User> userQueryList = new ArrayList<User>();
        for(  User temp : userList ){
            if ( temp.getName().contains(query) ){
                userQueryList.add(temp);
            }
        }
        return userQueryList;
    }
}
