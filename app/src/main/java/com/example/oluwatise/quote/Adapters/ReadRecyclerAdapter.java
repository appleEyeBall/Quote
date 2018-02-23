package com.example.oluwatise.quote.Adapters;

import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.oluwatise.quote.HelperClasses.QuoteObject;
import com.example.oluwatise.quote.Holders.ReadRecyclerHolder;
import com.example.oluwatise.quote.R;

import java.util.ArrayList;

/**
 * Created by Oluwatise on 1/18/2018.
 */

public class ReadRecyclerAdapter extends PagerAdapter {
    public ArrayList<QuoteObject> allMessages = new ArrayList<>();
    ReadRecyclerHolder readRecyclerHolder;

    public ReadRecyclerAdapter(ArrayList<QuoteObject> allMessages) {
        this.allMessages = allMessages;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View swipeCard = LayoutInflater.from(container.getContext()).inflate(R.layout.swipe_card,container,false);
        readRecyclerHolder = new ReadRecyclerHolder(swipeCard, allMessages.get(position));
        readRecyclerHolder.updateUi();
        container.addView(swipeCard);
        return swipeCard;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return allMessages.size();
    }

}

