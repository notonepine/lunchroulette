package com.notonepine.lunchroulette;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeFragment extends Fragment {
    View mView;
    ImageView avatarImage;
    TextView name;
    SharedPreferences mPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        avatarImage = ((ImageView) mView.findViewById(R.id.home_avatar));
        name = ((TextView) mView.findViewById(R.id.home_name));
        mPrefs = getActivity().getSharedPreferences("com.notonepine.lunchroulette", Context.MODE_PRIVATE);
        ((Button) mView.findViewById(R.id.go_button)).setOnClickListener(searchClick());
        return mView;
    }

    @Override
    public void onResume() {
        String userId = mPrefs.getString(LunchRouletteFragmentActivity.FACEBOOK_USER_ID, "");
        String userName = mPrefs.getString(LunchRouletteFragmentActivity.FACEBOOK_FULL_NAME, "");
        if (userId != "" && userName != "") {
            name.setText("Welcome, " + userName);
            String avatarUrl = Utils.getUserAvatarUrl(userId);
            ImageLoader.getInstance().displayImage(avatarUrl, avatarImage);
        }
        super.onResume();
    }

    private OnClickListener searchClick() {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        };
    }
}
