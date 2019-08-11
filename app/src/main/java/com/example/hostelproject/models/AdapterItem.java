package com.example.hostelproject.models;

import android.view.View;

import androidx.databinding.BindingAdapter;

import com.example.hostelproject.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterItem {
    String name;
    String phone;
    String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @BindingAdapter({"app:image"})
    public static void loadImage(View view, String image) {

        CircleImageView imageView = (CircleImageView) view;

        Picasso.get()
                .load(image)
                .placeholder(R.drawable.loading)
                .fit()
                .centerInside()
                .into(imageView);
    }
}
