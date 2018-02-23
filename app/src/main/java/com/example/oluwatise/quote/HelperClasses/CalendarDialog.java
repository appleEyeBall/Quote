package com.example.oluwatise.quote.HelperClasses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Oluwatise on 1/24/2018.
 */

public class CalendarDialog extends DialogFragment {

    public static CalendarDialog newInstance(String selectedItemString) {
        CalendarDialog thisDialog = new CalendarDialog();
        // Note any int value can be used for year, month and day. If the caller is "SingleChoiceDialog"

        Bundle args = new Bundle();
        args.putString("selectedItemString", selectedItemString);
        thisDialog.setArguments(args);
        return thisDialog;
    }


    DatePicker datePicker;
    String selectedItemString;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        if (getArguments()!= null) {
            selectedItemString = getArguments().getString("selectedItemString");
        }
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.picker_calendar,null);
        datePicker = (DatePicker) v.findViewById(R.id.datePicker);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MaterialDialog);
        builder.setView(v);
        builder.setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = datePicker.getYear();
                                int mon = datePicker.getMonth();
                                int day = datePicker.getDayOfMonth();
                                ClockDialog clockDialog = ClockDialog.newInstance(selectedItemString, year, mon, day);
                                clockDialog.show(getFragmentManager(), "showClockDialog");
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.getMainActivity().getSettingsFragment().decideSwitchState();
                    }
                });

                return builder.create();

    }
}
