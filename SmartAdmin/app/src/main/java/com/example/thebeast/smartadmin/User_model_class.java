package com.example.thebeast.smartadmin;

/**
 * Created by Fidel M Omolo on 5/9/2018.
 */

public class User_model_class {
    String fname,lname,imageuri,email,location,phone_no,thumburi;

    public User_model_class() {
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getThumburi() {
        return thumburi;
    }

    public void setThumburi(String thumburi) {
        this.thumburi = thumburi;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public User_model_class(String fname, String lname, String phone_no, String email, String location, String imageuri, String thumburi) {
        this.fname = fname;
        this.lname = lname;
        this.phone_no=phone_no;
        this.email=email;
        this.imageuri = imageuri;
        this.thumburi=thumburi;
        this.location=location;

    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }
}
