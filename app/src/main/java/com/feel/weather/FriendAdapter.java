package com.feel.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendAdapter extends BaseAdapter{
    private Context mContext;

    private ArrayList<Integer> feelimage;
    private final ArrayList<String> feel;

    public FriendAdapter(Context c, ArrayList<String> feel, ArrayList<Integer> feelimage){
        mContext = c;
        this.feelimage = feelimage;
        this.feel = feel;
    }

    @Override
    public int getCount() {
        return feelimage.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(view == null) {
             grid = new View(mContext);
             grid = inflater.inflate(R.layout.item, null);
             TextView textView = (TextView) grid.findViewById(R.id.item_text);
             textView.setText(feel.get(i));
             ImageView imageView  = (ImageView) grid.findViewById(R.id.item_image);
             imageView.setImageResource(feelimage.get(i));
        }
        else {
            grid = (View) view;
        }
        return grid;
    }

}
