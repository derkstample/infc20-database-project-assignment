package se.lu.ics.models;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String personalNo;
    private String name;
    private String email;
    private List<Course> courses = new ArrayList<>();

    public Student(String personalNo, String name, String email) {
        this.personalNo = personalNo;
        this.name = name;
        this.email = email;
    }

    public String getPersonalNo(){
        return personalNo;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setPersonalNo(String personalNo){
        this.personalNo = personalNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Course> getCourses() {
        return courses;
    }
}