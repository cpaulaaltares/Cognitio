package com.example.heroku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class ProfileSetupActivity extends AppCompatActivity {

    //firebase
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //storage
    StorageReference storageReference;
    // path where images of userprofile and cover are stored
    String storagePath = "Users_Profile_Cover_Imgs/";

    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    String storagePermissions[];

    @BindView(R.id.iv_camera)
    ImageView iv_camera;

    @BindView(R.id.et_name)
    EditText et_nickname;

    @BindView(R.id.et_phone)
    EditText et_phone;

    @BindView(R.id.btn_continue)
    Button btn_continue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = getInstance().getReference();// firebase storage reference

        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    @OnClick(R.id.iv_camera)
    public void uploadPic(View view){
        pickFromGallery();
    }

    @OnClick(R.id.btn_continue)
    public void submit(View view) {
        FirebaseUser user = mAuth.getCurrentUser();

        final String name = et_nickname.getText().toString().trim();
        final String phone = et_phone.getText().toString().trim();
        //validate if user has entered something or not
        if (!TextUtils.isEmpty(name)&& !TextUtils.isEmpty(phone)) {


            HashMap<String, Object> result = new HashMap<>();
            result.put("name", name);
            result.put("phone", phone);

            databaseReference.child(user.getUid()).updateChildren(result)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(ProfileSetupActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(ProfileSetupActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                }
            });

            startActivity(new Intent(ProfileSetupActivity.this, DashboardActivity.class));
            finish();
        } else if (phone.length() != 11){
            Toast.makeText(ProfileSetupActivity.this, "Phone number must be 11 digits", Toast.LENGTH_SHORT).show();
        }
    }
}
