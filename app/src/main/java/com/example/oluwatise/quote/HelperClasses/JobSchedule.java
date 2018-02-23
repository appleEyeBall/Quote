package com.example.oluwatise.quote.HelperClasses;

import android.app.Fragment;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.Fragments.SettingsFragment;
import com.example.oluwatise.quote.R;
import com.example.oluwatise.quote.Services.ScheduleNotificationService;

import java.util.concurrent.TimeUnit;

/**
 * Created by Oluwatise on 1/26/2018.
 */

public class JobSchedule {
    Context context;
    final int DELAY_JOB_ID = 100;
    final int INTERVAL_JOB_ID = 111;
    public JobSchedule(Context context) {
        this.context = context;
    }

    public void scheduleJobWithDelay() {
        SharedPreferences preferences = context.getSharedPreferences("futureTimeSharedPreference", Context.MODE_PRIVATE);
        String am_pmTime = preferences.getString("am_pmTime", null);
        //set jobScheduler
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(context, ScheduleNotificationService.class);
        JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(DELAY_JOB_ID, componentName);
        long setTime = preferences.getLong("futureTimeStamp", System.currentTimeMillis());
        long delay = setTime - System.currentTimeMillis();
        jobInfoBuilder.setMinimumLatency(delay);
        jobInfoBuilder.setPersisted(true);
        int resultCode = jobScheduler.schedule(jobInfoBuilder.build());
        if (resultCode== JobScheduler.RESULT_SUCCESS) {
            Log.v("POWER", "FROM Jobschedule_delay, jobscheduler created");
            storeIsScheduled(true);  //store true/false in shared prefs
            // update the UI
            updateSettingUi(am_pmTime);
        }else {
            Log.v("POWER", "FROM Jobschedule_delay, jobscheduler failed");
            storeIsScheduled(false);  //store true/false in shared prefs
        }
    }

    public void scheduleJobWithInterval(String interval){
        long timeInMilli;
        if (interval ==  null) {
            interval = "Daily";
        }
        if (interval.equals("Hourly")){
            timeInMilli = TimeUnit.HOURS.toMillis(1);
        }
        else if (interval.equals("Daily")) {
            timeInMilli = TimeUnit.DAYS.toMillis(1);
        }
        else if (interval.equals("Weekly")) {
            timeInMilli = TimeUnit.DAYS.toMillis(7);

        }
        else {
            // i.e monthly
            timeInMilli = TimeUnit.DAYS.toMillis(30);
        }
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(context, ScheduleNotificationService.class);
        JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(INTERVAL_JOB_ID, componentName);
        Log.v("POWER", "New interval is "+ String.valueOf(timeInMilli));
        jobInfoBuilder.setMinimumLatency(timeInMilli);
        jobInfoBuilder.setPersisted(true);
        int resultCode = jobScheduler.schedule(jobInfoBuilder.build());
        if (resultCode== JobScheduler.RESULT_SUCCESS) {
            Log.v("POWER", "FROM Jobschedule_interval, jobscheduler created");
        }else {
            Log.v("POWER", "FROM Jobschedule_interval, jobscheduler failed");
        }

    }

    public void updateSettingUi(String value){
        Fragment fragment = MainActivity.getMainActivity().getFragmentManager().findFragmentById(R.id.mainFragmentContainer);
        if (fragment instanceof SettingsFragment) {
            SettingsFragment settingsFragment = (SettingsFragment) fragment;
            settingsFragment.getTimeTextView().setText(value);
            settingsFragment.getNotifyMeSwitch().setChecked(true);
            settingsFragment.getNotifyMeTextView().setText(R.string.sendMessages);
            settingsFragment.getNotifyMeTextView().setTextColor(context.getResources().getColor(R.color.white));
        }

    }

    public void storeIsScheduled(boolean shouldShowNotification) {
        SharedPreferences notificationSettings;
        SharedPreferences.Editor notificationSettingseditor;
        notificationSettings = context.getSharedPreferences("notificationSetting", Context.MODE_PRIVATE);
        notificationSettingseditor = notificationSettings.edit();
        if (shouldShowNotification) {
            notificationSettingseditor.putBoolean("showNotification", true);
        }
        else {
            notificationSettingseditor.putBoolean("showNotification", false);
        }
        notificationSettingseditor.apply();
    }

    public boolean isScheduled(){
        SharedPreferences notificationSettings = context.getSharedPreferences("notificationSetting", Context.MODE_PRIVATE);
        return notificationSettings.getBoolean("showNotification", false);
    }

    public int getINTERVAL_JOB_ID(){
        return INTERVAL_JOB_ID;
    }
    public void setDefaultValues() {

    }
}
