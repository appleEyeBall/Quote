package com.example.oluwatise.quote.Services;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.HelperClasses.Animations;
import com.example.oluwatise.quote.HelperClasses.QuoteObject;
import com.example.oluwatise.quote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Created by Oluwatise on 2/23/2018.
 */

public class GetMostLikedMsgs {
    private static final GetMostLikedMsgs ourInstance = new GetMostLikedMsgs();

    public static GetMostLikedMsgs getInstance() {
        return ourInstance;
    }

    public GetMostLikedMsgs() {
    }
    ArrayList<QuoteObject> quoteObjects = new ArrayList<>();

    public void getMsgs(final TextView v, final CardView parentView, final int delay, final int topMargin){

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("quotes").orderBy("numberOfLikes").limit(3).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for ( DocumentSnapshot documentSnapshot: documentSnapshots) {
                    QuoteObject quoteObject = documentSnapshot.toObject(QuoteObject.class);
                    quoteObjects.add(quoteObject);
                }
                Animations animations = new Animations(MainActivity.getMainActivity());
                Log.v("POWER", "id is "+String.valueOf(v.getId()));
                if (v.getId() == R.id.mostLiked1) {
                    v.setText(quoteObjects.get(0).getMsg());
                }
                else if (v.getId() == R.id.mostLiked2) {
                    v.setText(quoteObjects.get(1).getMsg());
                }
                else if (v.getId() == R.id.mostLiked3) {
                    v.setText(quoteObjects.get(2).getMsg());
                }
                animations.moveMostLiked(parentView, delay, topMargin);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
