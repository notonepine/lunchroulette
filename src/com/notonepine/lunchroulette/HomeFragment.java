package com.notonepine.lunchroulette;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.notonepine.lunchroulette.views.RoundedImageView;
import com.notonepine.lunchroulette.views.TextViewWithFont;

public class HomeFragment extends Fragment {
    View mView;
    RoundedImageView avatarImage;
    TextViewWithFont name;
    SharedPreferences mPrefs;

    String userId;
    String userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        avatarImage = ((RoundedImageView) mView.findViewById(R.id.home_avatar));
        name = ((TextViewWithFont) mView.findViewById(R.id.home_welcome_text));
        mPrefs = getActivity().getSharedPreferences("com.notonepine.lunchroulette", Context.MODE_PRIVATE);
        ((Button) mView.findViewById(R.id.go_button)).setOnClickListener(searchClick());
        return mView;
    }

    @Override
    public void onResume() {
        if (userId == null && userName == null) {
            userId = mPrefs.getString(LunchRouletteFragmentActivity.FACEBOOK_USER_ID, "");
            userName = mPrefs.getString(LunchRouletteFragmentActivity.FACEBOOK_FULL_NAME, "");
        }

        if (userId != "" && userName != "") {
            name.setText("Welcome, " + userName);
            String avatarUrl = Utils.getUserAvatarUrl(userId);

            ((LunchRouletteFragmentActivity) getActivity()).loader.displayImage(avatarUrl, avatarImage);
        }
        super.onResume();
    }

    private OnClickListener searchClick() {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToFinalScreen();
            }
        };
    }

    private void moveToFinalScreen() {
        FragmentTransaction ft = getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in,
                                        android.R.anim.fade_out).addToBackStack(null);
        ft.replace(R.id.container, new FinalFragment());
        ft.commit();
    }
}
