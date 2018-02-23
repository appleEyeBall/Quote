package com.example.oluwatise.quote.Services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.oluwatise.quote.HelperClasses.JobSchedule;

import java.util.List;

/**
 * Created by Oluwatise on 1/24/2018.
 */

public class ScheduleNotificationService extends JobService {
    BuildNotification buildNotification;
    String intervalString;

    @Override
    public boolean onStartJob(JobParameters params) {
        // get username
        SharedPreferences sharedPreferences = this.getSharedPreferences("userName", Context.MODE_PRIVATE);
       String userName = sharedPreferences.getString("name", null);
       // get interval String
        SharedPreferences intervalpref = this.getSharedPreferences("futureTimeSharedPreference", Context.MODE_PRIVATE);
        intervalString = intervalpref.getString("intervalString", null);

       buildNotification = new BuildNotification(this, params, userName, intervalString);
       buildNotification.execute();
        Log.v("POWER", "Notification Job started");
        return true;
    }



    @Override
    public boolean onStopJob(JobParameters params) {
        if (buildNotification!=null){
            buildNotification.cancel(true);
            Log.v("POWER", "build notification canceled");
        }

        Log.v("POWER", "Notification Job STOPPED");

        return true;
    }

}


