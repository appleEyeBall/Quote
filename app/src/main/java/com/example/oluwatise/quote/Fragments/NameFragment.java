package com.example.oluwatise.quote.Fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.HelperClasses.Animations;
import com.example.oluwatise.quote.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must
 * Use the {@link NameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NameFragment extends android.app.Fragment {

    public NameFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment NameFragment.
     */

    public static NameFragment newInstance() {
        NameFragment fragment = new NameFragment();
        return fragment;
    }

    // All initializations
    EditText heyEdit;
    Button sendBtn;
    TextInputLayout heyText;
    TextView heyMsg;
    SharedPreferences userNameSharedPreference;
    SharedPreferences.Editor userNameEditor;
    Animations animations;
    CoordinatorLayout coordinatorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_name, container, false);

        heyMsg = (TextView) v.findViewById(R.id.heyMsg);
        heyText = (TextInputLayout) v.findViewById(R.id.heyText);
        heyEdit = (EditText) v.findViewById(R.id.heyEdit);
        sendBtn = (Button) v.findViewById(R.id.sendBtn);
        coordinatorLayout = (CoordinatorLayout) MainActivity.getMainActivity().getCoordinatorLayout();
        animations = new Animations(getActivity());
        animations.sendBtnAnimation(sendBtn); //call the method that animates the send button
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNameAndHideKeyboard();

            }
        });
        heyEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendBtn.performClick();
                    return true;
                }
                return false;
            }
        });


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Go fullscreen
        MainActivity.getMainActivity().goFullscreen();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.getMainActivity().exitFullscreen();
    }
                    /* ******************************
                    *********************************
                    *****   ALL OTHER METHODS   *****
                    *********************************/


    public void saveNameAndHideKeyboard(){
        // Hide keyboard first
        View view = getActivity().getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        // Save the name in Shared preferences
        String name = heyEdit.getText().toString().trim();
        userNameSharedPreference = getActivity().getSharedPreferences("userName", Context.MODE_PRIVATE);
        userNameEditor = userNameSharedPreference.edit();
        userNameEditor.putString("name", name);
        userNameEditor.apply();

        animations.sendBtnAction(sendBtn);

        MainActivity.getMainActivity().closeNameFragment();

        String nameTest = userNameSharedPreference.getString("name", null);
        Log.v("POWERA", "save name"+ nameTest);
    }
}
