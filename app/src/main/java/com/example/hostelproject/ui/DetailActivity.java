package com.example.hostelproject.ui;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.hostelproject.Constants;
import com.example.hostelproject.ImageUtils.IdImageActivity;
import com.example.hostelproject.ImageUtils.ChooseImageFragment;
import com.example.hostelproject.ImageUtils.ChooseImageFragmentSecond;
import com.example.hostelproject.R;
import com.example.hostelproject.databinding.ActivityDetailBinding;
import com.example.hostelproject.models.Item;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

import static com.example.hostelproject.Constants.Email;
import static com.example.hostelproject.Constants.First_Name;
import static com.example.hostelproject.Constants.Middle_Name;
import static com.example.hostelproject.Constants.Sur_Name;
import static com.example.hostelproject.Constants.city;
import static com.example.hostelproject.Constants.dob;
import static com.example.hostelproject.Constants.emergency;
import static com.example.hostelproject.Constants.idImage;
import static com.example.hostelproject.Constants.parents_Name;
import static com.example.hostelproject.Constants.parents_Phone;
import static com.example.hostelproject.Constants.phone;
import static com.example.hostelproject.Constants.profile_Picture;
import static com.example.hostelproject.Constants.school;
import static java.lang.System.currentTimeMillis;

public class DetailActivity extends AppCompatActivity implements
        ChooseImageFragment.OnInputListener, DatePickerDialog.OnDateSetListener, ChooseImageFragmentSecond.OnInputListener {
    private String profile;
    private byte[] bytesProfileImage;
    private byte[] bytesId;
    private String profileImage;
    private Toolbar toolbar;
    private MenuItem save;
    ActivityDetailBinding activityDetailBinding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private String idImageUrl;
    private String idImageRevised;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
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


        activityDetailBinding.imageViewDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, ImageActivity.class);
                intent.putExtra("Image", profile);
                startActivity(intent);


            }
        });


        InitializeIntent();
    }


    public void InitializeIntent() {
        Intent intent = getIntent();
        String fName = intent.getStringExtra(First_Name);
        String mName = intent.getStringExtra(Middle_Name);
        String sName = intent.getStringExtra(Sur_Name);
        String email = intent.getStringExtra(Email);
        String Phone = intent.getStringExtra(phone);
        String City = intent.getStringExtra(city);
        String parents_phone = intent.getStringExtra(parents_Phone);
        String parents_name = intent.getStringExtra(parents_Name);
        String Emergency = intent.getStringExtra(emergency);
        profile = intent.getStringExtra(profile_Picture);
        String Dob = intent.getStringExtra(dob);
        String School = intent.getStringExtra(school);
        idImageUrl = intent.getStringExtra(idImage);

        Item item = new Item(fName, mName, sName, email, Phone, City, parents_name, parents_phone, Dob, Emergency, profile, null, School, null);
        activityDetailBinding.setItems(item);

        if (idImageUrl != null) {
            activityDetailBinding.texViewImage.setText("View ID");
            activityDetailBinding.texViewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenId();
                }
            });
        } else {
            activityDetailBinding.texViewImage.setText("No ID");
        }

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
        activityDetailBinding.texViewFirstName.setEnabled(true);
        activityDetailBinding.texViewFirstName.requestFocus();
        activityDetailBinding.texViewMiddleName.setEnabled(true);
        activityDetailBinding.texViewSurName.setEnabled(true);
        activityDetailBinding.texViewEmail.setEnabled(true);
        activityDetailBinding.texViewPhone.setEnabled(true);
        activityDetailBinding.texViewDob.setEnabled(true);
        activityDetailBinding.texViewParentName.setEnabled(true);
        activityDetailBinding.texViewParentContact.setEnabled(true);
        activityDetailBinding.texViewEmergencyContact.setEnabled(true);
        activityDetailBinding.texViewCity.setEnabled(true);
        activityDetailBinding.texViewSchool.setEnabled(true);

        activityDetailBinding.texViewImage.setText("Change ID");

        activityDetailBinding.imageViewDe.setOnClickListener(new View.OnClickListener() {
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
        activityDetailBinding.texViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new com.example.hostelproject.ImageUtils.ChooseImageFragmentSecond();
                dialogFragment.show(getSupportFragmentManager(), "mine");
            }
        });
        activityDetailBinding.texViewDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new com.example.hostelproject.DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");

            }
        });

    }

    public void Update() {
        String fNameIntent = activityDetailBinding.texViewFirstName.getText().toString().trim();
        String mNameIntent = activityDetailBinding.texViewMiddleName.getText().toString().trim();
        String sNameIntent = activityDetailBinding.texViewSurName.getText().toString().trim();
        String emailIntent = activityDetailBinding.texViewEmail.getText().toString().trim();
        String phoneIntent = activityDetailBinding.texViewPhone.getText().toString().trim();
        String cityIntent = activityDetailBinding.texViewCity.getText().toString().trim();
        String parentsPhoneIntent = activityDetailBinding.texViewParentContact.getText().toString().trim();
        String parentsNameIntent = activityDetailBinding.texViewParentName.getText().toString().trim();
        String emergencyIntent = activityDetailBinding.texViewEmergencyContact.getText().toString().trim();
        String dobIntent = activityDetailBinding.texViewDob.getText().toString().trim();
        String school = activityDetailBinding.texViewSchool.getText().toString().trim();


        Intent intent = new Intent();
        intent.putExtra(First_Name, fNameIntent);
        intent.putExtra(Middle_Name, mNameIntent);
        intent.putExtra(Sur_Name, sNameIntent);
        intent.putExtra(Email, emailIntent);
        intent.putExtra(phone, phoneIntent);
        intent.putExtra(city, cityIntent);
        intent.putExtra(parents_Name, parentsNameIntent);
        intent.putExtra(parents_Phone, parentsPhoneIntent);
        intent.putExtra(dob, dobIntent);
        intent.putExtra(emergency, emergencyIntent);
        intent.putExtra(Constants.school,school);


        if (profileImage != null && !profileImage.isEmpty()) {
            intent.putExtra(profile_Picture, profileImage);
            if (profile != null && !profile.isEmpty()) {
                DeletePhoto();
            }

        } else {
            intent.putExtra(profile_Picture, profile);
        }
        if (idImageRevised != null && !idImageRevised.isEmpty()) {
            intent.putExtra(idImage, idImageRevised);
            if (idImageUrl != null && !idImageUrl.isEmpty()) {
                DeletePhotoId();
            }
        } else {
            intent.putExtra(idImage, idImageUrl);
        }
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void DeletePhotoId() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(idImageUrl);
        storageReference.delete();
    }


    @Override
    public void sendInput(byte[] bytes) {
        bytesProfileImage = bytes;
        UploadImage();

    }

    void UploadImage() {
        if (user != null) {
            StorageReference profileImageRef =
                    FirebaseStorage.getInstance().getReference(user.getUid() + "ProfilePictures/" + currentTimeMillis() + ".jpg");
            if (bytesProfileImage != null) {
                activityDetailBinding.progressbarUpdate.setVisibility(View.VISIBLE);
                profileImageRef.putBytes(bytesProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(DetailActivity.this, "Profile Picture uploaded", Toast.LENGTH_LONG).show();

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while
                        (!urlTask.isSuccessful()) ;
                        Uri downloadUrl = urlTask.getResult();
                        profileImage = downloadUrl.toString();
                        save.setVisible(true);
                        activityDetailBinding.progressbarUpdate.setVisibility(View.GONE);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytesProfileImage, 0, bytesProfileImage.length);
                        activityDetailBinding.imageViewDe.setImageBitmap(bitmap);

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        save.setVisible(false);
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
        String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());

        activityDetailBinding.texViewDob.setText(currentDateString);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void DeletePhoto() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(profile);
        storageReference.delete();
    }

    public void OpenId() {
        Intent intent = new Intent(DetailActivity.this, IdImageActivity.class);
        intent.putExtra("idImage", idImageUrl);
        startActivity(intent);
    }

    @Override
    public void sendInputSecond(byte[] bytes) {

        bytesId = bytes;
        uploadID();

    }

    private void uploadID() {

        if (user != null) {
            StorageReference profileImageRef =
                    FirebaseStorage.getInstance().getReference(user.getUid() + "ProfilePictures/" + currentTimeMillis() + ".jpg");
            if (bytesId != null) {
                activityDetailBinding.progressbar2.setVisibility(View.VISIBLE);
                profileImageRef.putBytes(bytesId).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(DetailActivity.this, "ID Picture uploaded", Toast.LENGTH_LONG).show();

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while
                        (!urlTask.isSuccessful()) ;
                        Uri downloadUrl = urlTask.getResult();
                        idImageRevised = downloadUrl.toString();
                        save.setVisible(true);
                        activityDetailBinding.progressbar2.setVisibility(View.GONE);

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        save.setVisible(false);
                    }
                });
            }
        }
    }
}