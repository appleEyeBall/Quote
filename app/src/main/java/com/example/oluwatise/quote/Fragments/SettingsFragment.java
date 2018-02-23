package com.example.oluwatise.quote.Fragments;


import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.icu.text.UnicodeSetSpanner;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.HelperClasses.Animations;
import com.example.oluwatise.quote.HelperClasses.JobSchedule;
import com.example.oluwatise.quote.HelperClasses.LocationHelper;
import com.example.oluwatise.quote.HelperClasses.SingleChoiceDialog;
import com.example.oluwatise.quote.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    RelativeLayout notifyMeContainer;
    TextView locationText;
    ImageView refreshLocationImg;
    TextView notifyMeTextView;
    TextView timeTextView;
    Switch notifyMeSwitch;
    Animations animations;
    String optionSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        animations = new Animations(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_settings, container, false);

        // RIPPLE EFFECT
        v.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) MainActivity.getMainActivity().getCoordinatorLayout();
                animations.circularRevealFragment(v, coordinatorLayout, right, top);
            }
        });

        notifyMeContainer = (RelativeLayout) v.findViewById(R.id.notifyMeContainer);
        locationText = (TextView) v.findViewById(R.id.locationText);
        refreshLocationImg = (ImageView) v.findViewById(R.id.refreshLocationImg);
        notifyMeTextView = (TextView) v.findViewById(R.id.notifyMeTextView);
        timeTextView = (TextView) v.findViewById(R.id.timeText);
        notifyMeSwitch = (Switch) v.findViewById(R.id.notifyMe);
        //let the user see the current cityName
        locationText.setText(MainActivity.getMainActivity().getSharedPreferences("cityName", Context.MODE_PRIVATE)
        .getString("city", null));

        decideSwitchState();
        refreshLocationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animations.rotateRefresh();
                LocationHelper locationHelper = new LocationHelper(getActivity());
               // LocationText gets set in the locationHelper above....
            }
        });


        notifyMeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog();
                singleChoiceDialog.show(getFragmentManager(), "singleChoice");
            }
        });
        notifyMeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            JobSchedule jobSchedule = new JobSchedule(getActivity());
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = getActivity().getSharedPreferences("futureTimeSharedPreference", Context.MODE_PRIVATE);
                String am_pmTime = preferences.getString("am_pmTime", null);
                if (isChecked) {
                    if (am_pmTime!=null && !jobSchedule.isScheduled()) {
                        jobSchedule.scheduleJobWithDelay();
                    }
                    else {
                        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog();
                        singleChoiceDialog.show(getFragmentManager(), "singleChoice");
                    }
                }
                else {
                    jobSchedule.storeIsScheduled(false);
                    JobScheduler jobScheduler = (JobScheduler) getActivity().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    jobScheduler.cancelAll();
                    // set the shared pref to false
                    SharedPreferences notificationSettings = getActivity().getSharedPreferences("notificationSetting", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = notificationSettings.edit();
                    boolean showNotification = notificationSettings.getBoolean("showNotification", false);
                    editor.putBoolean("showNotification", false);
                    editor.apply();
                    decideSwitchState();

                }
            }
        });

        return v;
    }


    public void setLocationText(String location) {
        locationText.setText(location);
        animations.stopRotateRefresh();
    }

    public ImageView getRefreshLocationImg(){
        return refreshLocationImg;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.getMainActivity().setToolbarTitle("EmPower");
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.getMainActivity().setToolbarTitle("Settings");
        animations = new Animations(getActivity());
        FloatingActionButton fab = MainActivity.getMainActivity().getFab();
        FloatingActionButton fab2 = MainActivity.getMainActivity().getFab2();
        animations.hideFab(fab2);
        animations.hideFab(fab);
    }

    public TextView getTimeTextView() {
        return timeTextView;
    }

    public Switch getNotifyMeSwitch() {
        return notifyMeSwitch;
    }
    public TextView getNotifyMeTextView() {
        return notifyMeTextView;
    }

    public void decideSwitchState(){
        SharedPreferences notificationSettings = getActivity().getSharedPreferences("notificationSetting", Context.MODE_PRIVATE);
        boolean showNotification = notificationSettings.getBoolean("showNotification", false);
        if (showNotification) {
            SharedPreferences timeSp = getActivity().getSharedPreferences("futureTimeSharedPreference", Context.MODE_PRIVATE);
            String time = timeSp.getString("am_pmTime", null);
            JobSchedule jobSchedule = new JobSchedule(getActivity());
            jobSchedule.updateSettingUi(time);
        }else {
            // restore Defaults
            notifyMeSwitch.setChecked(false);
            getNotifyMeTextView().setText("Don't send me messages");
            timeTextView.setText("");
        }
    }
}
