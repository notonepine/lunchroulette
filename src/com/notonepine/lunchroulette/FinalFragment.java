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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class FinalFragment extends Fragment {
    View mView;
    private GoogleMap map;
    SharedPreferences mPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_final, container, false);
        ((Button) mView.findViewById(R.id.final_cancel_button)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                translatePersonIn(((LinearLayout) mView.findViewById(R.id.person_one_layout)), 600);
            }
        });

        mView.post(new Runnable() {

            @Override
            public void run() {
                map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                map.getUiSettings().setZoomControlsEnabled(false);
                map.setMyLocationEnabled(true);
                Location location = Utils.getLocation(FinalFragment.this.getActivity());
                LatLng ltl = new LatLng(location.getLongitude(), location.getLatitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(ltl, 16));
            }
        });


        mPrefs = getActivity().getSharedPreferences("com.notonepine.lunchroulette", Context.MODE_PRIVATE);
        String userId = mPrefs.getString(LunchRouletteFragmentActivity.USER_ID, "");
        Location location = Utils.getLocation(getActivity());
        NetworkUtils.beginSearch(userId, location.getLatitude(), location.getLongitude(), newUserFound(), locationFound());
        return mView;
    }

    private void translatePersonIn(final View view, int dist) {
        Animation anim = new TranslateAnimation(dist, 0, 0, 0);

        anim.setFillAfter(true);
        anim.setDuration(600);

        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (view.getId() == R.id.person_one_layout) {
                    translatePersonIn(((LinearLayout) mView.findViewById(R.id.person_two_layout)), 400);
                } else if (view.getId() == R.id.person_two_layout) {
                    translatePersonIn(((LinearLayout) mView.findViewById(R.id.person_three_layout)), 200);
                } else if (view.getId() == R.id.person_three_layout) {
                    fadeOut(((LinearLayout) mView.findViewById(R.id.loading_layout)));
                    fadeIn(((LinearLayout) mView.findViewById(R.id.final_map_container)));
                }
            }
        });

        view.startAnimation(anim);
    }

    private void fadeOut(final View view) {
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);
        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void fadeIn(final View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);
        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Switches from the loading spinner to the map view.
     */
    private void switchStates() {

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
