package com.example.oluwatise.quote.Activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.drawable.RippleDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oluwatise.quote.Fragments.CreditsFragment;
import com.example.oluwatise.quote.Fragments.ReadFragment;
import com.example.oluwatise.quote.Fragments.SettingsFragment;
import com.example.oluwatise.quote.Fragments.WriteFragment;
import com.example.oluwatise.quote.HelperClasses.Animations;
import com.example.oluwatise.quote.Fragments.NameFragment;
import com.example.oluwatise.quote.HelperClasses.LocationHelper;
import com.example.oluwatise.quote.Manifest;
import com.example.oluwatise.quote.R;
import com.example.oluwatise.quote.Services.GetMostLikedMsgs;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.google.android.gms.ads.MobileAds;

import static android.support.v4.app.ActivityCompat.requestPermissions;

public class MainActivity extends AppCompatActivity {

    /*  ORDER OF OPERATION
    * All shared preferences are stored in the activity
    * */

    private final int PERMISSION_ACCESS_LOCATION = 100 ;
    android.app.FragmentManager fm;
    WriteFragment writeFragment;
    NameFragment nameFragment;
    ReadFragment readFragment;
    TextView mostLiked1; CardView mostLikedContainer1;
    TextView mostLiked2; CardView mostLikedContainer2;
    TextView mostLiked3; CardView mostLikedContainer3;
    int viewPagerPosition;
    SettingsFragment settingsFragment;
    CreditsFragment creditsFragment;
    SharedPreferences userNameSharedPreference;
    SharedPreferences.Editor userNameEditor;
    Animations animations = new Animations(this);

