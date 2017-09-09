package com.feel.weather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;

import com.feel.weather.DBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AddFriendsActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayAdapter<String> newDevices;
    private BluetoothChatService mChatService = null;
    String bluetoothdata;
    String dummy = "//";
    String dataarray[];
    String mac;
    Boolean overlap = false;
    List<String> adress = new ArrayList();
    ProgressDialog dialog;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int STATE_ACCEPT = 6;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        DBHelper db = new DBHelper(getApplicationContext());

        c = this;

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        mChatService = new BluetoothChatService(this, mHandler);

        startDiscovery();
        ensureDiscoverable();

        newDevices = new ArrayAdapter<String>(this, R.layout.name);

        ListView newDeviceView = (ListView) findViewById(R.id.listView);
        newDeviceView.setAdapter(newDevices);
        newDeviceView.setOnItemClickListener(mDeviceClickListener);

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int count = 0;
            Boolean alreadyfound = false;

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                for(int i = 0; i < count; i++) {
                    if(device.getAddress().equals(adress.get(i))) {
                          Log.d("fordebug", device.getAddress());
                          Log.d("fordebug2", adress.get(i));
                          alreadyfound = true;
                    }
                }
                if (alreadyfound == false) {
                    newDevices.add(" " + device.getName() + "\n" + device.getAddress());
                    adress.add(device.getAddress());
                    count++;
                    Log.d("Device name", device.getName());
                    Log.d("Device address", device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
            }
        }
    };

    private void startDiscovery() {
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();
    }

    private void ensureDiscoverable() {
        Log.d("TAG", "ensure discoverable");
        if (mBtAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            DBHelper db = new DBHelper(getApplicationContext());
            List<Info> infos = db.getAllContacts();

            SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = mPref.edit();
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String address = adress.get(arg2);
            if ((mBtAdapter != null) && (mBtAdapter.isEnabled()))
            {
                mac = mBtAdapter.getDefaultAdapter().getAddress();
            }
            editor.putString("macaddress", mac);
            editor.commit();
            Log.d("char", address);

            for(Info io : infos) {
                Log.d("char2", io.getMacaddress());
                if(address.equals(io.getMacaddress())) {
                    overlap = true;
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddFriendsActivity.this);
                    builder.setTitle("알림");
                    builder.setMessage("이미 추가된 사용자입니다.");
                    builder.setNeutralButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            }

            if(overlap == false) {
                dialog = ProgressDialog.show(c, "",
                        "친구의 프로필을 불러오는 중입니다.", true);
                timerDelayRemoveDialog(15000, dialog);
                bluetoothdata = mPref.getString("username", "") + dummy +  Integer.toString(mPref.getInt("userage", 0)) + dummy + mPref.getString("usergender", "") + dummy + mPref.getString("userintroduce", "") + dummy + mPref.getString("userfeel", "") + dummy + mPref.getString("macaddress", "");

                // Get the BluetoothDevice object
                BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                mChatService.connect(device, false);
            }
        }
    };

    public void sendMessage(String message) {

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
            Log.d("Send it", "Already!");

        }
    }


    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler;
    {
        mHandler = new Handler() {
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

                    case STATE_ACCEPT:
              /*        원래는 연결을 받는 기기에도 Progressdialog를 나오게 할려고 하였으나 제대로 작동되지도 않고 Force Close의 원인이 되어 주석처리.
                        dialog = ProgressDialog.show(c, "",
                                "친구의 프로필을 불러오는 중입니다.", true);
                        timerDelayRemoveDialog(15000, dialog); */
                        break;

                    case MESSAGE_WRITE:
                        break;

                    case MESSAGE_READ:
                        DBHelper db = new DBHelper(getApplicationContext());
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        dataarray = readMessage.split("//");
                        db.addInfo(new Info(dataarray[0], dataarray[1], dataarray[2], dataarray[3], dataarray[4], dataarray[5]));
                        Intent intent2;
                        intent2 = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent2);
                        if(dialog != null) {
                            dialog.dismiss();
                        }
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

    @Override
    public void onDestroy() {
         super.onDestroy();

        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(AddFriendsActivity.this);
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
