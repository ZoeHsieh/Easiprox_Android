package com.anxell.e5ar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.anxell.e5ar.custom.MyToolbar;
import com.anxell.e5ar.data.UserData;
import com.anxell.e5ar.transport.APPConfig;
import com.anxell.e5ar.transport.BPprotocol;
import com.anxell.e5ar.transport.GeneralDialog;
import com.anxell.e5ar.transport.bpActivity;
import com.anxell.e5ar.util.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Locale;

public class UsersListActivity extends bpActivity implements View.OnClickListener, OnRecyclerViewItemClickListener {
    private final String TAG = UsersListActivity.class.getSimpleName().toString();
    private final boolean debugFlag = true;
    private RecyclerView mRecyclerView;
    private UsersInfoAdapter mAdapter;
    private volatile int userCount = 1;
    private int user_read_retry_cnt=0;
    private int userMax = 0;

    private Thread UserLoadingTHD = null;
    private boolean isLiveUSTHD = false;
    private ProgressDialog progressDialog = null;
    private volatile boolean isCancel = false;
    private UserData userTmpItem = new UserData("","","",0);
    public static UserData userInfoData;
    public static int updateStatus  = 0;
    public final static int up_user_del = 1;
    public final static int up_user_Add = 2;
    public final static int up_none = 0;
    public static boolean isLoadUserListCompleted = false;
    public String deviceBD_ADDR = "";
    ///
    public MyToolbar toolbar;
    private String deviceName =  "";    
    private EditText searchView_ET;
    private Activity curActivity;
    ///
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Initial(getLocalClassName());
        setContentView(R.layout.activity_users_info);
        Intent intent = getIntent();
        deviceBD_ADDR = intent.getStringExtra(APPConfig.deviceBddrTag);
        ///
 	    deviceName = intent.getStringExtra(APPConfig.deviceNameTag);
        curActivity = this;
        ///
	    registerReceiver(mGattUpdateReceiver,  getIntentFilter());
        findViews();
        setListeners();
        setSearchViewTextSize();

