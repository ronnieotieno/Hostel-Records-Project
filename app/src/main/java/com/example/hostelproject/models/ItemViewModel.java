package com.example.hostelproject.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.hostelproject.BR;


public class ItemViewModel extends BaseObservable {

    public Item item;

    @Bindable
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        notifyPropertyChanged(BR.item);
    }
}
