package com.example.csdbot.components;

import java.util.ArrayList;

public class DatabaseHelper {

    private ArrayList<User> userList;
    private ArrayList<Course> courseList;
    private ArrayList<PostGraduateCourse> postGraduateCourseList = new ArrayList<PostGraduateCourse>();


    /**
     *      Constructors
     */
    public DatabaseHelper(){
        this.userList = new ArrayList<User>();
        this.courseList = new ArrayList<Course>();
    }

    public DatabaseHelper(ArrayList<User> userList, ArrayList<Course> courseList) {
        this.userList = userList;
        this.courseList = courseList;
    }

    /**
     * Check if the user exists in the database
     *
     * @param name the name of the User
     * @return true if the user exists in the database
     */
    public boolean userExists(String name){
        for(User temp : userList) {
            if ( temp.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    /**
     * Add user to the user's list
     * @param user user to be added to the list
     */
    public void addToUserList(User user){
        this.userList.add(user);
    }

    /**
     * Get user from user list, given his/her UID
     *
     * @param UID the user's unique ID number
     * @return the user with thw given UID
     */
    public User getUserByUID(String UID) {
        for ( User tempUser : this.userList) {
            if ( UID.equals(tempUser.getUID()) ) {
                return tempUser;
            }
        }
        return null;
    }

    /**
     * Get the user's position in the list
     *
     * @param user the user given
     * @return the user's position
     *          -1 if the user doesn't exist
     */
    public int getUserPosition (User user) {
        for( int i = 0 ; i < this.userList.size() ; i++ ) {
            if ( user.getUID().equals(this.userList.get(i).getUID())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get the course given its name
     * @param name the course's name
     * @return the undergraduate course requested
     */
    public Course getCourseByName(String name){
        for( Course temp : courseList ) {
            if(temp.getName_en().equals(name) || temp.getName_gr().equals(name)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * Get the course given its name
     * @param name the course's name
     * @return the postgraduate course requested
     */
    public PostGraduateCourse getPostGraduateCourseByName(String name){
        for( PostGraduateCourse temp : postGraduateCourseList ) {
            if(temp.getName_en().equals(name) || temp.getName_gr().equals(name)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * Check if database has undergraduate course
     * @param course the course given
     * @return true if undergraduate course exists in database
     *          false if undergraduate course does not exist in database
     */
    public boolean hasCourse(Course course) {
        for( Course temp : courseList ){
            if ( temp.getCode_en().equals(course.getCode_en()) || temp.getCode_gr().equals(course.getCode_gr()) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if database has postgraduate course
     * @param course the course given
     * @return true if postgraduate course exists in database
     *          false if postgraduate course does not exist in database
     */
    public boolean hasCourse(PostGraduateCourse course) {
        for( Course temp : postGraduateCourseList ){
            if ( temp.getCode_en().equals(course.getCode_en()) || temp.getCode_gr().equals(course.getCode_gr()) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the course's position in the course list
     *
     * @param course the course given
     * @return the course's position in the list
     */
    public int getCoursePosition (Course course) {
        for( int i = 0 ; i < this.courseList.size() ; i++ ) {
            if ( course.getName_en().equals(this.courseList.get(i).getName_en())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get the course's name, given its code
     * @param code the course's code
     * @return the course's name
     */
    public String getCourseNameByCode(String code){
        for( Course temp : courseList ){
            if ( temp.getCode_en().contains(code) || temp.getCode_gr().contains(code) ){
                return  temp.getName_en();
            }
        }
        return "";
    }

    /**
     * Search for query in undergraduate
     * and postgraduate courses lists
     *
     * @param query query for search
     * @return all courses containing the query
     */
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
                    temp.getArea_name_gr().contains(query)
            ){
                courseQueryList.add(temp);
            }
        }
        for( PostGraduateCourse temp : postGraduateCourseList ){
            if ( temp.getCode_en().contains(query) ||
                    temp.getName_en().contains(query) ||
                    temp.getDescription_en().contains(query) ||
                    temp.getArea_code_en().contains(query) ||
                    temp.getArea_name_en().contains(query) ||
                    temp.getCode_gr().contains(query) ||
                    temp.getName_gr().contains(query) ||
                    temp.getDescription_gr().contains(query) ||
                    temp.getArea_code_gr().contains(query) ||
                    temp.getArea_name_gr().contains(query)
            ){
                courseQueryList.add(temp);
            }
        }
        return  courseQueryList;
    }

    /**
     * Searcg for query in Users list
     * @param query query
     * @return the users list containing the query
     */
    public ArrayList<User> searchInUsers(String query){
        ArrayList<User> userQueryList = new ArrayList<User>();
        for(  User temp : userList ){
            if ( temp.getName().contains(query) ){
                userQueryList.add(temp);
            }
        }
        return userQueryList;
    }
    /**
     *      Getters and Setters
     */
    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
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
}
