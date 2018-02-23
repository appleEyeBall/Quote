package com.example.oluwatise.quote.Services;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.Adapters.QuoteAdapter;
import com.example.oluwatise.quote.Adapters.ReadRecyclerAdapter;
import com.example.oluwatise.quote.HelperClasses.QuoteObject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Oluwatise on 1/18/2018.
 */

public class GetAllMessageFeed {
    private static final GetAllMessageFeed ourInstance = new GetAllMessageFeed();

    public static GetAllMessageFeed getInstance() {
        return ourInstance;
    }

    ReadRecyclerAdapter readRecyclerAdapter;
    ViewPager viewPager;
    ArrayList<QuoteObject> quotesFromDb = new ArrayList<>();       // the array to store all the objects
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public GetAllMessageFeed() {
    }

    public void getQuotes(final Context context, final ViewPager viewPager, final int viewPagerPosition) {

        quotesFromDb.clear(); // work with a fresh empty array so
        // everytime you open the fragment, it does not append to previous data.
        this.viewPager = viewPager;
        // gets all the quotes on the database (in the read fragment)
        firestore.collection("quotes").orderBy("timeStamp", Query.Direction.DESCENDING).limit(20).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for (DocumentSnapshot documentSnapshot: documentSnapshots) {
                    QuoteObject quoteObject = documentSnapshot.toObject(QuoteObject.class);
                    quotesFromDb.add(quoteObject);
                }
                // setup the adapter (passing in our new array) and the recyclerview
                // setup the recycler view that displays all quotes submitted by the user
                readRecyclerAdapter = new ReadRecyclerAdapter(quotesFromDb);
                viewPager.setOffscreenPageLimit(3);
                viewPager.setPageMargin(80);
                viewPager.setElevation(5f);


                try {
                    // this loads a specific post in recyclerView
                    Field field = ViewPager.class.getDeclaredField("mRestoredCurItem");
                    field.setAccessible(true);
                    field.set(viewPager, viewPagerPosition);
                } catch (NoSuchFieldException e) {
                    Log.v("POWER", "NO SUCH FIELD");
                    e.printStackTrace();
                }
                catch (IllegalAccessException ie) {
                    Log.v("POWER", "illegal");
                    ie.printStackTrace();
                }
                viewPager.setAdapter(readRecyclerAdapter);

                Log.v("POWER", "read quotes is: "+ quotesFromDb.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("POWER", "It failed      "+ e.toString());
            }
        });

    }
}