    private static MainActivity mainActivity;
    private int position;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    String notificationSelection;
    FloatingActionButton fab;
    FloatingActionButton fab2;
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    ImageView homeBtn;
    android.app.Fragment currentFragment;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        this.setMainActivity(this);
        Log.v("POWER", "THis is the beginning of the activity");
        firebaseFirestore = FirebaseFirestore.getInstance();
        userNameSharedPreference = getSharedPreferences("userName", Context.MODE_PRIVATE);
        userNameEditor = userNameSharedPreference.edit();
        fm = getFragmentManager();
        currentFragment = fm.findFragmentById(R.id.mainFragmentContainer);
        getFragment();      // get fragment if one exists and create if not (NOTE: all fragments are initialized here)
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        changeBackgroundBasedOnOrientation();       // This changes the background
        MobileAds.initialize(this, "ca-app-pub-9110389779271843~7759487012");   //Initialize mobile ads
        initializeBottomBanner(); // load the ads
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // we need to check if fragmentHolder is null or not,
                // to know weather to add or replace fragment in mainFragmentContainer
                currentFragment = fm.findFragmentById(R.id.mainFragmentContainer);
                if (currentFragment == null) {
                    Log.v("POWER", "adding fragment");
                    fm.beginTransaction().add(R.id.mainFragmentContainer, writeFragment).commit();

                }
                else{
                    Log.v("POWER", "replacing fragment");
                    fm.beginTransaction().replace(R.id.mainFragmentContainer, writeFragment).addToBackStack("write").commit();
                }
                changeToolbarColorBlack();

            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFragment = fm.findFragmentById(R.id.mainFragmentContainer);
                if (currentFragment == null) {
                    Log.v("POWER", "adding fragment");
                    fm.beginTransaction().add(R.id.mainFragmentContainer, readFragment).commit();
                }
                else{
                    Log.v("POWER", "replacing fragment");
                    fm.beginTransaction().replace(R.id.mainFragmentContainer, readFragment).addToBackStack("read").commit();
                }
                changeToolbarColorBlack();
            }
        });

        homeBtn = (ImageView) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFragment = fm.findFragmentById(R.id.mainFragmentContainer);
                if (currentFragment != null) {
                    fm.beginTransaction().remove(currentFragment).commit();
                    animations.showFab(fab);
                    animations.showFab(fab2);
                    setToolbarTitle("EmPower");
                    removeToolbarColor();
                }
            }
        });

        //If it's the first time using the app (No name entered yet),
        // Load the nameFragment fragment
        if (userNameSharedPreference.getString("name", null) == null) {
            if (currentFragment instanceof NameFragment) {}
            else {
                fm.beginTransaction().add(R.id.mainFragmentContainer, nameFragment).commit();
            }
        }
        else {
            exitFullscreen();
        }
        GetMostLikedMsgs getMostLikedMsgs = GetMostLikedMsgs.getInstance();
        mostLiked1 = (TextView) findViewById(R.id.mostLiked1);
        mostLiked2 = (TextView) findViewById(R.id.mostLiked2);
        mostLiked3 = (TextView) findViewById(R.id.mostLiked3);
        mostLikedContainer1 = (CardView) findViewById(R.id.mostLikedContainer1);
        mostLikedContainer2 = (CardView) findViewById(R.id.mostLikedContainer2);
        mostLikedContainer3 = (CardView) findViewById(R.id.mostLikedContainer3);

        getMostLikedMsgs.getMsgs(mostLiked1, mostLikedContainer1, 700, 0);
        getMostLikedMsgs.getMsgs(mostLiked2, mostLikedContainer2, 1000, 25);
        getMostLikedMsgs.getMsgs(mostLiked3, mostLikedContainer3, 1400, 50);

        // animate the fab button
        animations.moveRightFab(fab);
        animations.moveLeftFab(fab2);
        loadFromNotification();         // if notification was clicked

    }

    private void changeToolbarColorBlack() {
        appBarLayout.setBackgroundResource(R.drawable.black_heading);
        appBarLayout.setElevation(8);
    }
    private void removeToolbarColor() {
        appBarLayout.setBackgroundResource(R.drawable.shape3);
        appBarLayout.setElevation(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            currentFragment = fm.findFragmentById(R.id.mainFragmentContainer);
            if (currentFragment == null) {
                fm.beginTransaction().add(R.id.mainFragmentContainer, settingsFragment).commit();
                animations.hideFab(fab);
                animations.hideFab(fab2);
            }
            else {
                fm.beginTransaction().replace(R.id.mainFragmentContainer, settingsFragment).addToBackStack("settings").commit();
                animations.hideFab(fab);
                animations.hideFab(fab2);
            }
            changeToolbarColorBlack();
            return true;
        }
        if (id == R.id.action_credit) {
            currentFragment = fm.findFragmentById(R.id.mainFragmentContainer);
            if (currentFragment == null) {
                fm.beginTransaction().add(R.id.mainFragmentContainer, creditsFragment).commit();
                animations.hideFab(fab);
                animations.hideFab(fab2);
            }
            else {
                fm.beginTransaction().replace(R.id.mainFragmentContainer, creditsFragment).addToBackStack("credits").commit();
                animations.hideFab(fab);
                animations.hideFab(fab2);
            }
            changeToolbarColorBlack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v("POWER", "requesting permission");
        switch (requestCode) {
            case PERMISSION_ACCESS_LOCATION:
                final LocationHelper locationHelper = new LocationHelper(this);
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationHelper.requestLocationUpdates();
                    Log.v("POWER", "Permission granted");
                } else {
                    // TODO: If user deny's permission, we give them another chance to accept it
                    // but this time with better explanation why we need the permission
                }
        }
    }

    private void getFragment() {
        if (currentFragment!=null) {
            if (currentFragment instanceof NameFragment) {
                Log.v("POWER", "Gonna load NAMEfragment");
                nameFragment = (NameFragment) fm.findFragmentById(R.id.mainFragmentContainer);
                writeFragment = WriteFragment.newInstance("a", "b");
                readFragment = ReadFragment.newInstance("a", "b");
                settingsFragment = SettingsFragment.newInstance("a", "b");
                creditsFragment = CreditsFragment.newInstance("a", "b");
            }
            else if (currentFragment instanceof WriteFragment) {
                writeFragment = (WriteFragment) fm.findFragmentById(R.id.mainFragmentContainer);
                nameFragment = NameFragment.newInstance();
                readFragment = ReadFragment.newInstance("a", "b");
                settingsFragment = SettingsFragment.newInstance("a", "b");
                creditsFragment = CreditsFragment.newInstance("a", "b");
            }
            else if (currentFragment instanceof ReadFragment) {
                readFragment = (ReadFragment) fm.findFragmentById(R.id.mainFragmentContainer);
                nameFragment = NameFragment.newInstance();
                writeFragment = WriteFragment.newInstance("a", "b");
                settingsFragment = SettingsFragment.newInstance("a", "b");
                creditsFragment = CreditsFragment.newInstance("a", "b");
            }
            else if (currentFragment instanceof SettingsFragment) {
                settingsFragment = (SettingsFragment) fm.findFragmentById(R.id.mainFragmentContainer);
                nameFragment = NameFragment.newInstance();
                writeFragment = WriteFragment.newInstance("a", "b");
                readFragment = ReadFragment.newInstance("a", "b");
                creditsFragment = CreditsFragment.newInstance("a", "b");
            }
            else if (currentFragment instanceof CreditsFragment) {
                creditsFragment = (CreditsFragment) fm.findFragmentById(R.id.mainFragmentContainer);
                readFragment = ReadFragment.newInstance("a", "b");
                nameFragment = NameFragment.newInstance();
                writeFragment = WriteFragment.newInstance("a", "b");
                settingsFragment = SettingsFragment.newInstance("a", "b");
            }
        }
        else {
            nameFragment = NameFragment.newInstance();
            writeFragment = WriteFragment.newInstance("a", "b");
            readFragment = ReadFragment.newInstance("a", "b");
            settingsFragment = SettingsFragment.newInstance("a", "b");
            creditsFragment = CreditsFragment.newInstance("a", "b");
        }
    }

    public void loadFromNotification(){
        // if it was clicked from notification
        notificationSelection = getIntent().getStringExtra("notificationSelection");
        if (notificationSelection!=null) {
            if (notificationSelection.equals("readFragment")) {
                checkMsgPositionInDb();
            }
        }
    }
    private void checkMsgPositionInDb(){
        String docID = getIntent().getStringExtra("docID");
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        //get the documentSnapshot using it's ID first
        firestore.collection("quotes").document(docID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    firestore.collection("quotes").orderBy("timeStamp", Query.Direction.DESCENDING).endAt(documentSnapshot).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot documentSnapshots) {
                                    viewPagerPosition = documentSnapshots.size()-1;
                                    fm.beginTransaction().add(R.id.mainFragmentContainer, readFragment).commit();
                                    Log.v("POWER", "snapshot size is "+ String.valueOf(position));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.v("POWER", "Check msg position In DB failed");
                                }
                            });
                }
                else {
                    viewPagerPosition = 0;
                    fm.beginTransaction().add(R.id.mainFragmentContainer, readFragment).commit();
                    Log.v("POWER", "snapshot deleted");
                    Toast.makeText(getApplicationContext(), "Message deleted", Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("POWER", "no more" + e);

            }
        });
    }

    public void closeNameFragment() {
        fm = getFragmentManager();
        fm.beginTransaction().setCustomAnimations(R.animator.slide_in, R.animator.slide_out).detach(nameFragment).commit();
    }

    public void goFullscreen() {
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setVisibility(View.GONE);
        this.getSupportActionBar().hide();
        fab.setVisibility(View.GONE);
        fab2.setVisibility(View.GONE);
    }
    public void exitFullscreen() {
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setVisibility(View.VISIBLE);
        animations = new Animations(this);
        animations.exitFullscreen(fab, fab2, appBarLayout);
    }

    public View getCoordinatorLayout(){
        // Could/would be used to measure the dimension of the screen
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        return coordinatorLayout;
    }

    public int getViewPagerPosition() {
        return viewPagerPosition;
    }

    public void setToolbarTitle(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
    }
    public FirebaseFirestore getFirebaseFirestore(){
        //Helps fragments get the firestore object
        return this.firebaseFirestore;
    }

    public WriteFragment getWriteFragment() {
        return writeFragment;
    }
    public SettingsFragment getSettingsFragment() {return settingsFragment; }


    public FloatingActionButton getFab(){
        return fab;
    }
    public FloatingActionButton getFab2() {
        return fab2;
    }

    public void changeBackgroundBasedOnOrientation() {
        WindowManager window = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        int num = display.getRotation();
        if (num == 0) {
            getCoordinatorLayout().setBackgroundResource(R.drawable.home_port);
        }
        else if (num==1 || num==3) {
            getCoordinatorLayout().setBackgroundResource(R.drawable.home_land);
        }
        else  {
            getCoordinatorLayout().setBackgroundResource(R.drawable.home_port);
        }
    }
    private void initializeBottomBanner(){
        AdView bottomAd;
        bottomAd = (AdView) findViewById(R.id.bottomAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        bottomAd.loadAd(adRequest);

    }

}
