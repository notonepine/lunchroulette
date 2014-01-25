package com.notonepine.lunchroulette;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class LoginFragment extends Fragment {

    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login, container, false);
        ((Button) mView.findViewById(R.id.login_button)).setOnClickListener(loginClick());
        return mView;
    }

    private OnClickListener loginClick() {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d("MyApp", "View clicked: " + v.toString());
                if (v.getId() == R.id.login_button) {
                    ParseFacebookUtils.logIn(getActivity(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException err) {
                            if (user == null) {
                                Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                                Log.d("MyApp", "Exception: " + err.getMessage());
                            } else if (user.isNew()) {
                                Log.d("MyApp", "User signed up and logged in through Facebook!");
                                moveToHomescreen();
                            } else {
                                Log.d("MyApp", "User logged in through Facebook!");
                                moveToHomescreen();
                            }
                        }
                    });
                }

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
