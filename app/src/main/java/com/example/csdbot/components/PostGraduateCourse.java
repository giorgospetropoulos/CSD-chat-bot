package com.example.csdbot.components;

import java.util.ArrayList;

public class PostGraduateCourse extends Course{
    private ArrayList<String> area_codes_gr = new ArrayList<String>();
    private ArrayList<String> area_codes_en = new ArrayList<String>();
    private ArrayList<String> area_names_gr = new ArrayList<String>();
    private ArrayList<String> area_names_en = new ArrayList<String>();

    /**
     *      Constructors
     */
    public PostGraduateCourse(){

    }


    public PostGraduateCourse(String code_en, String name_en, String description_en, String area_code_en, String url, String area_name_en, String code_gr, String name_gr, String description_gr, String area_code_gr, String area_name_gr, String ECTS) {
        this.setCode_en(code_en);
        this.setName_en(name_en);
        this.setDescription_en(description_en);
        this.area_codes_en.add(area_code_en);
        this.setUrl(url);
        this.area_names_en.add(area_name_en);
        this.setCode_gr(code_gr);
        this.setName_gr(name_gr);
        this.setDescription_gr(description_gr);
        this.area_codes_gr.add(area_code_gr);
        this.area_names_gr.add(area_name_gr);
        this.setECTS(ECTS);
        this.setTeacherUID("0");

    }

    public PostGraduateCourse(String name_en, String code_en, String description_en, String url, String ECTS, ArrayList<String> area_codes_en, ArrayList<String> area_names_en){
        this.setName_en(name_en);
        this.setCode_en(code_en);
        this.setDescription_en(description_en);
        this.area_codes_en = area_codes_en;
        this.setUrl(url);
        this.area_names_en = area_names_en;
        this.setCode_gr("");
        this.setName_gr("");
        this.setDescription_gr("");
        this.setTeacherUID("0");
        this.setTeacher("");
    }

    /**
     *      Getters and Setters
     */

    public ArrayList<String> getArea_codes_gr() {
        return area_codes_gr;
    }

    public void setArea_codes_gr(ArrayList<String> area_codes_gr) {
        this.area_codes_gr = area_codes_gr;
    }

    public ArrayList<String> getArea_codes_en() {
        return area_codes_en;
    }

    public void setArea_codes_en(ArrayList<String> area_codes_en) {
        this.area_codes_en = area_codes_en;
    }

    public ArrayList<String> getArea_names_gr() {
        return area_names_gr;
    }

    public void setArea_names_gr(ArrayList<String> area_names_gr) {
        this.area_names_gr = area_names_gr;
    }

    public ArrayList<String> getArea_names_en() {
        return area_names_en;
    }

    public void setArea_names_en(ArrayList<String> area_names_en) {
        this.area_names_en = area_names_en;
    }
}
