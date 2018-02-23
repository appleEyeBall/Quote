package com.example.oluwatise.quote.Holders;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.Adapters.ReadRecyclerAdapter;
import com.example.oluwatise.quote.HelperClasses.Animations;
import com.example.oluwatise.quote.HelperClasses.QuoteObject;
import com.example.oluwatise.quote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Created by Oluwatise on 1/18/2018.
 */

public class ReadRecyclerHolder {
    String userName;
    View view;
    QuoteObject quoteObject;
    TextView mainMsg;
    ImageView swipeCardLikeImage;
    ImageView swipeCardFlagImage;
    TextView numberOfLikesView;
    TextView locationView;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Animations animations = new Animations(MainActivity.getMainActivity());

    public ReadRecyclerHolder(View view, QuoteObject quoteObject) {
        SharedPreferences sharedPreferences = MainActivity.getMainActivity().getSharedPreferences("userName", Context.MODE_PRIVATE);
        this.userName = sharedPreferences.getString("name", null);

        this.view = view;
        this.quoteObject = quoteObject;
        mainMsg = (TextView) view.findViewById(R.id.mainMsg);
        numberOfLikesView = (TextView) view.findViewById(R.id.numberOfLikes);
        locationView = (TextView) view.findViewById(R.id.locationText);
        swipeCardLikeImage = (ImageView) view.findViewById(R.id.swipeCardLikeImage);
        swipeCardFlagImage = (ImageView) view.findViewById(R.id.flagImg);
        checkLikes();
        checkFlags();
        likeImage();
        flagImage();
    }

    public void updateUi() {
        String msg = quoteObject.getMsg();
        String numberOfLikes = String.valueOf(quoteObject.getNumberOfLikes());
        String location = quoteObject.getLocation();

        mainMsg.setText(msg);
        numberOfLikesView.setText(numberOfLikes);
        locationView.setText(location);
    }
    private void likeImage(){
        swipeCardLikeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animations.likeQuote(swipeCardLikeImage);
                int newNumberOfLikes;
                if (!quoteObject.isUserLiked()) {
                    newNumberOfLikes = quoteObject.getNumberOfLikes() + 1;
                    quoteObject.addLikedUser(userName);
                }
                else {
                    newNumberOfLikes = quoteObject.getNumberOfLikes() - 1;
                    if (newNumberOfLikes<0) {newNumberOfLikes = 0;}
                    quoteObject.removeLikedUser(userName);
                }
                quoteObject.setNumberOfLikes(newNumberOfLikes); //set the number of likes
                numberOfLikesView.setText(String.valueOf(newNumberOfLikes)); // display the number of likes
                updateLikesAndFlagsOnDb();
                checkLikes();

            }
        });
    }
    private void flagImage(){
        swipeCardFlagImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animations.flag(swipeCardFlagImage);
                if (!quoteObject.isUserFlagged()) {
                    quoteObject.addFlaggedUser(userName);
                    if (quoteObject.getFlaggedUsers().size()>10) {
                        deleteMsgFromDb();
                    }
                    else {
                        updateLikesAndFlagsOnDb();
                        checkFlags();
                    }
                }

            }
        });
    }


    private void updateLikesAndFlagsOnDb(){
        firestore.collection("quotes").whereEqualTo("name", quoteObject.getName()).whereEqualTo("timeStamp", quoteObject.getTimeStamp()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for (DocumentSnapshot documentSnapshot: documentSnapshots) {
                    // get the ID (it's easier to use a foreach loop, tho documentSnapshots has just one content)
                    String documentId = documentSnapshot.getId();
                    updateDatabase(quoteObject, documentId);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("POWER", "Error deleting:  "+ e.toString());
            }
        });
    }
    private void deleteMsgFromDb(){
        firestore.collection("quotes").whereEqualTo("name", quoteObject.getName()).whereEqualTo("timeStamp", quoteObject.getTimeStamp())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for (DocumentSnapshot documentSnapshot: documentSnapshots) {
                    String docId = documentSnapshot.getId();
                    DocumentReference documentReference = getDocument(docId);
                    Log.v("POWER", "docId is "+ docId);
                    documentReference.delete();
                    Log.v("POWER", "msg deleted ");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("POWER", "could not get document");

            }
        });

    }
    private DocumentReference getDocument(String documentId) {
        DocumentReference documentReference = firestore.collection("quotes").document(documentId);
        return documentReference;
    }

    private void checkLikes(){
        if (quoteObject.getLikedUsers().contains(userName)) {
            quoteObject.setUserLiked(true);
            swipeCardLikeImage.setImageResource(R.drawable.heart_on);
        }
        else {
            quoteObject.setUserLiked(false);
            swipeCardLikeImage.setImageResource(R.drawable.heart_off);
        }

    }
    private void checkFlags() {
        ArrayList<String> flaggedUsers = quoteObject.getFlaggedUsers();
        if (quoteObject.getFlaggedUsers().contains(userName)) {
            quoteObject.setUserFlagged(true);
        }
        else {
            quoteObject.setUserFlagged(false);
        }

    }

    private void updateDatabase(QuoteObject quoteObject, String documentId) {
        // get the exact document from it's ID and update the database with the new quoteObject
        firestore.collection("quotes").document(documentId).set(quoteObject).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.getMainActivity(), "can't perform operation :( ", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
