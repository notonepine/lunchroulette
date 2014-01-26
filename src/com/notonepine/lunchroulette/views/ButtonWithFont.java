package com.notonepine.lunchroulette.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.notonepine.lunchroulette.LunchRouletteFragmentActivity;

public class ButtonWithFont extends Button {

    public ButtonWithFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(LunchRouletteFragmentActivity.openSans);
    }

    public ButtonWithFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface(LunchRouletteFragmentActivity.openSans);
    }

    public ButtonWithFont(Context context) {
        super(context);
        this.setTypeface(LunchRouletteFragmentActivity.openSans);
    }

}