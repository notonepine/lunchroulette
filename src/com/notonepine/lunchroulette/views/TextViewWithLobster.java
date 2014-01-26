package com.notonepine.lunchroulette.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.notonepine.lunchroulette.LunchRouletteFragmentActivity;

public class TextViewWithLobster extends TextView {

    public TextViewWithLobster(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(LunchRouletteFragmentActivity.lobster);
    }

    public TextViewWithLobster(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface(LunchRouletteFragmentActivity.lobster);
    }

    public TextViewWithLobster(Context context) {
        super(context);
        this.setTypeface(LunchRouletteFragmentActivity.lobster);
    }

}