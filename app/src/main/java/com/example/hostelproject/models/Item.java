package com.example.hostelproject.models;

import com.google.firebase.database.Exclude;

import java.util.Date;

public class Item {

    private String fname;
    private String mname;
    private String sname;
    private String email;
    private String phone;
    private String city;
    private String parentsName;
    private String parentsContact;
    private String dob;
    private String emergency;
    private String profilePicture;
    private Date date;

    @Exclude
    private String id;

   public Item() {
        //Empty constructor required

    }

    public Item(String fname, String mname, String sname, String email, String phone,
                String city, String parentsName, String parentsContact, String dob, String emergency, String profilePicture, Date date) {
        this.fname = fname;
        this.mname = mname;
        this.sname = sname;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.parentsName = parentsName;
        this.parentsContact = parentsContact;
        this.dob = dob;
        this.emergency = emergency;
        this.profilePicture = profilePicture;
        this.date = date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public String getMname() {
        return mname;
    }

    public String getSname() {
        return sname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCity() {
        return city;
    }

    public String getParentsName() {
        return parentsName;
    }

    public String getParentsContact() {
        return parentsContact;
    }

    public String getDob() {
        return dob;
    }

    public String getEmergency() {
        return emergency;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public Date getDate() {
        return date;
    }

}
