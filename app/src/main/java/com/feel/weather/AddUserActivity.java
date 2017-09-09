package com.feel.weather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class AddUserActivity extends ActionBarActivity {

    ImageButton save;
    EditText name, age, introduce;
    Spinner feel, gender;
    String feelnow, gendernow;
    ArrayAdapter<CharSequence> adfeel, adgender;
    Boolean wantcheck;
    String[] Feel = {"맑음", "비옴", "눈", "안개", "폭풍", "번개"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(AddUserActivity.this);

        Intent intent2 = getIntent();
        wantcheck = false;
        if (getIntent().getBooleanExtra("wantedit", false)  != false){
            wantcheck = true;
            setTitle("프로필 수정");
        }

        name = (EditText)findViewById(R.id.nameedit);
        age = (EditText)findViewById(R.id.ageedit);
        introduce = (EditText)findViewById(R.id.introduceedit);

        feel = (Spinner)findViewById(R.id.feelspinnerr);
        feel.setPrompt("날씨를 선택해주세요");
        adfeel = ArrayAdapter.createFromResource(this, R.array.feelarray, android.R.layout.simple_list_item_checked);
        adfeel.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        feel.setAdapter(adfeel);

        feel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    feelnow = String.valueOf(adfeel.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        gender = (Spinner)findViewById(R.id.genderspinner);
        gender.setPrompt("성별을 선택해주세요");
        adgender = ArrayAdapter.createFromResource(this, R.array.genderarray, android.R.layout.simple_list_item_checked);
        adgender.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        gender.setAdapter(adgender);

        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    gendernow = String.valueOf(adgender.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(wantcheck) {
            SharedPreferences.Editor editor = mPref.edit();
            name.setText(mPref.getString("username", ""), TextView.BufferType.EDITABLE);
            age.setText(Integer.toString(mPref.getInt("userage", 0)), TextView.BufferType.EDITABLE);
            introduce.setText(mPref.getString("userintroduce", ""), TextView.BufferType.EDITABLE);

            String a = mPref.getString("usergender", "");
            gendernow = a;
            if(a.equals("남성")) {
               gender.setSelection(0);
            }
            else {
               gender.setSelection(1);
            }

            String b = mPref.getString("userfeel", "");
            feelnow = b;
            for(int i = 0; i < 6; i++)
            {
                if(Feel[i].equals(feelnow))
                {
                    feel.setSelection(i);
                }
            }
        }
        else {
            feelnow = "남성";
            gendernow = "맑음";
        }

        save = (ImageButton)findViewById(R.id.save);
        save.setOnClickListener(new Button.OnClickListener() {
            SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(AddUserActivity.this);
            public void onClick(View v) {
                SharedPreferences.Editor editor = mPref.edit();
                if(name.getText().toString().equals("") || age.getText().toString().equals("") || introduce.getText().toString().equals("")) {
                    AlertDialog.Builder alertDlg = new AlertDialog.Builder(AddUserActivity.this) ;
                    alertDlg.setTitle("알림") ;
                    alertDlg.setMessage("공백이 있습니다.") ;
                    alertDlg.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    }).show();
                }
                else
                {
                    editor.putString("username", name.getText().toString());
                    editor.putInt("userage", Integer.parseInt(age.getText().toString()));
                    editor.putString("userintroduce", introduce.getText().toString());
                    editor.putString("userfeel", feelnow);
                    editor.putString("usergender", gendernow);
                    editor.putBoolean("isuseravailable", false);
                    editor.commit();
                    Intent intent;
                    intent = new Intent(AddUserActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
