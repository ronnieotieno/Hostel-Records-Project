package com.example.hostelproject.models;

import android.view.View;

import androidx.databinding.BindingAdapter;

import com.example.hostelproject.R;
import com.google.firebase.database.Exclude;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Item {

    public String fname;
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
    private String school;
    private String idImageUrl;

    @Exclude
    private String id;

    public Item() {
        //Empty constructor required

    }

    public Item(String fname, String mname, String sname, String email, String phone,
                String city, String parentsName, String parentsContact, String dob, String emergency, String profilePicture, Date date, String school, String idImageUrl) {
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
        this.school = school;
        this.idImageUrl = idImageUrl;
    }

    public String getSchool() {
        return school;
    }

    public String getIdImageUrl() {
        return idImageUrl;
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

    @BindingAdapter({"app:imageUrl"})
    public static void loadImage(View view, String profilePicture) {

        CircleImageView imageView = (CircleImageView) view;

        Picasso.get()
                .load(profilePicture)
                .placeholder(R.drawable.loading)
                .fit()
                .centerInside()
                .into(imageView);
    }

}
