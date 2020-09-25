package com.example.csdbot.components;

import java.util.ArrayList;
import java.util.Comparator;

public class Course {

    private String code_en, name_en, description_en, teacher, area_code_en, teacherUID, url, area_name_en;
    private String code_gr, name_gr, description_gr, area_code_gr, area_name_gr;
    private ArrayList<Reminder> courseReminders = new ArrayList<Reminder>();
    private  ArrayList<String> userList = new ArrayList<String>();
    private String ECTS;


    public Course(){

    }

    public Course(String code_en, String name_en, String description_en, String area_code_en, String url, String area_name_en, String code_gr, String name_gr, String description_gr, String area_code_gr, String area_name_gr, String ECTS) {
        this.code_en = code_en;
        this.name_en = name_en;
        this.description_en = description_en;
        this.area_code_en = area_code_en;
        this.url = url;
        this.area_name_en = area_name_en;
        this.code_gr = code_gr;
        this.name_gr = name_gr;
        this.description_gr = description_gr;
        this.area_code_gr = area_code_gr;
        this.area_name_gr = area_name_gr;

        this.ECTS = ECTS;
        this.teacherUID = "0";
    }

    public Course(String code, String name, String teacher, String description, String field, String ECTS) {
        this.code_en = code;
        this.name_en = name;
        this.description_en = description;
        this.teacher = teacher;
        this.area_code_en = field;
        this.ECTS = ECTS;
        this.teacherUID = "0";
    }



    public Course(String name, String description, String ECTS) {
        this.name_en = name;
        this.description_en = description;
        this.ECTS = ECTS;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getDescription_en() {
        return description_en;
    }

    public void setDescription_en(String description_en) {
        this.description_en = description_en;
    }

    public String getECTS() {
        return ECTS;
    }

    public void setECTS(String ECTS) {
        this.ECTS = ECTS;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getArea_code_en() {
        return area_code_en;
    }

    public void setArea_code_en(String area_code_en) {
        this.area_code_en = area_code_en;
    }

    public ArrayList<Reminder> getCourseReminders() {
        return courseReminders;
    }

    public void setCourseReminder(Reminder rem) {
        this.courseReminders.add(rem);
    }

    public Reminder getCourseReminder(int i) {
        return courseReminders.get(i);
    }

    public void deleteCourseReminder(int i) {
        courseReminders.remove(i);
    }

    public void setCourseReminders(ArrayList<Reminder> courseReminders) {
        this.courseReminders = courseReminders;
    }

    public String getTeacherUID() {
        return teacherUID;
    }

    public void setTeacherUID(String teacherUID) {
        this.teacherUID = teacherUID;
    }

    public ArrayList<String> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<String> userList) {
        this.userList = userList;
    }

    public void addUserToCourseUserList(String UID) {
        this.userList.add(UID);
    }

    public void removeUserFromCourseUserList(String UID) {
        for( int i = 0 ; i < userList.size() ; i++ ){
            if (UID.equals(userList.get(i))) {
                userList.remove(i);
                return;
            }
        }
    }

    public String getCode_en() {
        return code_en;
    }

    public void setCode_en(String code_en) {
        this.code_en = code_en;
    }

    public static Comparator<Course> CourseComparator = new Comparator<Course>() {
        @Override
        public int compare(Course c1, Course c2) {
            return c1.getCode_en().compareTo(c2.getCode_en());
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCode_gr() {
        return code_gr;
    }

    public void setCode_gr(String code_gr) {
        this.code_gr = code_gr;
    }

    public String getName_gr() {
        return name_gr;
    }

    public void setName_gr(String name_gr) {
        this.name_gr = name_gr;
    }

    public String getDescription_gr() {
        return description_gr;
    }

    public void setDescription_gr(String description_gr) {
        this.description_gr = description_gr;
    }

    public String getArea_code_gr() {
        return area_code_gr;
    }

    public void setArea_code_gr(String area_code_gr) {
        this.area_code_gr = area_code_gr;
    }

    public String getArea_name_en() {
        return area_name_en;
    }

    public void setArea_name_en(String area_name_en) {
        this.area_name_en = area_name_en;
    }

    public String getArea_name_gr() {
        return area_name_gr;
    }

    public void setArea_name_gr(String area_name_gr) {
        this.area_name_gr = area_name_gr;
    }

    public int getCourseReminderPosition(String name){
        int i = 0;
        for( Reminder temp: courseReminders){
            if ( temp.getName().equals(name) ){
                return i;
            }
            i++;
        }
        return i;
    }

    @Override
    public String toString(){
        return getName_en();
    }
}