        mAdapter = new UsersInfoAdapter(this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        ///
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(searchView_ET != null)
                Util.closeSoftKeybord(searchView_ET,curActivity);

                return false;
            }
        });
        ///
        mAdapter.updateData(mUserDataList);
        bpProtocol.getUsersCount();
       currentClassName = getLocalClassName();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        switch(updateStatus){


            case up_user_del:
                int userIndex = userInfoData.getUserIndex();
                bpProtocol.setUserDel(userIndex);
                userTmpItem = userInfoData;
                break;
            case up_user_Add:
                byte id[] = Util.convertStringToByteBufferForAddUser(userInfoData.getId(),BPprotocol.userID_maxLen);
                byte password[] = Util.convertStringToByteBuffer(userInfoData.getPasswrod(),BPprotocol.userPD_maxLen);
                userTmpItem = new UserData(userInfoData.getId(), userInfoData.getPasswrod(), userInfoData.getCard(), 0);
                if(!APPConfig.deviceType.equals( APPConfig.deviceType_Keypad)) {

                    if(!userInfoData.getCard().equals(BPprotocol.spaceCardStr)) {
                        Long data = Long.parseLong(userInfoData.getCard());
                        byte[] card = Util.hexStringToByteArray(Util.StringDecToUINT8(data));
                        bpProtocol.setUserAdd(password, id,card);
                    }else{
                        ByteBuffer b = ByteBuffer.allocate(BPprotocol.len_Admin_card);
                        b.order(ByteOrder.LITTLE_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
                        for(int i=0;i<BPprotocol.len_Admin_card;i++)
                        b.put((byte)0xFF);
                        Util.debugMessage(TAG,"len="+b.array().length,true);
                        bpProtocol.setUserAdd(password, id, b.array());
                    }


                }else{

                    bpProtocol.setUserAdd(password, id);
                }

                break;
        }
        mAdapter.updateData(mUserDataList);
        updateStatus = up_none;
        currentClassName = getLocalClassName();

    }

    @Override
    public void update_service_connect() {

    }

    private void findViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    private void setListeners() {
        toolbar = (MyToolbar) findViewById(R.id.toolbarView);
        toolbar.setRightBtnClickListener(this);
    }

    private void setSearchViewTextSize() {
        SearchView searchView = (SearchView) findViewById(R.id.searchView);

        LinearLayout linearLayout1 = (LinearLayout) searchView.getChildAt(0);
        LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
        LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
        AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
        autoComplete.setTextSize(13);
        searchView.setQueryHint(getString(R.string.UserList_search_placeHolder));
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                    mAdapter.getFilter().filter(newText);
                    mAdapter.notifyDataSetChanged();

                return false;
            }
        });
        ///
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(!isLoadUserListCompleted){
                        if(progressDialog!=null)
                            progressDialog.show();


                    }
                }
            }
        });



        int id = searchView.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        searchView_ET = (EditText) searchView.findViewById(id);
        searchView_ET.setEnabled(true);////////searchbar?????????
        ///


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rightTV:
                if(isLoadUserListCompleted)
                openAddUserPage();
                else{
                    if(progressDialog!=null)
                    progressDialog.show();
                }
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if(isLoadUserListCompleted)
        openUserInfoPage(mUserDataList.get(position));
        else{
            if(progressDialog!=null)
                progressDialog.show();
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        if(isLoadUserListCompleted)
        showDeleteDialog(position);
        else{
            if(progressDialog!=null)
                progressDialog.show();
        }
    }

    private void openAddUserPage() {
        Intent intent = new Intent(this, AddUserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("from", Config.FROM_USER_2_PAGE);
        bundle.putString(APPConfig.deviceBddrTag,deviceBD_ADDR);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransitionRightToLeft();
    }

    private void openUserInfoPage(UserData data) {
        Intent intent = new Intent(this, UserInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", data);
        bundle.putString(APPConfig.deviceBddrTag,deviceBD_ADDR);
        intent.putExtras(bundle);
        userInfoData =  data;
        startActivity(intent);
        overridePendingTransitionRightToLeft();
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.msg_delete);

        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                userTmpItem = mUserDataList.get(position);
                bpProtocol.setUserDel(userTmpItem.getUserIndex());
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, null);

        dialogBuilder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!isLoadUserListCompleted){
         mUserDataList.clear();
        isCancel  = true;
        }
        overridePendingTransitionLeftToRight();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void cmdAnalysis(byte cmd, byte cmdType, byte data[], int datalen) {
        Util.debugMessage(TAG,"cmdAnalysis",debugFlag);
        String message = "";
        Util.debugMessage(TAG,"current="+currentClassName + "local="+getLocalClassName(),debugFlag);

        switch ((char) cmd) {

            case BPprotocol.cmd_user_info:
                if(data[0]!= (byte)0xFF && data[0]!= (byte)0x00)
                    update_Users_Data(data);
                else
                    user_read_retry_cnt++;

                if(userCount <= userMax) {
                    if(user_read_retry_cnt==5){
                        user_read_retry_cnt=0;
                        userCount++;
                    }
                    bpProtocol.getUserByRange(userCount);

                }
                break;

            case BPprotocol.cmd_user_add:
                if (data[0] == BPprotocol.result_fail && datalen == 1) {

                    message = getResources().getString(R.string.program_fail);

                }else if(datalen == 2){

                    int index = encode.getUnsignedTwoByte(data);
                    Util.debugMessage(TAG,"id="+userTmpItem.getId(),debugFlag);
                    Util.debugMessage(TAG,"pd="+userTmpItem.getPasswrod(),debugFlag);

                    mUserDataList.add(new UserData(userTmpItem.getId(),userTmpItem.getPasswrod(),userTmpItem.getCard(),index));






                }
                userTmpItem = null;

                break;
            case BPprotocol.cmd_user_del:



                if (data[0] == BPprotocol.result_success) {

                    mUserDataList.remove(getUserPosition(userTmpItem.getUserIndex()));
                    Util.debugMessage(TAG,"urse",debugFlag);
                    message = getResources().getString(R.string.program_success);

                }else
                    message = getResources().getString(R.string.program_fail);
               // show_toast_msg(message);


                break;

            case BPprotocol.cmd_user_counter:
                Util.debugMessage(TAG,"old userMax="+userMax,debugFlag);
                Util.debugMessage(TAG,"data="+encode.BytetoHexString(data),debugFlag);
                userMax = encode.getUnsignedTwoByte(data);
                Util.debugMessage(TAG,"New userMax="+userMax,debugFlag);

                Util.debugMessage(TAG,"isLoadUserListCompleted="+isLoadUserListCompleted,debugFlag);


                    if(userMax > 0 &&!isLoadUserListCompleted){
                        mUserDataList.clear();
                        userCount = 1;
                        getUsersList();
                        Users_List_Dialog_Loading();

                    }else if(userMax == 0){
                        userMax =0;
                        mAdapter.notifyDataSetChanged();
                        ///
			searchView_ET.setEnabled(true);
			///

                            isLoadUserListCompleted = true;
                            GeneralDialog.MessagePromptDialog(this, "", getString(R.string.no_user_note));

                    }



                break;

        }
        mAdapter.updateData(mUserDataList);

    }

    private void update_Users_Data(byte[] data) {

        String strPassword="";
        String strName="";
        String strCard ="";


        for(int j = 0; j< BPprotocol.userID_maxLen; j++) {

            if(data[j] != (byte)0xFF && data[j]!=(byte)0x00) {
                try {
                    strName += String.format(Locale.US, "%02x", data[j]);
                } catch (Exception e) {
                    //e.printStackTrace();
                    strName = "????";
                }
            }
        }
        strName = encode.toUtf8(new String(encode.HexStringToBytes(strName)));
        Util.debugMessage(TAG, "strName = [" + strName + "]",debugFlag);

        for(int i = 16; i< BPprotocol.userPD_maxLen+16; i++){
            //Get Password
            try {
                if(data[i]!=(byte)0xFF && data[i]!=(byte)0x00){
                    strPassword += String.format(Locale.US, "%02x", data[i]);
                }
            } catch (Exception e) {
                //e.printStackTrace();
                strPassword = "";
            }

        }
        strPassword = encode.toUtf8(new String(encode.HexStringToBytes(strPassword)));

        byte CardData[] = Arrays.copyOfRange(data,24,28);


        strCard = Util.UINT8toStringDecForCard(CardData,BPprotocol.len_Admin_card);

        Util.debugMessage(TAG,"Password = [" + strPassword + "]",debugFlag);

        int indexH = encode.getUnsignedByte(data[28]);
        int indexL = encode.getUnsignedByte(data[29]);
        int userIndex =  indexH*256 +indexL;
        mUserDataList.add(new UserData(strName, strPassword,strCard , userIndex));

        Util.debugMessage(TAG," user data user index ="+ userIndex ,debugFlag);

        userCount++;



    }

    public void getUsersList(){

        bpProtocol.getUserByRange(userCount);

    }

    private void Users_List_Dialog_Loading() {


        progressDialog = new ProgressDialog(this);

        progressDialog.setMax(userMax);
        progressDialog.setMessage(getString(R.string.download_dialog_message));
        progressDialog.setTitle(getString(R.string.download_dialog_title) + getResources().getString(R.string.settings_users_manage_list));
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressNumberFormat("%1d / %2d");

        isCancel = false;

        progressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getResources().getString(R.string.progress_dialog_hide_btn_title),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();

            }
        });

        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                progressDialog.dismiss();
                UserLoadingTHD.interrupt();
                mUserDataList.clear();
                userCount = 1;
                isCancel = true;
                isLiveUSTHD = false;
                UserLoadingTHD = null;
                onBackPressed();
            }
        });

        progressDialog.show();

        //progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        if(UserLoadingTHD == null) {
            isLiveUSTHD = true;
            UserLoadingTHD =  new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean isDone = false;

                        do {
                            Thread.sleep(150);



                            progressDialog.setProgress(userCount);


                            Util.debugMessage(TAG, "mUsers_Data_Need_Received, userCount = " + userCount + ", Max = " + progressDialog.getMax(), debugFlag);

                            if (progressDialog.getProgress() >= progressDialog.getMax()||userCount>=userMax) {
                                Util.debugMessage(TAG, "progressDialog.getProgress Done !!!", debugFlag);

                                progressDialog.dismiss();
                                isDone = true;
                                isLiveUSTHD = false;
                                UserLoadingTHD.interrupt();
                                UserLoadingTHD = null;
                                isLoadUserListCompleted = true;
                            } else {
                                Util.debugMessage(TAG, "progressDialog.getProgress() = " + progressDialog.getProgress(), debugFlag);
                            }

                        } while ((!isDone) && (!isCancel)&&isLiveUSTHD);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            UserLoadingTHD.start();
        }
    }
}
