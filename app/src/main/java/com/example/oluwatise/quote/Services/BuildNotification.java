package com.example.oluwatise.quote.Services;

import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.Fragments.SettingsFragment;
import com.example.oluwatise.quote.HelperClasses.JobSchedule;
import com.example.oluwatise.quote.HelperClasses.QuoteObject;
import com.example.oluwatise.quote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oluwatise on 1/25/2018.
 */

public class BuildNotification extends AsyncTask<Void, Void, Boolean> {
    JobService jobService;
    private String userName;
    private String message;
    private int position;
    private String docID;
    DocumentSnapshot latestQuoteDocument;
    final int DELAY_JOB_ID = 100;
    final int INTERVAL_JOB_ID = 111;
    final int NOTIFICATIONID = 111;
    JobParameters params;
    String intervalString;

    public BuildNotification(JobService jobService, JobParameters params, String userName, String intervalString) {
        this.jobService = jobService;
        this.params = params;
        this.userName = userName;
        this.intervalString = intervalString;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        getLatestQuoteFromDb();
        return Boolean.TRUE;

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        jobService.jobFinished(params, aBoolean);

        JobScheduler jobScheduler = (JobScheduler) jobService.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(DELAY_JOB_ID);      // cancel the initial jobScheduler
        startNewScheduler();
        Log.v("POWER", "Jobs canceled and new job created");

    }

    private void getLatestQuoteFromDb() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("quotes").orderBy("timeStamp", Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    docID = documentSnapshot.getId();
                    QuoteObject quoteObject = documentSnapshot.toObject(QuoteObject.class);
                    message = quoteObject.getMsg();
                    latestQuoteDocument = documentSnapshot;
                    Log.v("POWER", "Notification data gotten");
                    // I am calling buildNotification here cuz this whole method
                    // runs on a different thread. and we wanna call 'buildNotification'
                    // only when 'message' variable has a value
                    buildNotification();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("POWER", "AsyncTask cannot get data " + e);
            }
        });
    }

    private void buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(jobService.getApplicationContext());
        Intent resultIntent = new Intent(jobService.getApplicationContext(), MainActivity.class);
        resultIntent.putExtra("notificationSelection", "readFragment");
        resultIntent.putExtra("docID", docID);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(jobService.getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) jobService.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Hi! " + userName)
                .setContentText(message)
                .setContentIntent(resultPendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATIONID, builder.build());
            Log.v("POWER", "notification built");

        } else {
            Log.v("POWER", "notification manager is null");

        }
    }

    private void startNewScheduler() {
        JobSchedule jobSchedule = new JobSchedule(jobService.getApplicationContext());
        jobSchedule.scheduleJobWithInterval(intervalString);
    }



}

