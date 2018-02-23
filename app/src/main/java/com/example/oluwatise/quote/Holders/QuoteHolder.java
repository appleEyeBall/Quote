package com.example.oluwatise.quote.Holders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oluwatise.quote.HelperClasses.QuoteObject;
import com.example.oluwatise.quote.R;

import java.lang.reflect.Field;

/**
 * Created by Oluwatise on 12/15/2017.
 */

public class QuoteHolder extends RecyclerView.ViewHolder{
//    TextView cardTitle;
    TextView cardMessage;
    TextView cardLikes;
    TextView cardUserName;
    TextView cardcity;
    ImageView cardLikeImage;

    public QuoteHolder(View itemView) {
        super(itemView);
//        cardTitle = (TextView) itemView.findViewById(R.id.cardTitle);
        cardMessage = (TextView) itemView.findViewById(R.id.cardMessage);
        cardLikes = (TextView) itemView.findViewById(R.id.cardLikeText);
        cardUserName = (TextView) itemView.findViewById(R.id.cardUsername);
        cardcity = (TextView) itemView.findViewById(R.id.cardCity);
        cardLikeImage = (ImageView) itemView.findViewById(R.id.cardLikeImage);
    }

    public void updateUi(QuoteObject quoteObject) {
        String cardUserNameText = "You,";
        String cardLikesText = String.valueOf(quoteObject.getNumberOfLikes());
        String cardCityText = quoteObject.getLocation();
        String cardMsgText = quoteObject.getMsg();
        if (cardMsgText.length()>40) {
            cardMsgText = cardMsgText.substring(0, 40) + " ...";
        }

        cardMessage.setText(cardMsgText);
        cardLikes.setText(cardLikesText);
        cardUserName.setText(cardUserNameText);
        cardcity.setText(cardCityText);
    }
}
