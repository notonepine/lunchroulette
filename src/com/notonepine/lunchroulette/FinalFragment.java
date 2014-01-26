package com.notonepine.lunchroulette;

import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
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
    SharedPreferences mPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_final, container, false);
        ((Button) mView.findViewById(R.id.final_cancel_button)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                animateSomeStuff(((LinearLayout) mView.findViewById(R.id.person_one_layout)), 600);
            }
        });

        mPrefs = getActivity().getSharedPreferences("com.notonepine.lunchroulette", Context.MODE_PRIVATE);
        String userId = mPrefs.getString(LunchRouletteFragmentActivity.USER_ID, "");
        Location location = Utils.getLocation(getActivity());
        NetworkUtils.beginSearch(userId, location.getLatitude(), location.getLongitude(), newUserFound(), locationFound());
        return mView;
    }

    private void animateSomeStuff(View view, int dist) {
        Animation anim = new TranslateAnimation(dist, 0, 0, 0);

        anim.setFillAfter(true);
        anim.setDuration(600);

        view.startAnimation(anim);
    }

    private JsonHttpResponseHandler newUserFound() {
    	return new JsonHttpResponseHandler() {
    		@Override
    		public void onSuccess(JSONObject userData) {
    			String name = userData.optString("name");
    			String avatarUrl = Utils.getUserAvatarUrl(userData.optString("fb_id"));
    			// do something with this
    		}
    	};
    }

    private JsonHttpResponseHandler locationFound() {
    	return new JsonHttpResponseHandler() {
    		@Override
    		public void onSuccess(JSONObject restaurant) {
    			double latitude = restaurant.optDouble("lat");
    			double longitude = restaurant.optDouble("long");
    			String name = restaurant.optString("name");
    			// do something with this
    		}
    	};
    }
}
