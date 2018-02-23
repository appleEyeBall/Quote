package com.example.oluwatise.quote.Services;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.Adapters.QuoteAdapter;
import com.example.oluwatise.quote.Adapters.ReadRecyclerAdapter;
import com.example.oluwatise.quote.HelperClasses.Animations;
import com.example.oluwatise.quote.HelperClasses.QuoteObject;
import com.example.oluwatise.quote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Created by Oluwatise on 12/16/2017.
 */

public class GetMyQuoteHistorySingleton{
    static final GetMyQuoteHistorySingleton ourInstance = new GetMyQuoteHistorySingleton();

    public static GetMyQuoteHistorySingleton getInstance() {
        return ourInstance;
    }
    QuoteAdapter quoteAdapter;
    ReadRecyclerAdapter readRecyclerAdapter;
    RecyclerView recyclerView;
    ArrayList<QuoteObject> quotesFromDb = new ArrayList<>();       // the array to store all the objects
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Context context;
    private GetMyQuoteHistorySingleton() {

    }

    public void getQuotes(final Context context, String name, RecyclerView rv, final TextView noQuoteView){
        quotesFromDb.clear(); // work with a fresh empty array so
                            // everytime you open the fragment, it does not append to previous data.
        this.recyclerView = rv;
        this.context = context;
        // gets only the quotes submitted by the specific user
        // note: the 'whereEqualTo' clause is added

        firestore.collection("quotes").orderBy("timeStamp", Query.Direction.DESCENDING).whereEqualTo("name", name).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                // Decide if to show the noQuote textView or not
                if (documentSnapshots.isEmpty()) {
                    noQuoteView.setVisibility(View.VISIBLE);
                }
                else {
                    noQuoteView.setVisibility(View.GONE);
                }
                // now get the quotes
                for (DocumentSnapshot documentSnapshot: documentSnapshots) {
                    QuoteObject quoteObject = documentSnapshot.toObject(QuoteObject.class);
                    quotesFromDb.add(quoteObject);
                    Log.v("POWER", "size is: " + String.valueOf(quotesFromDb.size()));
                }
                // setup the adapter (passing in our new array) and the recyclerview
                // setup the recycler view that displays all quotes submitted by the user
                quoteAdapter = new QuoteAdapter(quotesFromDb);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(context);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
                recyclerView.setAdapter(quoteAdapter);
                animateRv();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("POWER", "It failed      "+ e.toString());
            }
        });

    }
    public void animateRv() {
        final Animations animations = new Animations(context);
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int childrenToAnimate;
                if (quotesFromDb.size() >= 10) {
                    childrenToAnimate = 10;
                } else {
                    childrenToAnimate = quotesFromDb.size();
                }

                for (int x = 0; x<=childrenToAnimate; x++) {
                    View child = recyclerView.getChildAt(x);
                    animations.showRvChild(child);
                    animations.animateRvChildren(child, (x+1)*80);
                }
            }

        });

    }
}
