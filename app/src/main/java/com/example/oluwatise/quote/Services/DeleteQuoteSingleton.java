package com.example.oluwatise.quote.Services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.Fragments.WriteFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Created by Oluwatise on 1/17/2018.
 */

public class DeleteQuoteSingleton {
    private static final DeleteQuoteSingleton ourInstance = new DeleteQuoteSingleton();

    public static DeleteQuoteSingleton getInstance() {
        return ourInstance;
    }
    String name;
    long timeStamp;
    String documentId;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private DeleteQuoteSingleton() {
    }
    // This is the method acts as a constructor, since it's
    // hard to initialize properties in singleton's constructor
    public void deleteQuoteConstructor (String name, long timeStamp) {
        this.name = name;
        this.timeStamp = timeStamp;
    }

    public void deleteQuote() {
        // get the fields of the document and use those fields to get the ID.
        firestore.collection("quotes").whereEqualTo("name", name).whereEqualTo("timeStamp", timeStamp).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for (DocumentSnapshot documentSnapshot: documentSnapshots) {
                    // get the ID (it's easier to use a foreach loop, tho documentSnapshots has just one content)
                    documentId = documentSnapshot.getId();
                    // get and delete the document
                    DocumentReference document = getDocument(documentId);
                    document.delete();
                    // now reload recyclerView
                    MainActivity.getMainActivity().getWriteFragment().loadRecyclerView();

                }
                Log.v("POWER", "ID is: "+ documentId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("POWER", "Error deleting:  "+ e.toString());
            }
        });
    }

    public DocumentReference getDocument(String documentId) {
        DocumentReference documentReference = firestore.collection("quotes").document(documentId);
        return documentReference;
    }
}
