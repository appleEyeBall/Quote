package com.example.oluwatise.quote.Fragments;


import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.HelperClasses.Animations;
import com.example.oluwatise.quote.R;
import com.example.oluwatise.quote.Services.GetAllMessageFeed;
import com.example.oluwatise.quote.Services.GetMyQuoteHistorySingleton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadFragment extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ReadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReadFragment newInstance(String param1, String param2) {
        ReadFragment fragment = new ReadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    ViewPager viewPager;
    Animations animations;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
        animations = new Animations(getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_read, container, false);

        v.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) MainActivity.getMainActivity().getCoordinatorLayout();
                animations.circularRevealFragment(v, coordinatorLayout, left, bottom);
            }
        });
        // set up viewPager
        viewPager = (ViewPager) v.findViewById(R.id.viewPager);
        GetAllMessageFeed getAllMessageFeed = GetAllMessageFeed.getInstance();
        int viewPagerPosition = MainActivity.getMainActivity().getViewPagerPosition();
        Log.v("POWER", " in read....vP is "+ String.valueOf(viewPagerPosition));
        getAllMessageFeed.getQuotes(getActivity(), viewPager, viewPagerPosition);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.getMainActivity().setToolbarTitle("EmPower");
    }


    @Override
    public void onResume() {
        super.onResume();
        MainActivity.getMainActivity().setToolbarTitle("Read");
        animations = new Animations(getActivity());
        FloatingActionButton fab = MainActivity.getMainActivity().getFab();
        FloatingActionButton fab2 = MainActivity.getMainActivity().getFab2();
        animations.hideFab(fab2);
        animations.showFab(fab);
    }
}
