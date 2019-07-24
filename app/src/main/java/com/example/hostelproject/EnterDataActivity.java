package com.example.hostelproject;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

    private EditText editTextFName, editTextMName, editTextSName, editTextEmail, editTextPhone, editTextCity,
            editTextParentName, editTextParentPhone, editTextEmergency;
    private TextView textViewAddImage, textViewDob;
    private Button buttonSave;
    private ProgressBar progressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final int CAMERA_PIC_REQUEST = 1;
    public static final int CAMERA_PERMISSION = 2;
    public static final int STORAGE_PERMISSION = 3;
    public static final int STORAGE_REQUEST = 4;
    private byte[] mUploadBytes;
    private String profileImageUrl;
    private Context context;
    private CollectionReference dbRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_data);
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


        editTextCity = findViewById(R.id.field_city);
        editTextPhone = findViewById(R.id.field_phone_number);
        editTextEmail = findViewById(R.id.field_email);
        editTextSName = findViewById(R.id.field_sur_name);
        editTextMName = findViewById(R.id.field_middle_name);
        editTextFName = findViewById(R.id.field_first_name);
        editTextParentName = findViewById(R.id.field_parentsName);
        editTextParentPhone = findViewById(R.id.field_parent_phone_number);
        editTextEmergency = findViewById(R.id.field_Emergency);
        textViewDob = findViewById(R.id.field_dob);
        textViewAddImage = findViewById(R.id.field_photo);
        buttonSave = findViewById(R.id.btn_ok);
        progressBar = findViewById(R.id.progressbar);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });
        textViewDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new com.example.hostelproject.DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        textViewAddImage.setOnClickListener(new View.OnClickListener() {
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

        String Fname = editTextFName.getText().toString().trim();
        String Mname = editTextMName.getText().toString().trim();
        String Sname = editTextSName.getText().toString().trim();
        String Email = editTextEmail.getText().toString().trim();
        String Phone = editTextPhone.getText().toString().trim();
        String City = editTextCity.getText().toString().trim();
        String ParentName = editTextParentName.getText().toString().trim();
        String ParentsPhone = editTextParentPhone.getText().toString().trim();
        String EmergencyContact = editTextEmergency.getText().toString().trim();
        String Dob = textViewDob.getText().toString().trim();

        if (Fname.isEmpty()) {
            editTextFName.setError("First Name is required");
            editTextFName.requestFocus();
            return;
        }
        if (Mname.isEmpty() && Sname.isEmpty()) {
            Toast.makeText(EnterDataActivity.this, "Surname or Middle Name is required", Toast.LENGTH_LONG).show();
            return;
        }
        if (City.isEmpty()) {
            editTextCity.setError("Location is Required");
            editTextCity.requestFocus();
            return;
        }
        if (Phone.isEmpty()) {
            editTextPhone.setError("Phone Number is Required");
            editTextPhone.requestFocus();
            return;
        }
        if (ParentName.isEmpty()) {
            editTextParentName.setError("Parent's/Guardian's Name is Required");
            editTextParentName.requestFocus();
            return;
        }
        if (ParentsPhone.isEmpty()) {
            editTextParentPhone.setError("Parent's/Guardian's Phone number is Required");
            editTextParentPhone.requestFocus();
            return;
        }
        if (Dob.isEmpty()) {
            textViewDob.setError("Date of Birth is Required");
            textViewDob.requestFocus();
            return;
        } else {
            textViewDob.setError(null);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        Date date = Calendar.getInstance().getTime();
        String dates = format.format(date);
        try {
            date = formatter.parse(dates);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (user != null) {
            dbRef = db.collection(user.getUid() + "Archives");
            Item items = new Item(Fname, Mname, Sname, Email, Phone, City, ParentName, ParentsPhone, Dob, EmergencyContact, profileImageUrl, date);
            progressBar.setVisibility(View.VISIBLE);
            dbRef.add(items).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(EnterDataActivity.this, "Saved", LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
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

        textViewDob.setText(currentDateString);
    }

    public void clearViews() {
        editTextEmergency.getText().clear();
        editTextParentPhone.getText().clear();
        editTextParentName.getText().clear();
        editTextFName.getText().clear();
        editTextCity.getText().clear();
        editTextMName.getText().clear();
        editTextSName.getText().clear();
        editTextEmail.getText().clear();
        textViewDob.setText(null);
        textViewAddImage.setText(null);
        editTextPhone.getText().clear();
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
                progressBar.setVisibility(View.VISIBLE);
                profileImageRef.putBytes(mUploadBytes)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(EnterDataActivity.this, "Profile picture uploaded", Toast.LENGTH_SHORT).show();

                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while
                                (!urlTask.isSuccessful()) ;
                                Uri downloadUrl = urlTask.getResult();
                                profileImageUrl = downloadUrl.toString();
                                textViewAddImage.setVisibility(View.VISIBLE);
                                textViewAddImage.setEnabled(true);
                                textViewAddImage.setText("Profile Picture Uploaded");
                                progressBar.setVisibility(View.GONE);
                                buttonSave.setEnabled(true);

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
                        progressBar.getProgress();
                        buttonSave.setEnabled(false);
                        textViewAddImage.setText("Uploading Profile Picture...");
                        textViewAddImage.setEnabled(false);
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