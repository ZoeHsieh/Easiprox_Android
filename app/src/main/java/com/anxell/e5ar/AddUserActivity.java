package com.anxell.e5ar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.anxell.e5ar.custom.EditCardView;
import com.anxell.e5ar.custom.MyEditText;
import com.anxell.e5ar.custom.MyTextWatcher;
import com.anxell.e5ar.custom.MyToolbar;
import com.anxell.e5ar.data.UserData;
import com.anxell.e5ar.transport.APPConfig;
import com.anxell.e5ar.transport.BPprotocol;
import com.anxell.e5ar.transport.GeneralDialog;
import com.anxell.e5ar.transport.bpActivity;
import com.anxell.e5ar.util.Util;

public class AddUserActivity extends bpActivity implements View.OnClickListener {
    private final String TAG = AddUserActivity.class.getSimpleName().toString();
    private final boolean debugFlag = true;
    private MyEditText mIdET;
    private MyEditText mPasswordET;
    private EditCardView mCardET;
    private  MyToolbar toolbar;
    private boolean isADDOK = false;
    private int mFrom;
    private String device_BDADDR ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Initial(getLocalClassName());
        if(APPConfig.deviceType == APPConfig.deviceType_Keypad)
            setContentView(R.layout.activity_add_user);
        else
            setContentView(R.layout.activity_add_user_card);

        findViews();
        setListeners();

