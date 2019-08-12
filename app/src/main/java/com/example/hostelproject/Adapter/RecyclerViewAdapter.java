package com.example.hostelproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelproject.R;
import com.example.hostelproject.databinding.RecyclerViewListBinding;
import com.example.hostelproject.models.AdapterItem;
import com.example.hostelproject.models.Item;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class RecyclerViewAdapter extends FirestoreRecyclerAdapter<Item, RecyclerViewAdapter.ViewHolder> {

    private Context context;


    private OnItemClickListener listener;


    public RecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Item> options, Context context) {
        super(options);
        this.context = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Item item) {

        String phone = item.getPhone();
        String name;
        String image = item.getProfilePicture();

        if (!item.getSname().equals("")) {
            name = (item.getFname() + " " + item.getSname());
        } else {
            name = (item.getFname() + " " + item.getMname());
        }

        AdapterItem adapterItem = new AdapterItem();
        adapterItem.setName(name);
        adapterItem.setPhone(phone);
        adapterItem.setImage(image);
        viewHolder.recyclerViewListBinding.setAdapter(adapterItem);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewListBinding recyclerViewBindings = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.recycler_view_list, parent, false);
        return new ViewHolder(recyclerViewBindings);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerViewListBinding recyclerViewListBinding;

        public ViewHolder(@NonNull RecyclerViewListBinding itemView) {
            super(itemView.getRoot());
            recyclerViewListBinding = itemView;

            itemView.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);

                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
