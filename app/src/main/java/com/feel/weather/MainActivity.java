package com.feel.weather;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    TextView t1;
    ImageButton b1;
    GridView friends;
    BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_ENABLE_BT = 2;
    Boolean check;
    private ArrayList<String> feel;
    private ArrayList<Integer> feelimage;
    int friendcount = 0;
    int myfeel, addfeel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper db = new DBHelper(getApplicationContext());

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean firstrun = mPref.getBoolean("isuseravailable", true);
        if (firstrun) {
            Intent intent;
            intent = new Intent(MainActivity.this, AddUserActivity.class);
            startActivity(intent);
            finish();

            if (mBtAdapter == null) {
                //블루투스를 지원하지 않는다.
            }

            if (!mBtAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        String state = mPref.getString("userfeel", "");
        if(state.equals("맑음")) {
           myfeel = R.drawable.sun;
        } else if(state.equals("비옴")) {
           myfeel = R.drawable.rain;
        } else if(state.equals("눈")) {
            myfeel = R.drawable.snow;
        } else if(state.equals("안개")) {
            myfeel = R.drawable.fog;
        } else if(state.equals("폭풍")) {
            myfeel = R.drawable.storm;
        } else if(state.equals("번개")) {
            myfeel = R.drawable.thndr;
        }

        feel = new ArrayList<String>();
        feelimage = new ArrayList<Integer>();

        feel.add(mPref.getString("username", ""));
        feelimage.add(myfeel);

        List<Info> infos = db.getAllContacts();

        for(Info io : infos) {
             String name = io.getName();
             String feelstatus = io.getFeel();
             Log.d("name", name);
             Log.d("feel" ,feelstatus);
             if(feelstatus.equals("맑음")) {
                 addfeel = R.drawable.sun;
             } else if(feelstatus.equals("비옴")) {
                 addfeel = R.drawable.rain;
             } else if(feelstatus.equals("눈")) {
                 addfeel = R.drawable.snow;
             } else if(feelstatus.equals("안개")) {
                 addfeel = R.drawable.fog;
             } else if(feelstatus.equals("폭풍")) {
                 addfeel = R.drawable.storm;
             } else if(feelstatus.equals("번개")) {
                 addfeel = R.drawable.thndr;
             }

             feel.add(name);
             feelimage.add(addfeel);
        }


        GridView gridview = (GridView)findViewById(R.id.gridView);
        gridview.setAdapter(new FriendAdapter(this, feel, feelimage));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0)
                {
                    Intent intent2;
                    intent2 = new Intent(MainActivity.this, MyProFileActivity.class);
                    startActivity(intent2);
                }
                else {
                    Intent intent2;
                    intent2 = new Intent(MainActivity.this, FriendsActivity.class);
                    intent2.putExtra("number", i);
                    startActivity(intent2);
                }
            }
        });

        b1 = (ImageButton) findViewById(R.id.button);
        b1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent2;
                intent2 = new Intent(MainActivity.this, AddFriendsActivity.class);
                startActivity(intent2);
            }
        });


    }


}
