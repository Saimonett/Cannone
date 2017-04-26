package com.simonetta.cannone;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
/**
 * Created by Simonetta on 22/03/17.
 */

public class InstructionesActivity extends FragmentActivity {
    ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructiones);
        viewpager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter padapter = new PagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(padapter);
    }
}
