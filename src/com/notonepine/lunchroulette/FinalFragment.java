package com.notonepine.lunchroulette;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

public class FinalFragment extends Fragment {
    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_final, container, false);
        ((Button) mView.findViewById(R.id.final_cancel_button)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                animateSomeStuff(((LinearLayout) mView.findViewById(R.id.person_one_layout)), 600);
            }
        });
        return mView;
    }

    private void animateSomeStuff(View view, int dist) {
        Animation anim = new TranslateAnimation(dist, 0, 0, 0);

        anim.setFillAfter(true);
        anim.setDuration(600);

        view.startAnimation(anim);
    }

}
