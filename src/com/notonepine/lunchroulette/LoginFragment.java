package com.notonepine.lunchroulette;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

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
                FragmentTransaction ft = getFragmentManager().beginTransaction()
                                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                .addToBackStack(null);

                ft.replace(R.id.container, new HomeFragment());
                ft.commit();
            }
        };
    }
}
