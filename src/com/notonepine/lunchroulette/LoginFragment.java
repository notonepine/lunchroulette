package com.notonepine.lunchroulette;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

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
import com.notonepine.lunchroulette.NetworkService.NetworkResponseListener;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.ParseUser;

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

    private OnClickListener loginClick() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Start loading animation
                ParseFacebookUtils.logIn(fbPermissions(), getActivity(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Exception: " + err.getMessage());
                        } else {
                            getUserData();
                        }
                    }
                });
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
        Log.d("MyApp", "User data to server: " + data.toString());
        // mocked successful response
        userSignup().onSuccess(null);
//        NetworkConnection.getService().post("http://hacktech4.cloudapp.net:80/users/", data.toString(), userSignup());
    }

    private NetworkResponseListener userSignup() {
        return new NetworkResponseListener() {
            @Override
            public void onSuccess(InputStream data) {
                // TODO if new, move to signup flow
                moveToHomescreen();
            }

            @Override
            public void onError(Exception error) {
                // TODO error something...
            }
        };
    }

    private void moveToHomescreen() {
        FragmentTransaction ft = getFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null);
        ft.replace(R.id.container, new HomeFragment());
        ft.commit();
    }

}
