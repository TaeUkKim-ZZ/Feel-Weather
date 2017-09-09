package com.feel.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MyProFileActivity extends ActionBarActivity {

    TextView name, age, gender, introduce, feel;
    String name2, gender2, introduce2, feel2;
    int age2;
    ImageView feelimage;
    ImageButton editbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pro_file);

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);

        name = (TextView)findViewById(R.id.nowname);
        age = (TextView)findViewById(R.id.nowage);
        gender = (TextView)findViewById(R.id.nowgender);
        introduce = (TextView)findViewById(R.id.nowintroduce);
        feel = (TextView)findViewById(R.id.nowfeel);
        feelimage = (ImageView)findViewById(R.id.feelimage);
        editbutton = (ImageButton)findViewById(R.id.edit);

        editbutton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent2;
                intent2 = new Intent(MyProFileActivity.this, AddUserActivity.class);
                intent2.putExtra("wantedit", true);
                startActivity(intent2);
                finish();
            }
        });

        name2 = mPref.getString("username", "");
        age2 = mPref.getInt("userage", 0);
        gender2 = mPref.getString("usergender", "");
        introduce2 = mPref.getString("userintroduce", "");
        feel2 = mPref.getString("userfeel", "");

        if(feel2.equals("맑음")) {
            feelimage.setImageResource(R.drawable.sun);
        } else if(feel2.equals("비옴")) {
            feelimage.setImageResource(R.drawable.rain);
        } else if(feel2.equals("눈")) {
            feelimage.setImageResource(R.drawable.snow);
        } else if(feel2.equals("안개")) {
            feelimage.setImageResource(R.drawable.fog);
        } else if(feel2.equals("폭풍")) {
            feelimage.setImageResource(R.drawable.storm);
        } else if(feel2.equals("번개")) {
            feelimage.setImageResource(R.drawable.thndr);
        }

        name.setText(name2);
        age.setText(Integer.toString(age2));
        gender.setText(gender2);
        introduce.setText(introduce2);
        feel.setText(feel2);
    }


}
