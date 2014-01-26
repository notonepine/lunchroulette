package com.notonepine.lunchroulette;

import java.util.Arrays;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils.Permissions;

public class LoginFragment extends Fragment {
    View mView;
    private SharedPreferences mPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login, container, false);
        ((Button) mView.findViewById(R.id.login_button)).setOnClickListener(loginClick());
        mPrefs = getActivity().getSharedPreferences("com.notonepine.lunchroulette", Context.MODE_PRIVATE);
        return mView;
    }

    @Override
    public void onResume() {
        if (mPrefs.getString(LunchRouletteFragmentActivity.USER_ID, "") != "") {
            moveToHomescreen();
        }
        super.onResume();
    }

    private OnClickListener loginClick() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToHomescreen();

                // // TODO Start loading animation
                // ParseFacebookUtils.logIn(fbPermissions(), getActivity(), new LogInCallback() {
                // @Override
                // public void done(ParseUser user, ParseException err) {
                // if (user == null) {
                // Log.d("MyApp", "Exception: " + err.getMessage());
                // } else {
                // getUserData();
                // }
                // }
                // });
            }
        };
    }

    private List<String> fbPermissions() {
        return Arrays.asList(Permissions.User.LIKES, Permissions.User.INTERESTS, Permissions.User.EDUCATION_HISTORY);
    }

    private void getUserData() {
        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                // TODO check for errors on response
                storeFacebookData(user);
                sendUserToServer(user);
            }
        });
        request.executeAsync();
    }

    private void sendUserToServer(GraphUser user) {
        JSONObject data = user.getInnerJSONObject();
        String GCMID = mPrefs.getString(LunchRouletteFragmentActivity.PROPERTY_REG_ID, "");
        try {
            data.put("GCM_ID", GCMID);
        } catch (JSONException e) {
            // TODO Probably do something about this.
            e.printStackTrace();
        }
        NetworkUtils.postUser(data, userSignup());
    }

    private AsyncHttpResponseHandler userSignup() {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    storeUserId(new JSONObject(response).getString("id"));
                } catch (JSONException e) { /* fuck it */ };

                moveToHomescreen();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("MyApp", "Status: " + statusCode);
            }
        };
    }

    private void storeFacebookData(GraphUser user) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(LunchRouletteFragmentActivity.FACEBOOK_USER_ID, user.getId());
        editor.putString(LunchRouletteFragmentActivity.FACEBOOK_FULL_NAME, user.getName());
        editor.commit();
    }

    private void storeUserId(String id) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(LunchRouletteFragmentActivity.USER_ID, id);
        editor.commit();
    }

    private void moveToHomescreen() {
        FragmentTransaction ft = getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in,
                                        android.R.anim.fade_out).addToBackStack(null);
        ft.replace(R.id.container, new HomeFragment());
        ft.commit();
    }
}
