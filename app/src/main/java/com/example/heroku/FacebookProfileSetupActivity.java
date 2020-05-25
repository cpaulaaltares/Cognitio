package com.example.heroku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class FacebookProfileSetupActivity extends AppCompatActivity {

    //firebase
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //storage

    // path where images of userprofile and cover are stored
    String storagePath = "Users_Profile_Cover_Imgs/";

    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    String storagePermissions[];

    @BindView(R.id.iv_logo)
    ImageView iv_logo;

    @BindView(R.id.et_name)
    EditText et_nickname;

    @BindView(R.id.btn_continue)
    Button btn_continue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_profile_setup);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        //storageReference = getInstance().getReference();// firebase storage reference

        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        checkNickname(accessToken);

    }

    private void checkNickname(AccessToken accessToken) {
        if (user.getUid() != null) {
            String imgurl = "https://graph.facebook.com/"+accessToken.getUserId()+"/picture";
            Picasso.get().load(imgurl).into(iv_logo);
            String name = user.getDisplayName();
            et_nickname.setText(name);

        }
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    @OnClick(R.id.iv_logo)
    public void uploadPic(View view){
        pickFromGallery();
    }

    @OnClick(R.id.btn_continue)
    public void submit(View view) {
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    String name = ""+ ds.child("name").getValue();
                    //String email = ""+ ds.child("email").getValue();
                    String image = ""+ ds.child("image").getValue();

                    //set data
                    et_nickname.setText(name);
                    //phoneTv.setText(phone);

                    try {
                        //if image is received then set
                        Picasso.get().load(image).into(iv_logo);
                    }
                    catch (Exception e){
                        //set defined image
                        Picasso.get().load(R.drawable.ic_default_img_white).into(iv_logo);
                    }

                    startActivity(new Intent(FacebookProfileSetupActivity.this, DashboardActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                startActivity(new Intent(FacebookProfileSetupActivity.this, DashboardActivity.class));
                finish();
            }
        });

    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

}
