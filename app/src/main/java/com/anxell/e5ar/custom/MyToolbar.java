package com.anxell.e5ar.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.anxell.e5ar.R;

/**
 * Created by kay on 2017/6/8.
 */

public class MyToolbar extends FrameLayout {
    private Toolbar mToolbar;
    private FontTextView mLeftTV;
    private FontTextView mTitleTV;
    private FontTextView mRightTV;
    private ImageButton mRight1IB;
    private ImageButton mRight2IB;

    public MyToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.my_toolbar, this);

        mLeftTV = (FontTextView) findViewById(R.id.leftTV);
        mTitleTV = (FontTextView) findViewById(R.id.title);
        mRightTV = (FontTextView) findViewById(R.id.rightTV);
        mRight1IB = (ImageButton) findViewById(R.id.rightIcon1);
        mRight2IB = (ImageButton) findViewById(R.id.rightIcon2);

        showMyAttrs(context, attrs);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.back);
        ((AppCompatActivity) context).setSupportActionBar(mToolbar);
    }

    public void hideNavigationIcon() {
        mToolbar.setNavigationIcon(null);
    }

    private void showMyAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyToolbarAttr);

        boolean showLeftText = typedArray.getBoolean(R.styleable.MyToolbarAttr_showLeftText, false);
        int leftTextColor = typedArray.getResourceId(R.styleable.MyToolbarAttr_leftTextColor, R.color.green);
        String leftText = typedArray.getString(R.styleable.MyToolbarAttr_leftText);
        if (showLeftText) {
            mLeftTV.setVisibility(View.VISIBLE);
            mLeftTV.setTextColor(getResources().getColor(leftTextColor));
            mLeftTV.setText(leftText);
        }

        DisplayMetrics metric = new DisplayMetrics();
        WindowManager gm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        gm.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 螢幕寬度（畫素）
        int height = metric.heightPixels;   // 螢幕高度（畫素）
        float density = metric.density;      // 螢幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 螢幕密度DPI（120 / 160 / 240）
        mTitleTV.setMaxWidth(width/100 * 50);
        String titleText = typedArray.getString(R.styleable.MyToolbarAttr_titleText);

        mTitleTV.setText(titleText);

        boolean showRight1Icon = typedArray.getBoolean(R.styleable.MyToolbarAttr_showRight1Icon, false);
        Drawable right1Icon = typedArray.getDrawable(R.styleable.MyToolbarAttr_right1Icon);
        if (showRight1Icon) {
            mRight1IB.setVisibility(View.VISIBLE);
            mRight1IB.setImageDrawable(right1Icon);
        }

        boolean showRight2Icon = typedArray.getBoolean(R.styleable.MyToolbarAttr_showRight2Icon, false);
        Drawable right2Icon = typedArray.getDrawable(R.styleable.MyToolbarAttr_right2Icon);
        if (showRight2Icon) {
            mRight2IB.setVisibility(View.VISIBLE);
            mRight2IB.setImageDrawable(right2Icon);
        }

        boolean showRightText = typedArray.getBoolean(R.styleable.MyToolbarAttr_showRightText, false);
        int rightTextColor = typedArray.getResourceId(R.styleable.MyToolbarAttr_rightTextColor, R.color.green);
        String rightText = typedArray.getString(R.styleable.MyToolbarAttr_rightText);
        if (showRightText) {
            mRightTV.setVisibility(View.VISIBLE);
            mRightTV.setTextColor(getResources().getColor(rightTextColor));
            mRightTV.setText(rightText);
        }

        typedArray.recycle();
    }

    public void setLeftBtnClickListener(OnClickListener listener) {
        mLeftTV.setOnClickListener(listener);
    }

    public void setRightBtnClickListener(OnClickListener listener) {
        mRightTV.setOnClickListener(listener);
    }

    public void setRight1IconClickListener(OnClickListener listener) {
        mRight1IB.setOnClickListener(listener);
    }

    public void setRight2IconClickListener(OnClickListener listener) {
        mRight2IB.setOnClickListener(listener);
    }
    public void setRightEnableColor(boolean enable){

                if(enable)
                    mRightTV.setTextColor(getResources().getColor(R.color.green));
                else{

                    mRightTV.setTextColor(getResources().getColor(R.color.gray5));
                }
    }
}