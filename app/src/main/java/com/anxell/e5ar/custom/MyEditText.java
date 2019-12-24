package com.anxell.e5ar.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.anxell.e5ar.R;

/**
 * Created by kay on 2017/6/8.
 */

public class MyEditText extends FrameLayout {

    private FontEditText mValueET;
    private LinearLayout mLayOut;
    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.my_edittext, this);

        mValueET = (FontEditText)findViewById(R.id.value);
        mLayOut = (LinearLayout)findViewById(R.id.myEditTextLayOut);
        showMyAttrs(context, attrs);
    }

    private void showMyAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyEditTextAttr);
        String subject = typedArray.getString(R.styleable.MyEditTextAttr_subject);
        Boolean isshowNote = typedArray.getBoolean(R.styleable.MyEditTextAttr_showNote,false);
        FontTextView subjectTV = (FontTextView) findViewById(R.id.subject);
        FontTextView noteTV = (FontTextView) findViewById(R.id.note);
        subjectTV.setText(subject);

        if (isshowNote == true)
        {
            noteTV.setVisibility(VISIBLE);
        }
        else {
            noteTV.setVisibility(GONE);
        }



        int inputType = typedArray.getInt(R.styleable.MyEditTextAttr_android_inputType, EditorInfo.TYPE_CLASS_TEXT);
        mValueET.setInputType(inputType);

        int hint = typedArray.getResourceId(R.styleable.MyEditTextAttr_android_hint, R.string.app_name);
        mValueET.setHint(hint);

        int maxLength = typedArray.getInt(R.styleable.MyEditTextAttr_android_maxLength, 0);
        if (maxLength != 0) {
            mValueET.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        }

        typedArray.recycle();
    }

    public String getText() {
        return mValueET.getText().toString();
    }

    public void setText(String value) {
        mValueET.setText(value);
    }

    public void setTextChangedListener(TextWatcher watcher){
        if(watcher != null)
        mValueET.addTextChangedListener(watcher);
    };
    public int getLength(){

    return mValueET.length();
    }
    public void setHintText(String Text){

         mValueET.setHint(Text);
    }
    public void setInputType(int type){
        mValueET.setInputType(type);
        NumberKeyListener myKeyListener = new NumberKeyListener() {
            public int getInputType()
            {
                //指定键盘类型
                return InputType.TYPE_CLASS_PHONE;
            }

            protected char[] getAcceptedChars()
            {
                //指定你所接受的字符
                String txt = "1234567890";
                return txt.toCharArray();
            }
        };

        mValueET.setKeyListener(myKeyListener);
    }

    public void setViewFocusable(boolean enable){
        mValueET.setClickable(false);
        //setFocusable(enable);
        mLayOut.setFocusable(true);
        mLayOut.setClickable(true);
        mLayOut.setFocusableInTouchMode(true);
    }
    public void setCursorEnable(boolean enable){
        mValueET.setCursorVisible(enable);
    }

    public void setETVOnClickListener(OnClickListener listener){

        mValueET.setOnClickListener(listener);
    }
    public void addTextChangedListener(TextWatcher watcher){
        mValueET.addTextChangedListener(watcher);
    }
    public void setTextTag(String tag){
        mValueET.setTag(tag);
    }

    public void setETSelection(int position){
        mValueET.setSelection(position);
    }

    public void setRawInputType (int InputType){
        mValueET.setRawInputType(InputType);
    }
}