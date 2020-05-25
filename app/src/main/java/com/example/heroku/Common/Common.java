package com.example.heroku.Common;

import com.example.heroku.models.ModelOffice;
import com.example.heroku.models.ModelUser;
import com.example.heroku.models.PsychologyCategory;
import com.example.heroku.models.TimeSlot;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Common {
    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_OFFICE_PLACE = "OFFICE_SAVE";
    public static final String KEY_OFFICE_LOAD_DONE= "OFFICE_LOAD_DONE";
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static final String KEY_PSYCHOLOGYCATEGORY_SELECTED = "PSYCHOLOGYCATEGORY_SELECTED";
    public static final int Time_SLOT_TOTAL = 9;
    public static final Object DISABLE_TAG = "DISABLE";
    public static final String KEY_TIME_SLOT = "TIME_SLOT";
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static String IS_LOGIN = "IsLogin";
    public static ModelUser currentUser;
    public static ModelOffice currentOffice;
    public static int step = 0;
    public static String city = "";
    public static PsychologyCategory currentPsychoTherapist;
    public static int currentTimeSlot=-1;
    //public static Calendar currentDate = Calendar.getInstance();
    public static Calendar bookingDate = Calendar.getInstance();
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");

    public static String convertTimeSlotToString(int slot) {
        switch (slot){
            case 0:
                return "9:00 - 10:00";
            case 1:
                return "10:00 - 11:00";
            case 2:
                return "11:00 - 12:00";
            case 3:
                return "1:00 - 2:00";
            case 4:
                return "2:00 - 3:00";
            case 5:
                return "3:00 - 4:00";
            case 6:
                return "4:00 - 5:00";
            case 7:
                return "5:00 - 6:00";
            default:
                return "Closed";
        }
    }
}
