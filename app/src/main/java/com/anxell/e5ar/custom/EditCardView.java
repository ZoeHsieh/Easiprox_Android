package com.anxell.e5ar.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.anxell.e5ar.R;
import com.anxell.e5ar.transport.BPprotocol;
import com.anxell.e5ar.util.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by kay on 2017/6/8.
 */

public class EditCardView extends FrameLayout {


    private LinearLayout mLayOut;
    private String newCard = "";
    final EditText ArrayCard[] = new EditText[10];
    final int uiCardEditID []={R.id.editText_Users_Edit_Dialog_Card1,R.id.editText_Users_Edit_Dialog_Card2,
            R.id.editText_Users_Edit_Dialog_Card3,R.id.editText_Users_Edit_Dialog_Card4,
            R.id.editText_Users_Edit_Dialog_Card5,R.id.editText_Users_Edit_Dialog_Card6,
            R.id.editText_Users_Edit_Dialog_Card7,R.id.editText_Users_Edit_Dialog_Card8,
            R.id.editText_Users_Edit_Dialog_Card9,R.id.editText_Users_Edit_Dialog_Card10
    };
    public EditCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.edit_card_view, this);


        mLayOut = (LinearLayout)findViewById(R.id.myEditTextLayOut);
        showMyAttrs(context, attrs);
    }

    private void showMyAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyEditTextAttr);
        String subject = typedArray.getString(R.styleable.MyEditTextAttr_subject);
        FontTextView subjectTV = (FontTextView) findViewById(R.id.subject);
        subjectTV.setText(subject);

        int inputType = typedArray.getInt(R.styleable.MyEditTextAttr_android_inputType, EditorInfo.TYPE_CLASS_TEXT);


        int hint = typedArray.getResourceId(R.styleable.MyEditTextAttr_android_hint, R.string.app_name);


        int maxLength = typedArray.getInt(R.styleable.MyEditTextAttr_android_maxLength, 0);


        typedArray.recycle();
    }

   public void initCardView(String currentCard) {



        for(int i=0;i<uiCardEditID.length;i++)
            ArrayCard[i] = (EditText) findViewById(uiCardEditID[i]);
       for(int i=0;i<uiCardEditID.length;i++){
           if(currentCard.length()< uiCardEditID.length)
                ArrayCard[i].setText("");
            else
                ArrayCard[i].setText(currentCard.substring(i,i+1));
            ArrayCard[i].setTextColor(Color.BLACK);
            ArrayCard[i].setRawInputType(getResources().getConfiguration().KEYBOARD_12KEY);
        }
       for(int i=0;i<uiCardEditID.length;i++)
           newCard += ArrayCard[i].getText().toString();

       for(int i=0;i<uiCardEditID.length;i++) {
            ArrayCard[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {




                }


            });
            ArrayCard[i].setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        for (int i = 0; i < uiCardEditID.length; i++) {
                            if (ArrayCard[i].isFocused() && i != 0 && ArrayCard[i].length() == 0) {
                                ArrayCard[i - 1].requestFocus();


                                i = uiCardEditID.length + 1;
                            }
                        }
                    }
                    return false;
                }
            });
        }
    }

    public void setTextChangedListener(TextWatcher watcher){
        for(int i=0;i<uiCardEditID.length;i++)
            ArrayCard[i].addTextChangedListener(watcher);

    }
    public void moveCursor(){
        for (int i = 0; i < uiCardEditID.length; i++) {
            if (ArrayCard[i].length() == 1 && ArrayCard[i].isFocused() && (i < (uiCardEditID.length - 1))) {
                ArrayCard[i + 1].requestFocus();
                i = uiCardEditID.length + 1;
            }
        }

    }
    public void setCard(String cardStr) {

        for(int i=0;i<uiCardEditID.length;i++) {
            if(cardStr.equals(BPprotocol.spaceCardStr))
                ArrayCard[i].setText("");
            else
                ArrayCard[i].setText(cardStr.substring(i,i+1));
        }

    }

    public String getCard() {
        newCard = "";
        for(int i=0;i<uiCardEditID.length;i++)
            newCard += ArrayCard[i].getText().toString();
        return newCard;
    }


   public int getCardLength(){


       int cardNum = 0;

       for (int i = 0; i < uiCardEditID.length; i++)
           cardNum += ArrayCard[i].length();
       return cardNum;
   }





    public void setViewFocusable(boolean enable){

        //setFocusable(enable);
        mLayOut.setFocusable(true);
        mLayOut.setClickable(true);
        mLayOut.setFocusableInTouchMode(true);
    }


}