        Bundle bundle = getIntent().getExtras();
        mFrom = bundle.getInt("from");
        device_BDADDR = bundle.getString(APPConfig.deviceBddrTag);
        toolbar.setRightEnableColor(false);

    }

    private void findViews() {
        mIdET = (MyEditText) findViewById(R.id.id);
        mPasswordET = (MyEditText) findViewById(R.id.password);
        mIdET.setTextChangedListener(textWatcher);
        mPasswordET.setInputType(this.getResources().getConfiguration().KEYBOARD_12KEY);
        mPasswordET.setTextChangedListener(textWatcher);
        if(APPConfig.deviceType != APPConfig.deviceType_Keypad){
        mCardET = (EditCardView) findViewById(R.id.userCard);
            mCardET.initCardView(BPprotocol.spaceCardStr);
            mCardET.setTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    isUserDataLength();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    mCardET.moveCursor();
                }
            });
        }

        mCardET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    mCardET.findViewById(R.id.editText_Users_Edit_Dialog_Card1).requestFocus();
                }
            }
        });

    }
    private void isUserDataLength(){

        if ((mPasswordET.getText().length() <4) || (mIdET.getText().isEmpty() ) || ((!mCardET.getCard().isEmpty()) &&  (mCardET.getCard().length() != BPprotocol.userCard_maxLen ))) {
            isADDOK = false;

        }else
            isADDOK = true;
        toolbar.setRightEnableColor(isADDOK);
        Util.debugMessage(TAG,"flag="+((mPasswordET.getText().length() <4) && (mIdET.getText().isEmpty() )&&((!mCardET.getCard().isEmpty()) &&  (mCardET.getCard().length() != BPprotocol.userCard_maxLen ))),true);



    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            isUserDataLength();
        }

        @Override
        public void afterTextChanged(Editable s) {




        }
    };
    private void setListeners() {
        toolbar = (MyToolbar) findViewById(R.id.toolbarView);
        toolbar.hideNavigationIcon();
        toolbar.setRightBtnClickListener(this);
        toolbar.setLeftBtnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rightTV:


                toolbar.setRightEnableColor(isADDOK);
                if(isADDOK){

                    if (isOkForData()) {
                        saveData();
                    }
                }
                break;

            case R.id.leftTV:
                onBackPressed();
                break;
        }
    }

    private boolean isOkForData() {
        String id = mIdET.getText();
        String password = mPasswordET.getText();
        String currAdminPWD = sharedPreferences.getString(APPConfig.ADMINPWD_Tag+device_BDADDR,"");
        String currAdminCARD = "";
        String card = "";
        if(APPConfig.deviceType != APPConfig.deviceType_Keypad){
            currAdminCARD = sharedPreferences.getString(APPConfig.ADMINCARD_Tag + device_BDADDR, "");
            card = mCardET.getCard();
        }

        boolean isDuplicated_Name = Util.checkUserDuplicateByName( id, mUserDataList);
        boolean isDuplicated_Password = Util.checkUserDuplicateByPassword(password,mUserDataList);
        boolean isDuplicated_Card =Util.checkUserDuplicateByCard(card,mUserDataList);
        boolean isAdminName1 = Util.checkUserNameAdmin(id.toUpperCase(), APPConfig.ADMIN_ID);
        boolean isAdminName2 = Util.checkUserNameAdmin(id.toUpperCase(), APPConfig.ADMIN_ENROLL);
        boolean isAdminPassword = password.toString().equals(currAdminPWD);
        boolean isAdminCard = card.toString().equals(currAdminCARD);
        if(!card.isEmpty()){
        try{
        if(Long.parseLong(card) > Long.parseLong(BPprotocol.INVALID_CARD)){
            GeneralDialog.MessagePromptDialog(this, "", getResources().getString(R.string.users_manage_edit_status_Admin_card));
            return false;
        }
        }catch(java.lang.NumberFormatException e){
            GeneralDialog.MessagePromptDialog(this, "", getResources().getString(R.string.users_manage_edit_status_Admin_card));
            return false;
        }
        }
       if (isAdminName1 || isAdminName2) {

           GeneralDialog.MessagePromptDialog(this, "", getResources().getString(R.string.users_manage_edit_status_Admin_name));
            return false;
       }else if(isAdminPassword) {
           GeneralDialog.MessagePromptDialog(this, "", getResources().getString(R.string.users_manage_edit_status_Admin_pwd));
            return false;
       }else if (isDuplicated_Name) {
           //nki_show_toast_msg("Name Duplication!!");
           GeneralDialog.MessagePromptDialog(this, "", getResources().getString(R.string.users_manage_edit_status_duplication_name));
            return false;
       } else if (isDuplicated_Password) {
           //nki_show_toast_msg("Password Duplication!!");
           GeneralDialog.MessagePromptDialog(this, "", getResources().getString(R.string.users_manage_edit_status_duplication_password));
            return  false;
       }else if (isDuplicated_Card && !card.isEmpty()) {
           //nki_show_toast_msg("Password Duplication!!");
           GeneralDialog.MessagePromptDialog(this, "", getResources().getString(R.string.users_manage_edit_status_duplication_card));
           return false;
       }else if (isAdminCard) {
           //nki_show_toast_msg("Password Duplication!!");
           GeneralDialog.MessagePromptDialog(this, "", getResources().getString(R.string.users_manage_edit_status_Admin_card));
           return false;
       }
           return true;
    }

    private void saveData() {
        if(APPConfig.deviceType == APPConfig.deviceType_Keypad)
            UsersListActivity.userInfoData = new UserData(mIdET.getText(),mPasswordET.getText(),BPprotocol.spaceCardStr,0);
        else {
            if(!mCardET.getCard().isEmpty())
                UsersListActivity.userInfoData = new UserData(mIdET.getText(), mPasswordET.getText(), mCardET.getCard(), 0);
            else
                UsersListActivity.userInfoData = new UserData(mIdET.getText(), mPasswordET.getText(), BPprotocol.spaceCardStr, 0);

        }
        UsersListActivity.updateStatus = UsersListActivity.up_user_Add;
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        switch (mFrom) {
            case Config.FROM_USER_1_PAGE:
                overridePendingTransitionTopToBottom();
                break;

            case Config.FROM_USER_2_PAGE:
                overridePendingTransitionLeftToRight();
                break;
        }
    }
}
