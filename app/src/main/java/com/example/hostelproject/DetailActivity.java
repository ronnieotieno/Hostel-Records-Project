package com.example.hostelproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.hostelproject.Constants.Email;
import static com.example.hostelproject.Constants.First_Name;
import static com.example.hostelproject.Constants.Middle_Name;
import static com.example.hostelproject.Constants.Sur_Name;
import static com.example.hostelproject.Constants.city;
import static com.example.hostelproject.Constants.dob;
import static com.example.hostelproject.Constants.emergency;
import static com.example.hostelproject.Constants.parents_Name;
import static com.example.hostelproject.Constants.parents_Phone;
import static com.example.hostelproject.Constants.phone;
import static com.example.hostelproject.Constants.profile_Picture;
import static java.lang.System.currentTimeMillis;

public class DetailActivity extends AppCompatActivity implements
        ChooseImageFragment.OnInputListener, DatePickerDialog.OnDateSetListener {
    EditText textViewFname, textViewMname, textViewSname, textViewEmail, textViewParents_phone, textViewParents_name, textView_phone,
            textViewEmergency, textViewCity;
    CircleImageView imageView;
    String profile;
    LinearLayout linearLayout;
    byte[] uriProfileImage;
    String profileimage;
    ProgressBar progressBar;
    TextView textViewDob;
    Toolbar toolbar;
    MenuItem save;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            setTitle("");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        textViewFname = findViewById(R.id.texView_first_name);
        textViewMname = findViewById(R.id.texView_middle_name);
        textViewSname = findViewById(R.id.texView_sur_name);
        textViewEmail = findViewById(R.id.texView_email);
        textView_phone = findViewById(R.id.texView_phone);
        textViewDob = findViewById(R.id.texView_dob);
        textViewParents_name = findViewById(R.id.texView_parent_name);
        textViewParents_phone = findViewById(R.id.texView_parent_contact);
        textViewEmergency = findViewById(R.id.texView_emergency_contact);
        imageView = findViewById(R.id.imageViewDe);
        textViewCity = findViewById(R.id.texView_city);
        linearLayout = findViewById(R.id.linear);
        progressBar = findViewById(R.id.progressbarUpdate);


        DisabledViews();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, ImageActivity.class);
                intent.putExtra("Image", profile);
                startActivity(intent);


            }
        });


        InitializeIntent();
    }

    public void DisabledViews() {
        textViewFname.setEnabled(false);
        textViewMname.setEnabled(false);
        textViewSname.setEnabled(false);
        textViewEmail.setEnabled(false);
        textView_phone.setEnabled(false);
        textViewDob.setEnabled(false);
        textViewParents_name.setEnabled(false);
        textViewParents_phone.setEnabled(false);
        textViewEmergency.setEnabled(false);
        textView_phone.setEnabled(false);
        textViewCity.setEnabled(false);
    }


    public void InitializeIntent() {
        Intent intent = getIntent();
        String Fname = intent.getStringExtra(First_Name);
        String Mname = intent.getStringExtra(Middle_Name);
        String Sname = intent.getStringExtra(Sur_Name);
        String email = intent.getStringExtra(Email);
        String Phone = intent.getStringExtra(phone);
        String City = intent.getStringExtra(city);
        String parents_phone = intent.getStringExtra(parents_Phone);
        String parents_name = intent.getStringExtra(parents_Name);
        String Emergency = intent.getStringExtra(emergency);
        profile = intent.getStringExtra(profile_Picture);
        String Dob = intent.getStringExtra(dob);
        textViewEmergency.setText(Emergency);
        textViewParents_phone.setText(parents_phone);
        textViewParents_name.setText(parents_name);
        textViewDob.setText(Dob);
        textViewEmail.setText(email);
        textViewSname.setText(Sname);
        textViewMname.setText(Mname);
        textViewFname.setText(Fname);
        textViewCity.setText(City);
        textView_phone.setText(Phone);


        Picasso.get()
                .load(profile)
                .placeholder(R.drawable.loading)
                .fit()
                .centerInside()
                .into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        save = menu.findItem(R.id.Save_menu).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.Edit_menu) {
            save.setVisible(true);
            getSupportActionBar().setTitle("Edit Profile");
            SetViewsEnabled();
        } else if (item.getItemId() == R.id.Save_menu) {
            Update();
        }
        return super.onOptionsItemSelected(item);
    }

    void SetViewsEnabled() {
        textViewFname.setEnabled(true);
        textViewFname.requestFocus();
        textViewMname.setEnabled(true);
        textViewSname.setEnabled(true);
        textViewEmail.setEnabled(true);
        textView_phone.setEnabled(true);
        textViewDob.setEnabled(true);
        textViewParents_name.setEnabled(true);
        textViewParents_phone.setEnabled(true);
        textViewEmergency.setEnabled(true);
        textViewCity.setEnabled(true);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("MyDialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                ChooseImageFragment dialog = new ChooseImageFragment();
                dialog.show(ft, "MyDialog");

            }

        });
        textViewDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new com.example.hostelproject.DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");

            }
        });

    }

    void Update() {
        String fnameintent = textViewFname.getText().toString();
        String mnameintent = textViewMname.getText().toString();
        String snameintent = textViewSname.getText().toString();
        String Emailintent = textViewEmail.getText().toString();
        String phoneintent = textView_phone.getText().toString();
        String cityintent = textViewCity.getText().toString();
        String parentsphoneIntent = textViewParents_phone.getText().toString();
        String parentsnameintent = textViewParents_name.getText().toString();
        String emergencyintent = textViewEmergency.getText().toString();
        String Dobintent = textViewDob.getText().toString();


        Intent intent = new Intent();
        intent.putExtra(First_Name, fnameintent);
        intent.putExtra(Middle_Name, mnameintent);
        intent.putExtra(Sur_Name, snameintent);
        intent.putExtra(Email, Emailintent);
        intent.putExtra(phone, phoneintent);
        intent.putExtra(city, cityintent);
        intent.putExtra(parents_Name, parentsnameintent);
        intent.putExtra(parents_Phone, parentsphoneIntent);
        intent.putExtra(dob, Dobintent);
        intent.putExtra(emergency, emergencyintent);


        if (profileimage != null && !profileimage.isEmpty()) {
            intent.putExtra(profile_Picture, profileimage);
            if (profile != null && !profile.isEmpty()) {
                DeletePhoto();
            }

        } else {
            intent.putExtra(profile_Picture, profile);
        }
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void sendInput(byte[] bytes) {
        uriProfileImage = bytes;
        UploadImage();

    }

    void UploadImage() {
        if (user != null) {
            StorageReference profileImageRef =
                    FirebaseStorage.getInstance().getReference(user.getUid() + "ProfilePictures/" + currentTimeMillis() + ".jpg");
            if (uriProfileImage != null) {
                progressBar.setVisibility(View.VISIBLE);
                profileImageRef.putBytes(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(DetailActivity.this, "Profile Picture uploaded", Toast.LENGTH_LONG).show();

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while
                        (!urlTask.isSuccessful()) ;
                        Uri downloadUrl = urlTask.getResult();
                        profileimage = downloadUrl.toString();
                        progressBar.setVisibility(View.GONE);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(uriProfileImage, 0, uriProfileImage.length);
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        }
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        textViewDob.setText(currentDateString);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    void DeletePhoto() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(profile);
        storageReference.delete();
    }

}