package com.example.hostelproject.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.hostelproject.ImageUtils.ChooseImageFragment;
import com.example.hostelproject.R;
import com.example.hostelproject.databinding.ActivityEnterDataBinding;
import com.example.hostelproject.models.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.widget.Toast.LENGTH_SHORT;
import static java.lang.System.currentTimeMillis;

public class EnterDataActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, ChooseImageFragment.OnInputListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final int CAMERA_PIC_REQUEST = 1;
    public static final int CAMERA_PERMISSION = 2;
    public static final int STORAGE_PERMISSION = 3;
    public static final int STORAGE_REQUEST = 4;
    private byte[] mUploadBytes;
    private String profileImageUrl;
    private ActivityEnterDataBinding enterDataBinding;
    private Context context;
    private CollectionReference dbRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enterDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_enter_data);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        Toolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            setTitle("Enter Data");
        }
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        enterDataBinding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });
        enterDataBinding.fieldDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new com.example.hostelproject.DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        enterDataBinding.fieldPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoader();

            }
        });
    }


    private void showLoader() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("MyDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        ChooseImageFragment dialog = new ChooseImageFragment();
        dialog.show(ft, "MyDialog");

    }


    public void saveItem() {
        String fName = enterDataBinding.fieldFirstName.getText().toString().trim();
        String mName = enterDataBinding.fieldMiddleName.getText().toString().trim();
        String sName = enterDataBinding.fieldSurName.getText().toString().trim();
        String Email = enterDataBinding.fieldEmail.getText().toString().trim();
        String Phone = enterDataBinding.fieldPhoneNumber.getText().toString().trim();
        String City = enterDataBinding.fieldCity.getText().toString().trim();
        String ParentName = enterDataBinding.fieldParentsName.getText().toString().trim();
        String ParentsPhone = enterDataBinding.fieldParentPhoneNumber.getText().toString().trim();
        String EmergencyContact = enterDataBinding.fieldEmergency.getText().toString().trim();
        String Dob = enterDataBinding.fieldDob.getText().toString().trim();

        if (fName.isEmpty()) {
            enterDataBinding.fieldFirstName.setError("First Name is required");
            enterDataBinding.fieldFirstName.requestFocus();
            return;
        }
        if (mName.isEmpty() && sName.isEmpty()) {
            Toast.makeText(EnterDataActivity.this, "Surname or Middle Name is required", Toast.LENGTH_LONG).show();
            return;
        }
        if (City.isEmpty()) {
            enterDataBinding.fieldCity.setError("Location is Required");
            enterDataBinding.fieldCity.requestFocus();
            return;
        }
        if (Phone.isEmpty()) {
            enterDataBinding.fieldPhoneNumber.setError("Phone Number is Required");
            enterDataBinding.fieldPhoneNumber.requestFocus();
            return;
        }
        if (ParentName.isEmpty()) {
            enterDataBinding.fieldParentsName.setError("Parent's/Guardian's Name is Required");
            enterDataBinding.fieldParentsName.requestFocus();
            return;
        }
        if (ParentsPhone.isEmpty()) {
            enterDataBinding.fieldParentPhoneNumber.setError("Parent's/Guardian's Phone number is Required");
            enterDataBinding.fieldParentPhoneNumber.requestFocus();
            return;
        }
        if (Dob.isEmpty()) {
            enterDataBinding.fieldDob.setError("Date of Birth is Required");
            enterDataBinding.fieldDob.requestFocus();
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Date date = Calendar.getInstance().getTime();
        try {
            date = formatter.parse(date.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("EnterData", "DATE " + date);

        if (user != null) {
            dbRef = db.collection(user.getUid() + "Archives");
            Item items = new Item(fName, mName, sName, Email, Phone, City, ParentName, ParentsPhone, Dob, EmergencyContact, profileImageUrl, date);
            enterDataBinding.progressbar.setVisibility(View.VISIBLE);
            dbRef.add(items).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(EnterDataActivity.this, "Saved", LENGTH_SHORT).show();
                    enterDataBinding.progressbar.setVisibility(View.GONE);
                    clearViews();
                }
            });
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());

        enterDataBinding.fieldDob.setText(currentDateString);
    }

    public void clearViews() {
        enterDataBinding.fieldEmergency.getText().clear();
        enterDataBinding.fieldParentPhoneNumber.getText().clear();
        enterDataBinding.fieldParentsName.getText().clear();
        enterDataBinding.fieldFirstName.getText().clear();
        enterDataBinding.fieldCity.getText().clear();
        enterDataBinding.fieldMiddleName.getText().clear();
        enterDataBinding.fieldSurName.getText().clear();
        enterDataBinding.fieldEmail.getText().clear();
        enterDataBinding.fieldDob.setText(null);
        enterDataBinding.fieldPhoto.setText(null);
        enterDataBinding.fieldPhoneNumber.getText().clear();
    }

    @Override
    public void sendInput(byte[] bytes) {

        mUploadBytes = bytes;
        uploadImageToFireBaseStorage();
    }

    public void uploadImageToFireBaseStorage() {
        if (user != null) {
            StorageReference profileImageRef =
                    FirebaseStorage.getInstance().getReference(user.getUid() + "ProfilePictures/" + currentTimeMillis() + ".jpg");

            if (mUploadBytes != null) {
                enterDataBinding.progressbar.setVisibility(View.VISIBLE);
                profileImageRef.putBytes(mUploadBytes)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(EnterDataActivity.this, "Profile picture uploaded", Toast.LENGTH_SHORT).show();

                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while
                                (!urlTask.isSuccessful());
                                    Uri downloadUrl = urlTask.getResult();
                                    profileImageUrl = downloadUrl.toString();
                                    enterDataBinding.fieldPhoto.setVisibility(View.VISIBLE);
                                    enterDataBinding.fieldPhoto.setEnabled(true);
                                    enterDataBinding.fieldPhoto.setText("Profile Picture Uploaded");
                                    enterDataBinding.progressbar.setVisibility(View.GONE);
                                    enterDataBinding.btnOk.setEnabled(true);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        enterDataBinding.progressbar.getProgress();
                        enterDataBinding.btnOk.setEnabled(false);
                        enterDataBinding.fieldPhoto.setText("Uploading Profile Picture...");
                        enterDataBinding.fieldPhoto.setEnabled(false);
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


}