package com.feel.weather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsActivity extends ActionBarActivity {

    int row, id;
    TextView name, age, gender, introduce, feel;
    String name2, gender2, introduce2, feel2, macaddress, age2;
    String bluetoothdata;
    String dataarray[];
    ImageView feelimage2;
    ImageButton refreshbutton, deletebutton;
    BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothChatService mChatService = null;
    String dummy = "//";
    ProgressDialog dialog;
    Context c;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        c = this;

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        bluetoothdata = mPref.getString("username", "") + dummy +  Integer.toString(mPref.getInt("userage", 0)) + dummy + mPref.getString("usergender", "") + dummy + mPref.getString("userintroduce", "") + dummy + mPref.getString("userfeel", "") + dummy + mPref.getString("macaddress", "");

        mChatService = new BluetoothChatService(this, mHandler2);

        name = (TextView)findViewById(R.id.friendname2);
        age = (TextView)findViewById(R.id.friendage2);
        gender = (TextView)findViewById(R.id.friendgender2);
        introduce = (TextView)findViewById(R.id.friendintroduce2);
        feel = (TextView)findViewById(R.id.friendfeel2);
        feelimage2 = (ImageView)findViewById(R.id.friendsimage);
        refreshbutton = (ImageButton)findViewById(R.id.refresh);
        deletebutton = (ImageButton)findViewById(R.id.delete);

        Cursor cursor;

        Intent intent2 = getIntent();
        final DBHelper db = new DBHelper(getApplicationContext());

        row = intent2.getExtras().getInt("number");
        cursor = db.getinfo(row);

        id = Integer.parseInt(cursor.getString(0));
        name2 = cursor.getString(1);
        gender2 = cursor.getString(3);
        introduce2 = cursor.getString(4);
        feel2 = cursor.getString(5);
        macaddress = cursor.getString(6);
        age2 = cursor.getString(2);
        Log.d("set", feel2);

        name.setText(name2);
        gender.setText(gender2);
        age.setText(age2);
        introduce.setText(introduce2);
        feel.setText(feel2);
        if(feel2.equals("맑음")) {
            feelimage2.setImageResource(R.drawable.sun);
        } else if(feel2.equals("비옴")) {
            feelimage2.setImageResource(R.drawable.rain);
        } else if(feel2.equals("눈")) {
            feelimage2.setImageResource(R.drawable.snow);
        } else if(feel2.equals("안개")) {
            feelimage2.setImageResource(R.drawable.fog);
        } else if(feel2.equals("폭풍")) {
            feelimage2.setImageResource(R.drawable.storm);
        } else if(feel2.equals("번개")) {
            feelimage2.setImageResource(R.drawable.thndr);
        }

        refreshbutton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                dialog = ProgressDialog.show(c, "",
                        "친구의 프로필을 불러오는 중입니다.", true);
                timerDelayRemoveDialog(15000, dialog);
                // Get the BluetoothDevice object
                BluetoothDevice device = mBtAdapter.getRemoteDevice(macaddress);
                // Attempt to connect to the device
                mChatService.connect(device, false);
            }
        });

        deletebutton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                builder.setMessage("정말 삭제하시겠습니까?")
                       .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {
                                       db.deleteContact(new Info(id, name2, age2, gender2, introduce2, feel2, macaddress));
                                       Intent intent;
                                       intent = new Intent(FriendsActivity.this, MainActivity.class);
                                       startActivity(intent);
                                       finish();
                                   }
                               })
                       .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {
                                       //Does Nothing.
                                   }
                               }).show();
            }
        });
    }

    private final Handler mHandler2;
    {
        mHandler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case BluetoothChatService.STATE_CONNECTED:
                                Log.d("device", "Connected!");
                                if (bluetoothdata.length() > 0) {
                                    byte[] send = bluetoothdata.getBytes();
                                    mChatService.write(send);
                                    Log.d("Send it", "Already!");
                                }
                                break;
                            case BluetoothChatService.STATE_CONNECTING:

                                break;
                            case BluetoothChatService.STATE_LISTEN:
                            case BluetoothChatService.STATE_NONE:
                                break;
                        }
                        break;

                    case MESSAGE_WRITE:
                        break;

                    case MESSAGE_READ:
                        DBHelper db = new DBHelper(getApplicationContext());
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        dataarray = readMessage.split("//");
                        db.updateContact(new Info(id, dataarray[0], dataarray[1], dataarray[2], dataarray[3], dataarray[4], dataarray[5]));
                        dialog.dismiss();
                        Intent intent2;
                        intent2 = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent2);
                        finish();
                        break;

                    case MESSAGE_DEVICE_NAME:
                        break;

                    case MESSAGE_TOAST:
                        break;

                }
            }
        };
    }

    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                builder.setTitle("알림");
                builder.setMessage("통신할수 없는 기기 이거나 오류가 발생한것 같습니다. 다시 시도해주세요.");
                builder.setNeutralButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        }, time);
    }


}
