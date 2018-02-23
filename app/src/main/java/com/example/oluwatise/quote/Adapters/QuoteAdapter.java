package com.example.oluwatise.quote.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.HelperClasses.QuoteObject;
import com.example.oluwatise.quote.Holders.QuoteHolder;
import com.example.oluwatise.quote.R;
import com.example.oluwatise.quote.Services.DeleteQuoteSingleton;

import java.util.ArrayList;

/**
 * Created by Oluwatise on 12/15/2017.
 */

public class QuoteAdapter extends RecyclerView.Adapter<QuoteHolder>{
    Context context = MainActivity.getMainActivity();
    ArrayList<QuoteObject> quotesFromDb = new ArrayList<>();

    public QuoteAdapter(ArrayList<QuoteObject> quotesFromDb) {
        this.quotesFromDb = quotesFromDb;
    }


    @Override
    public QuoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View quoteCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        QuoteHolder quoteHolder = new QuoteHolder(quoteCard);
        return quoteHolder;
    }

    @Override
    public void onBindViewHolder(final QuoteHolder holder, int position) {
        QuoteObject quoteFromDb = quotesFromDb.get(position);
        holder.updateUi(quoteFromDb);
        // make it clickable
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(holder.getAdapterPosition());
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return quotesFromDb.size();
    }



    public void showDeleteDialog(final int position) {
        AlertDialog.Builder builder;
        // set builder based on the kinda os
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.MaterialDialog);
        }
        else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Delete Message").setMessage(R.string.delete_prompt)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void deleteMessage(int position) {
        QuoteObject quoteObject = quotesFromDb.get(position);
        String name = quoteObject.getName();
        long timeStamp = quoteObject.getTimeStamp();
        DeleteQuoteSingleton deleteQuoteSingleton = DeleteQuoteSingleton.getInstance();
        deleteQuoteSingleton.deleteQuoteConstructor(name, timeStamp);
        deleteQuoteSingleton.deleteQuote();


    }

}
