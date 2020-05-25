package com.example.heroku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.heroku.Common.Common;
import com.example.heroku.Common.NonSwipeViewPager;
import com.example.heroku.adapters.MyStateAdapter;
import com.example.heroku.adapters.MyViewPagerAdapter;
import com.example.heroku.models.PsychologyCategory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import dmax.dialog.SpotsDialog;

public class BookingActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;
    AlertDialog dialog;
    CollectionReference officeRef;

    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.view_pager)
    NonSwipeViewPager viewPager;
    @BindView(R.id.btn_previous_step)
    Button btn_previous_step;
    @BindView(R.id.btn_next_step)
    Button btn_next_step;

    private Button buttonNext, buttonPrevious;

    //Event
//    @OnClick(R.id.btn_next_step)
//    void nextClick(){
//        Toast.makeText(this, ""+Common.currentOffice.getOfficeId(), Toast.LENGTH_SHORT).show();
//    }



    //Broadcast Receiver
    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int step = intent.getIntExtra(Common.KEY_STEP, 0);
            if (step == 1 )
            {
                Common.currentOffice = intent.getParcelableExtra(Common.KEY_OFFICE_PLACE);
            }

            else if (step == 2)
            {
                Common.currentPsychoTherapist = intent.getParcelableExtra(Common.KEY_PSYCHOLOGYCATEGORY_SELECTED);
            }
            else if (step == 3)
            {
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT, -1);
            }


            //Common.currentOffice = intent.getParcelableExtra(Common.KEY_OFFICE_PLACE);
            btn_next_step.setEnabled(true);
            setColorButton();

        }
    };

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(BookingActivity.this);

        dialog = new SpotsDialog.Builder().setContext(BookingActivity.this).setCancelable(false).build();//check issue

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver, new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));


        btn_next_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(BookingActivity.this, ""+Common.currentOffice.getOfficeId(), Toast.LENGTH_SHORT).show();
                if (Common.step < 3 || Common.step == 0)
                {
                    Common.step++; // increase
                    if(Common.step==1)//After Office
                    {
                        if (Common.currentOffice != null)
                            loadCategoryByOffice(Common.currentOffice.getOfficeId());
                    }
                    else if (Common.step == 2){ // Pick time slot
                        if (Common.currentPsychoTherapist != null)
                            loadTimeSlotOfPyschoTherapist(Common.currentPsychoTherapist.getCategoryID());
                    }
                    else if (Common.step == 3){ // Confirm
                        if (Common.currentTimeSlot != -1)
                            confirmBooking();
                    }
                    viewPager.setCurrentItem(Common.step);
                }
            }
        });

        btn_previous_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.step == 3 || Common.step > 0){
                    Common.step--;
                    viewPager.setCurrentItem(Common.step);
                    if (Common.step < 3) //Enable step button
                    {
                        btn_next_step.setEnabled(true);
                        setColorButton();
                    }
                }
            }
        });
        setupStepView();
        setColorButton();



        //View
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4);// Number of fragment that we keep state on our screen page on Booking Activity.... this will help to keep the state of our progress
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //show step
                stepView.go(position, true);
                if (position == 0)
                    btn_previous_step.setEnabled(false);
                else
                    btn_previous_step.setEnabled(true);

                //Set disable button next
                btn_next_step.setEnabled(false);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void confirmBooking() {
        //Send broadcast to fragment step four
        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadTimeSlotOfPyschoTherapist(String categoryID) {
        //send Local Broadcast to Fragment step 3
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadCategoryByOffice(String officeId) {
        dialog.show();

        //Now, select all PsychoTherapist of Offices
        // /PsychoTherapist/Manila/Branch/BdUegW65a7FKVLrrzBsp/Psychometrician
        //select all categorized specialist
        if (!TextUtils.isEmpty(Common.city)){
            officeRef = FirebaseFirestore.getInstance()
                    .collection("PsychoTherapist")
                    .document(Common.city)
                    .collection("Branch")
                    .document(officeId)
                    .collection("Psychometrician");

            officeRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            ArrayList<PsychologyCategory> psychCategories = new ArrayList<>();
                            for (QueryDocumentSnapshot categorySnapshot:task.getResult())
                            {
                                PsychologyCategory psychCategory = categorySnapshot.toObject(PsychologyCategory.class);
                                psychCategory.setPassword("");
                                psychCategory.setCategoryID(categorySnapshot.getId());

                                psychCategories.add(psychCategory);
                            }
                            //Send Broadcast to BookingStep2Fragment to load recycler
                            Intent intent = new Intent(Common.KEY_OFFICE_LOAD_DONE);
                            intent.putParcelableArrayListExtra(Common.KEY_OFFICE_LOAD_DONE, psychCategories);
                            localBroadcastManager.sendBroadcast(intent);

                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                }
            });
        }

    }

    private void setColorButton() {
        if(btn_next_step.isEnabled()){
            btn_next_step.setBackgroundResource(R.color.gray);
        } else {
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }

        if(btn_previous_step.isEnabled()){
            btn_previous_step.setBackgroundResource(R.color.gray);
        } else {
            btn_previous_step.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("Office");
        stepList.add("Therapist");
        stepList.add("Time");
        stepList.add("Confirm");
        stepView.setSteps(stepList);
        
    }


    
}
