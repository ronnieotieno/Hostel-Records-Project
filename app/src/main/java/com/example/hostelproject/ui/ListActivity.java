package com.example.hostelproject.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelproject.Adapter.RecyclerViewAdapter;
import com.example.hostelproject.FireBaseAccountsUtils.LogIn;
import com.example.hostelproject.R;
import com.example.hostelproject.models.Item;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;
    private Item clickedItem;
    public static int Clicked_Request_Code = 45;
    private FirestoreRecyclerOptions<Item> options;
    private List<Item> items;
    private CollectionReference dbRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        items = new ArrayList<>();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.fab);


        Toolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, EnterDataActivity.class);
                startActivity(intent);
            }
        });
        setUpAdapter();

    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || !user.isEmailVerified()) {
            finish();
            startActivity(new Intent(this, LogIn.class));
        } else {
            recyclerViewAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (user != null) {
            recyclerViewAdapter.stopListening();
        }
    }


    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

        clickedItem = documentSnapshot.toObject(Item.class);
        clickedItem.setId((documentSnapshot.getId()));

        Intent intent = new Intent(ListActivity.this, DetailActivity.class);
        intent.putExtra("items", clickedItem);
        startActivityForResult(intent, Clicked_Request_Code);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == Clicked_Request_Code && resultCode == RESULT_OK && data != null)) {
            Item items = (Item) data.getSerializableExtra("itemsSend");

            if (user != null) {
                db.collection(user.getUid() + "Archives").document(clickedItem.getId())
                        .set(items).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(ListActivity.this, "Updated Successfully", Toast.LENGTH_LONG).show();

                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.log_out) {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ListActivity.this, LogIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Search(newText);
                recyclerViewAdapter.startListening();

                return false;
            }
        });
        return true;
    }

    public void Search(String searchText) {
        if (user != null) {
            dbRef = db.collection(user.getUid() + "Archives");

            Query query = dbRef.orderBy("fname").startAt(searchText).endAt(searchText + "\uf8ff");
            // Query query2 = dbRef.orderBy("date", Query.Direction.DESCENDING);

            options = new FirestoreRecyclerOptions.Builder<Item>()
                    .setQuery(query, Item.class)
                    //  .setQuery(query2, Item.class)
                    .build();
            recyclerViewAdapter = new RecyclerViewAdapter(options, ListActivity.this);
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyDataSetChanged();
            recyclerViewAdapter.setOnItemClickListener(ListActivity.this);
        }
    }

    public void setUpAdapter() {

        if (user != null) {
            dbRef = db.collection(user.getUid() + "Archives");
            Query query = dbRef.orderBy("date", Query.Direction.DESCENDING);

            options = new FirestoreRecyclerOptions.Builder<Item>()
                    .setQuery(query, Item.class)
                    .build();
            recyclerViewAdapter = new RecyclerViewAdapter(options, ListActivity.this);
            recyclerViewAdapter.startListening();
            recyclerViewAdapter.notifyDataSetChanged();
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.setOnItemClickListener(ListActivity.this);
        }

//        db.collection(user.getUid() + "Archives").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        items.add(document.toObject(Item.class));
//                    }
//
//                    for (Item i : items) {
//                        Log.d("MainActivity", i.fname + " " + i.mname);
//                    }
//
//                } else {
//                    Log.d("MainActivity", "Error getting documents: ", task.getException());
//                }
//            }
//        });
    }

}
