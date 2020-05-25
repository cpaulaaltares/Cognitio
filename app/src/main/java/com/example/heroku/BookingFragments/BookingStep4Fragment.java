package com.example.heroku.BookingFragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.heroku.Common.Common;
import com.example.heroku.MainActivity;
import com.example.heroku.R;
import com.example.heroku.models.BookingInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BookingStep4Fragment extends Fragment {

    CollectionReference userRef;

    String postId,myEmail, myName, myUid;
    private Button buttonNext;
    FirebaseAuth mAuth;
    SimpleDateFormat simpleDateFormat;
    LocalBroadcastManager localBroadcastManager;
    Unbinder unbinder;

    AlertDialog dialog;

    @BindView(R.id.txt_booking_time_text)
    TextView txt_booking_time_text;
    @BindView(R.id.txt_booking_psychologist_text)
    TextView txt_booking_psychologist_text;
//    @BindView(R.id.txt_psychologist_name)
//    TextView txt_psychologist_name;
    @BindView(R.id.txt_office_name)
    TextView txt_office_name;
    @BindView(R.id.txt_office_address)
    TextView txt_office_address;
    @BindView(R.id.txt_office_open_hours)
    TextView txt_office_open_hours;
    @BindView(R.id.txt_office_phone)
    TextView txt_office_phone;
    @BindView(R.id.txt_office_website)
    TextView txt_office_website;
    @BindView(R.id.btn_confirm)
    Button btn_confirm;


    BroadcastReceiver confirmBookingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    private void setData() {
        txt_booking_psychologist_text.setText(Common.currentPsychoTherapist.getName());
        txt_booking_time_text.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
        .append("a t ")
        .append(simpleDateFormat.format(Common.bookingDate.getTime())));

        txt_office_address.setText(Common.currentOffice.getAddress());
        txt_office_website.setText(Common.currentOffice.getWebsite());
        txt_office_name.setText(Common.currentOffice.getName());
        txt_office_open_hours.setText(Common.currentOffice.getOpenHours());
    }

    static BookingStep4Fragment instance;
    public static BookingStep4Fragment getInstance(){
        if(instance == null)
            instance = new BookingStep4Fragment();
        return instance;
    }
    private void checkUserStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            //user is signed in
            myEmail = user.getEmail();
            myUid = user.getUid();
            myName = user.getDisplayName();
        }
        else {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //get id of post using intent
        //Intent intent = getIntent();



        //Apply format for date display on Confirm
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmBookingReceiver, new IntentFilter(Common.KEY_CONFIRM_BOOKING));

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();


    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_booking_step_four, container, false);

        unbinder = ButterKnife.bind(this, itemView);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
                //Process Timestamp
                //Timestamp filter all booking with date is greater today
                //Display all future booking
                String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
                String[] convertTime = startTime.split("-");
                //Get start time : get 9:00
                String[] startTimeConvert = convertTime[0].split(":");
                int startHourInt = Integer.parseInt(startTimeConvert[0].trim()); // value should get 9
                int startMinInt = Integer.parseInt(startTimeConvert[1].trim()); // value should get 00

                Calendar bookingDateWithourHouse = Calendar.getInstance();
                bookingDateWithourHouse.setTimeInMillis(Common.bookingDate.getTimeInMillis());
                bookingDateWithourHouse.set(Calendar.HOUR_OF_DAY,startHourInt);
                bookingDateWithourHouse.set(Calendar.MINUTE,startMinInt);

                //Create timestamp object and apply to BookingInformation
                Timestamp timestamp = new Timestamp(bookingDateWithourHouse.getTime());


                //Create Booking Information
                //FirebaseUser user = mAuth.getCurrentUser();
                final BookingInformation bookingInformation = new BookingInformation();

                bookingInformation.setDone(false); //set to false, to filter the display

                bookingInformation.setPsychologistId(Common.currentPsychoTherapist.getCategoryID());
                bookingInformation.setPsychologistName(Common.currentPsychoTherapist.getName());
                bookingInformation.setCustomerName(myName);
                bookingInformation.setCustomerPhone(Common.currentPsychoTherapist.getCategoryID());
                //bookingInformation.setCustomerPhone(Common.currentUser.getPhone());


                bookingInformation.setOfficeId(Common.currentOffice.getOfficeId());
                bookingInformation.setOfficeName(Common.currentOffice.getName());
                bookingInformation.setOfficeAddress(Common.currentOffice.getAddress());
                bookingInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                        .append(" at ")
                        .append(simpleDateFormat.format(Common.bookingDate.getTime())).toString());

                bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));

                //Submit to Psychologist document
                DocumentReference bookingDate =  FirebaseFirestore.getInstance()
                        .collection("PsychoTherapist")
                        .document(Common.city)
                        .collection("Branch")
                        .document(Common.currentOffice.getOfficeId())
                        .collection("Psychometrician")
                        .document(Common.currentPsychoTherapist.getCategoryID())
                        .collection(Common.simpleDateFormat.format(Common.bookingDate.getTime()))
                        .document(String.valueOf(Common.currentTimeSlot));

                //Write data
                bookingDate.set(bookingInformation)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Write a function to check if it already exist to our booking service
                                addToUserBooking(bookingInformation);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


        return itemView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main,menu);

        //SearchView
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        menu.findItem(R.id.action_add_post).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void addToUserBooking(final BookingInformation bookingInformation) {

        //DocumentReference currentUser = userRef.document().toString();
        //Create new collection
        final CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("User")
                .document("HtG8rkybJWdD1UskX61T34uSn3m1")
                .collection("Booking");
        /*final CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("Users");*/
        /*final DatabaseReference userBooking = FirebaseDatabase.getInstance()
                .getReference("Tokens");*/


        userBooking.whereEqualTo("done", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty())
                        {
                            //Set data
                            userBooking.document()
                                    .set(bookingInformation)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (dialog.isShowing())
                                                dialog.dismiss();

                                            addToCalendar(Common.bookingDate,Common.convertTimeSlotToString(Common.currentTimeSlot));
                                            resetStaticData();
                                            getActivity().finish();
                                            Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else
                        {
                            if (dialog.isShowing())
                                dialog.dismiss();

                            resetStaticData();
                            getActivity().finish();
                            Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addToCalendar(Calendar bookingDate, String startDate) {
        String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
        String[] convertTime = startTime.split("-"); // Split ex: 9:00 - 10:00
        //Get start time : get 9:00
        String[] startTimeConvert = convertTime[0].split(":");
        int startHourInt = Integer.parseInt(startTimeConvert[0].trim()); // value should get 9
        int startMinInt = Integer.parseInt(startTimeConvert[1].trim()); // value should get 00

        String[] endTimeConvert = convertTime[1].split(":");
        int endHourInt = Integer.parseInt(startTimeConvert[0].trim()); // value should get 10
        int endMinInt = Integer.parseInt(startTimeConvert[1].trim()); // value should get 00

        Calendar startEvent = Calendar.getInstance();
        startEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        startEvent.set(Calendar.HOUR_OF_DAY, startHourInt); //set event start hour
        startEvent.set(Calendar.MINUTE, startMinInt); //set event start min

        Calendar endEvent = Calendar.getInstance();
        endEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        endEvent.set(Calendar.HOUR_OF_DAY, startHourInt); //set event start hour
        endEvent.set(Calendar.MINUTE, startMinInt); //set event start min

        //After start and endevent, convert it to format string
        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        String startEventTime = calendarDateFormat.format(startEvent.getTime());
        String endEventTime = calendarDateFormat.format(endEvent.getTime());

        addToDeviceCalendar(startEventTime, endEventTime, "Office Booking",
                new StringBuilder("Office from")
                .append(startTime)
                .append("  with  ")
                .append(Common.currentPsychoTherapist.getName())
                .append("  at  ")
                .append(Common.currentOffice.getName()).toString(),
                    new StringBuilder(" Address: ").append(Common.currentOffice.getAddress()).toString());

    }

    private void addToDeviceCalendar(String startEventTime, String endEventTime, String title, String description, String location) {
        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        try {
            Date start = calendarDateFormat.parse(startEventTime);
            Date end = calendarDateFormat.parse(endEventTime);

            ContentValues event = new ContentValues();

            //Put
            event.put(CalendarContract.Events.CALENDAR_ID,getCalendar(getContext()));
            event.put(CalendarContract.Events.TITLE,title);
            event.put(CalendarContract.Events.DESCRIPTION,description);
            event.put(CalendarContract.Events.EVENT_LOCATION,location);

            //Time
            event.put(CalendarContract.Events.DTSTART,start.getTime());
            event.put(CalendarContract.Events.DTEND,end.getTime());
            event.put(CalendarContract.Events.ALL_DAY,0);
            event.put(CalendarContract.Events.HAS_ALARM,1);

            String timeZone = TimeZone.getDefault().getID();
            event.put(CalendarContract.Events.EVENT_TIMEZONE,timeZone);

            Uri calendars;
            if (Build.VERSION.SDK_INT >= 8){
                calendars = Uri.parse("content://com.android.calendar/events");
            } else {
                calendars = Uri.parse("content://calendars/events");
            }

            getActivity().getContentResolver().insert(calendars,event);

        } catch (ParseException e){
            e.printStackTrace();
        }
    }

    private String getCalendar(Context context) {
        //Get default calendar ID of Calendar of Gmail
        String gmailIdCalendar = "";
        String projection[]= {"_id","calendar_displayName"};
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = context.getContentResolver();
        //Select all calendar
        Cursor managedCursor = contentResolver.query(calendars,projection,null,null,null);
        if (managedCursor.moveToFirst())
        {
            String calName;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);
            do {
                calName = managedCursor.getString(nameCol);
                if (calName.contains("@gmail.com"))
                {
                    gmailIdCalendar = managedCursor.getString(idCol);
                    break; // Exit as soon as have id
                }
            } while (managedCursor.moveToNext());
            managedCursor.close();
        }
        return  gmailIdCalendar;
    }

    private void resetStaticData() {
        Common.step = 0;
        Common.currentTimeSlot = 0;
        Common.currentOffice = null;
        Common.currentPsychoTherapist = null;
        Common.bookingDate.add(Calendar.DATE,0); //Current date added
    }
}
