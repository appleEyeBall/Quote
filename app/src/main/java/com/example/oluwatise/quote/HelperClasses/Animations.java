package com.example.oluwatise.quote.HelperClasses;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.IntentFilter;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationSet;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.R;

/**
 * Created by Oluwatise on 11/20/2017.
 */

public class Animations {
    Context context;
    AnimatorSet rotateImageAnimation;

    public Animations(Context context) {
        this.context = context;
    }

    public void exitFullscreen(FloatingActionButton fab, FloatingActionButton fab2, AppBarLayout appBarLayout) {
        MainActivity.getMainActivity().getSupportActionBar().show();
        fab.setVisibility(View.VISIBLE);
        fab2.setVisibility(View.VISIBLE);
        AnimatorSet fabAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.enlarge_fab);
        AnimatorSet fab2Animator = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.enlarge_fab);
        AnimatorSet appBarAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.show_app_bar);
        fabAnimator.setTarget(fab);
        fab2Animator.setTarget(fab2);
        appBarAnimator.setTarget(appBarLayout);
        fabAnimator.start(); fab2Animator.start(); appBarAnimator.start();
    }

    public void sendBtnAction(View view){
        AnimatorSet sendAction = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.send_action);
        sendAction.setTarget(view);
        sendAction.start();
    }
    public void sendBtnAnimation(View view){
        // The method that animates the send button, giving a 'buzzing' effect every few seconds
        AnimatorSet sendBtnSet = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.send);
        sendBtnSet.setTarget(view);
        sendBtnSet.start();
        sendBtnSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.start();      // restart the animation, once it ends
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

    }

    public void moveRightFab(View view) {
        final AnimatorSet fabAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.fab_move);
        fabAnimation.setTarget(view);
        fabAnimation.start();
        fabAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fabAnimation.start();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    public void moveLeftFab(View view) {
        final AnimatorSet fab2Animation = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.fab2_move);
        fab2Animation.setTarget(view);
        fab2Animation.start();
        fab2Animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fab2Animation.start();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void circularRevealFragment(View view, View container, int x, int y){
        // If the view is closed
        int startRadius = 0;
        int endRadius = (int) Math.hypot(container.getWidth(), container.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, endRadius);
        anim.setDuration(context.getResources().getInteger(android.R.integer.config_mediumAnimTime));
        anim.start();
    }

    public void hideFab(final View view) {
        AnimatorSet hideRightFabAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.zoom_out_hide);
        hideRightFabAnimator.setTarget(view);
        hideRightFabAnimator.start();
        hideRightFabAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                view.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    public void showFab(final View fab) {
        AnimatorSet showRightFabAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.zoom_in_show);
        showRightFabAnimator.setTarget(fab);
        showRightFabAnimator.start();
        showRightFabAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void likeQuote(View view) {
        AnimatorSet likeQuoteAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.like_select);
        likeQuoteAnimator.setTarget(view);
        likeQuoteAnimator.start();
    }
    public void flag(View view) {
        AnimatorSet likeQuoteAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.move_flag);
        likeQuoteAnimator.setTarget(view);
        likeQuoteAnimator.start();
    }
    public void rotateRefresh() {
        View view = MainActivity.getMainActivity().getSettingsFragment().getRefreshLocationImg();
        if (rotateImageAnimation == null) {
            rotateImageAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.rotate_refresh_button);
            rotateImageAnimation.setTarget(view);
        }
        rotateImageAnimation.start();
    }
    public void stopRotateRefresh(){
        if (rotateImageAnimation!=null) {
            rotateImageAnimation.end();
        }
    }
    public void animateRvChildren(View view, int delay){
        AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.recycler);
        animatorSet.setTarget(view);
        animatorSet.setStartDelay(delay);
        animatorSet.start();
    }
    public void showRvChild(View view) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(view,"alpha", 0, 1);
        oa.setDuration(1);
        oa.start();
    }
}
