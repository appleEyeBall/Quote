package com.example.oluwatise.quote.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.Adapters.QuoteAdapter;
import com.example.oluwatise.quote.HelperClasses.Animations;
import com.example.oluwatise.quote.HelperClasses.LocationHelper;
import com.example.oluwatise.quote.HelperClasses.QuoteObject;
import com.example.oluwatise.quote.R;
import com.example.oluwatise.quote.Services.GetMyQuoteHistorySingleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WriteFragment extends android.app.Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public WriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WriteFragment newInstance(String param1, String param2) {
        WriteFragment fragment = new WriteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    Button submitBtn;
    EditText quoteEdit;
    String username;
    String cityName;
    TextView noQuote;
    public String quoteText;
    RecyclerView recyclerView;
    QuoteAdapter quoteAdapter;
    LocationHelper locationHelper;
    SharedPreferences userNameSharedPreferences;
    SharedPreferences cityNameSharedPreference;
    final int PERMISSION_ACCESS_LOCATION = 100;
    GetMyQuoteHistorySingleton getMyQuoteHistorySingleton;
    FirebaseFirestore firestore = MainActivity.getMainActivity().getFirebaseFirestore();
    Animations animations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_write, container, false);
        animations = new Animations(getActivity());

        v.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) MainActivity.getMainActivity().getCoordinatorLayout();
                animations.circularRevealFragment(v, coordinatorLayout, right, bottom);
            }
        });

        getMyQuoteHistorySingleton = GetMyQuoteHistorySingleton.getInstance();
        userNameSharedPreferences = getActivity().getSharedPreferences("userName", Context.MODE_PRIVATE);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        quoteEdit = (EditText) v.findViewById(R.id.quoteText);
        submitBtn = (Button) v.findViewById(R.id.submitQuote);
        noQuote = (TextView) v.findViewById(R.id.noQuote);
        username = userNameSharedPreferences.getString("name", null);
        loadRecyclerView(); //gets the quoteHistory and loads the recyclerView in a different thread

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quoteText = quoteEdit.getText().toString();
                locationHelper = new LocationHelper(getActivity());
                quoteEdit.setText("");
            }
        });
        return v;
    }

    public void storeInFireStore(){
        // This will be called by the LocationHelper class
        // The method saves the data to firestore
        cityNameSharedPreference = getActivity().getSharedPreferences("cityName", Context.MODE_PRIVATE);
        cityName = cityNameSharedPreference.getString("city", null);
        Log.v("POWER", " values are: " + quoteText+ " " + username +  " "  + cityName);
        QuoteObject quoteObject = new QuoteObject();
        quoteObject.setVariables(username, cityName, quoteText, getCurrentTimeStamp());
        // store the quoteObject in firestore
        firestore.collection("quotes").add(quoteObject).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                //get quotes... again, since a new one has been added
                loadRecyclerView();
                Toast.makeText(getActivity(), "Word is spread", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error spreading word", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void loadRecyclerView(){
        // getMyQuoteHistorySingleton sets up the recycler view in a different thread....
        getMyQuoteHistorySingleton.getQuotes(getActivity(), username, recyclerView, noQuote);

    }

    public Long getCurrentTimeStamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Log.v("POWER", String.valueOf(timestamp.getTime()));
        return timestamp.getTime();
    }



    @Override
    public void onResume() {
        super.onResume();
        MainActivity.getMainActivity().setToolbarTitle(" Write ");
        animations = new Animations(getActivity());
        FloatingActionButton fab = MainActivity.getMainActivity().getFab();
        FloatingActionButton fab2 = MainActivity.getMainActivity().getFab2();
        animations.hideFab(fab);
        animations.showFab(fab2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.getMainActivity().setToolbarTitle("EmPower");
    }

}
