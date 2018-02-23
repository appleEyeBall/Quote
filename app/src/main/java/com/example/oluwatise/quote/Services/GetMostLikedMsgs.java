package com.example.oluwatise.quote.Services;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.oluwatise.quote.HelperClasses.QuoteObject;
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

    public void getMsgs(View v){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("quotes").orderBy("numberOfLikes").limit(3).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for ( DocumentSnapshot documentSnapshot: documentSnapshots) {
                    QuoteObject quoteObject = documentSnapshot.toObject(QuoteObject.class);
                    quoteObjects.add(quoteObject);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
