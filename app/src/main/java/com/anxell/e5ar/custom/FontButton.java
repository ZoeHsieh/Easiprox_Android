package com.anxell.e5ar.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.Button;

import com.anxell.e5ar.Config;


/**
 * Created by nsdi-monkey on 2017/2/14.
 */

public class FontButton extends AppCompatButton {

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 設定字型
        setTypeface(Typeface.createFromAsset(context.getAssets(), Config.TYPEFACE));
    }

//    @Override
//    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK)
//        {
//            clearFocus();
//        }
//        return super.onKeyPreIme(keyCode, event);
//    }
}
