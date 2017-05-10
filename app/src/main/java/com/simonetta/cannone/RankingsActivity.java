package com.simonetta.cannone;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Simonetta on 22/03/17.
 */

public class RankingsActivity extends AppCompatActivity {

    private ListView lv;
    private String[] items={"Alfa", "Beta", "Gamma", "Delta", "Eta", "Theta",
            "Iota", "Kappa", "Lambda", "Mi", "Ni", "Xi", "Omicron" };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankings);

        lv=(ListView)findViewById(R.id.lv);
        BaseAdapter a=new BaseAdapter() {
            @Override
            public int getCount() {
                return items.length-3;
            }
            @Override
            public Object getItem(int position) {
                return items[position];
            }
            @Override
            public long getItemId(int position) {
                return 0;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v;
                if (convertView==null) v=getLayoutInflater().inflate(R.layout.adapter_rankings, parent, false);
                else
                    v=convertView;
                TextView tv =(TextView)v.findViewById(R.id.tv1);
                tv.setText(items[position]);
                return v;
            }
        };
        lv.setAdapter(a);
    }
}
