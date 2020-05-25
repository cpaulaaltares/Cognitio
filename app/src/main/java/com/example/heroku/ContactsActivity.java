package com.example.heroku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactsActivity extends AppCompatActivity {

    BottomNavigationView navView;
    RecyclerView myContactsList;
    ImageView findPeopleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        //navView = findViewById(R.id.nav_view);
        //navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //findPeopleBtn = findViewById(R.id.find_people_btn);
        //myContactsList = findViewById(R.id.contact_list);
        //myContactsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //findPeopleBtn.setOnClickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        
        validateUser();
        
    }

    private void validateUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        String currentUserId = "";
        reference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                {
                    Intent settingIntent = new Intent(ContactsActivity.this, UserProfileActivity.class);
                    startActivity(settingIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
