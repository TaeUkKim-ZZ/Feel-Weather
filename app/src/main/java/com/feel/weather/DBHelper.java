package com.feel.weather;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.feel.weather.Info;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "friendsinfo";

    private static final String TABLE_CONTACTS = "infos";
    private static final String SEQUENCE = "sqlite_sequence";

    private static final String SEQ = "seq";
    private static final String SEQNAME = "infos";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_AGE = "age";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_INTRODUCE = "introduce";
    private static final String KEY_USERFEEL = "userfeel";
    private static final String KEY_MACADDRESS = "macaddress";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE =
                "create table "+TABLE_CONTACTS+"("
                        +KEY_ID+" integer primary key autoincrement, "
                        +KEY_NAME+" text not null , "
                        +KEY_AGE+" text not null , "
                        +KEY_GENDER+" text not null , "
                        +KEY_INTRODUCE+" text not null , "
                        +KEY_USERFEEL+" text not null , "
                        +KEY_MACADDRESS+" text not null );";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        onCreate(db);
    }

    public void addInfo(Info info) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, info.getName());
        values.put(KEY_AGE, info.getAge());
        values.put(KEY_GENDER, info.getGender());
        values.put(KEY_INTRODUCE, info.getIntroduce());
        values.put(KEY_USERFEEL, info.getFeel());
        values.put(KEY_MACADDRESS, info.getMacaddress());

        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    public Cursor getinfo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                        KEY_NAME, KEY_AGE, KEY_GENDER, KEY_INTRODUCE, KEY_USERFEEL, KEY_MACADDRESS }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public List<Info> getAllContacts() {
        List<Info> contactList = new ArrayList<Info>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Info info = new Info();
                info.setID(Integer.parseInt(cursor.getString(0)));
                info.setName(cursor.getString(1));
                info.setAge(cursor.getString(2));
                info.setGender(cursor.getString(3));
                info.setIntroduce(cursor.getString(4));
                info.setFeel(cursor.getString(5));
                info.setMacaddress(cursor.getString(6));
                Log.d("check", cursor.getString(5));
                Log.d("check2", cursor.getString(6));
                contactList.add(info);
            } while (cursor.moveToNext());
        }

        return contactList;
    }

    public int updateContact(Info info) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, info.getName());
        values.put(KEY_AGE, info.getAge());
        values.put(KEY_GENDER, info.getGender());
        values.put(KEY_INTRODUCE, info.getIntroduce());
        values.put(KEY_USERFEEL, info.getFeel());
        values.put(KEY_MACADDRESS, info.getMacaddress());


        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(info.getID()) });
    }

    public void deleteContact(Info info) {
        int ab = 0;
        int id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(info.getID()) });
        Cursor c;
        c = db.query("sqlite_sequence", null, null, null, null, null, null);
        while (c.moveToNext()) {
            id = c.getInt(c.getColumnIndex("seq"));
        }

        ContentValues values = new ContentValues();
        values.put("seq", id-1);
        db.update("sqlite_sequence", values, "name=?", new String[]{"infos"});
        
        db.close();
    }

    public int getInfosCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

}