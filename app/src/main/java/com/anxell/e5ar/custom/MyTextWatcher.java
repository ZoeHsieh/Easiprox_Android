package com.anxell.e5ar.custom;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.widget.EditText;

/**
 * Created by Sean on 12/25/2017.
 */

public  class MyTextWatcher implements TextWatcher {

    private EditText mEditText;

    public MyTextWatcher(EditText editText) {
        mEditText = editText;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
    @Override
    public void afterTextChanged(Editable s) {




    }



    public void setInputType(int type){
        mEditText.setInputType(type);
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

        mEditText.setKeyListener(myKeyListener);
    }
}