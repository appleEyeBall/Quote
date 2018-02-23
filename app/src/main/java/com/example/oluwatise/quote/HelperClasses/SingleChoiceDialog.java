package com.example.oluwatise.quote.HelperClasses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.Fragments.SettingsFragment;
import com.example.oluwatise.quote.R;

/**
 * Created by Oluwatise on 1/24/2018.
 */

public class SingleChoiceDialog extends DialogFragment{
    private int selectedItem;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MaterialDialog);
        builder.setTitle("How often")
                .setItems(R.array.interval_Array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedItem = which;
                        String selectedItemString = getResources().getStringArray(R.array.interval_Array)[which].toString();
                        if (selectedItem<=1) {
                            ClockDialog clockDialog = ClockDialog.newInstance(selectedItemString,0,0,0);
                            clockDialog.show(getFragmentManager(), "showClockDialog");
                        }
                        else {
                            CalendarDialog calendarDialog = CalendarDialog.newInstance(selectedItemString);
                            calendarDialog.show(getFragmentManager(), "showCalendarDialog");
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.getMainActivity().getSettingsFragment().decideSwitchState();
            }
        });

        return builder.create();
    }
}
