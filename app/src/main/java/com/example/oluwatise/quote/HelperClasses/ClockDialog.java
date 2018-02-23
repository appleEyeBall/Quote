package com.example.oluwatise.quote.HelperClasses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.Fragments.SettingsFragment;
import com.example.oluwatise.quote.R;
import com.example.oluwatise.quote.Services.ScheduleNotificationService;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Oluwatise on 1/24/2018.
 */

public class ClockDialog extends DialogFragment {

    public static ClockDialog newInstance(String selectedItemString, int year, int month, int day) {
        ClockDialog thisDialog = new ClockDialog();
        // Note any int value can be used for year, month and day. If the caller is "SingleChoiceDialog"

        //set default, if the caller is not a calender
        if (selectedItemString.equals("Hourly") || selectedItemString.equals("Daily")){
            year = Calendar.getInstance().get(Calendar.YEAR);
            month = Calendar.getInstance().get(Calendar.MONTH);
            day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }

        Bundle args = new Bundle();
        args.putString("selectedItemString", selectedItemString);
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        thisDialog.setArguments(args);
        return thisDialog;
    }



    String selectedItemString;
    int year, month, day, rawHour, rawMinute;
    String am_pmTime;
    final int JOB_ID = 100;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        if (getArguments()!= null) {
            selectedItemString = getArguments().getString("selectedItemString");
            year = getArguments().getInt("year");
            month = getArguments().getInt("month");
            day = getArguments().getInt("day");
            storeIntervalStringInSharedPreference(selectedItemString);
        }

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.picker_time, null);
        final TimePicker timePickerView = (TimePicker) v.findViewById(R.id.timePicker);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MaterialDialog);
        builder.setView(v)
        .setTitle("Choose start time")
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.getMainActivity().getSettingsFragment().decideSwitchState();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
                            rawHour = timePickerView.getHour();
                            rawMinute = timePickerView.getMinute();
                        }
                        else {
                            rawHour = timePickerView.getCurrentHour();
                            rawMinute = timePickerView.getCurrentMinute();
                        }
                        am_pmTime = getAm_pmVersion(rawHour, rawMinute);
                        if(storeFutureTimeInSharedPreference(rawHour, rawMinute, am_pmTime)) {
                            JobSchedule jobSchedule = new JobSchedule(getActivity());
                            jobSchedule.scheduleJobWithDelay();     //it'll update Ui as well as store true/false in sharedpref
                        }
                    }
                });
       return builder.create();
    }

    private String getAm_pmVersion(int hours, int mins) {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        String myTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return myTime;
    }

    private boolean storeFutureTimeInSharedPreference(int rawHour, int rawMinute, String am_pmTime) {
        Calendar calendar = new GregorianCalendar(year, month, day, rawHour, rawMinute);
        long timeStamp = calendar.getTimeInMillis();
        SharedPreferences futureTimeSharedPreference = getActivity().getSharedPreferences("futureTimeSharedPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = futureTimeSharedPreference.edit();
        editor.putLong("futureTimeStamp", timeStamp);
        editor.putString("am_pmTime", am_pmTime);
        editor.apply();
        return true;
    }
    public void storeIntervalStringInSharedPreference(String interval){
        SharedPreferences futureTimeSharedPreference = getActivity().getSharedPreferences("futureTimeSharedPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = futureTimeSharedPreference.edit();
        editor.putString("intervalString", interval);
        editor.apply();

    }



}
