package com.simonetta.cannone;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Simonetta on 22/03/17.
public class PlayActivity extends Activity{
    private ImageView cannon;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        cannon = new ImageView(this);
        cannon.setImageDrawable(getResources().getDrawable(R.drawable.cannon));
        cannon.setRotation(45);
        setContentView(cannon);
    }

    @Override
    protected void onResume(){
        super.onResume();
        ObjectAnimator.ofFloat(cannon,"rotation",360).setDuration(10000).start();
    }
}
 */



import android.view.View.OnTouchListener;

public class PlayActivity extends Activity implements OnTouchListener{
    private ImageView wheel;
    private double mCurrAngle = 0;
    private double mPrevAngle = 0;
    ImageView bask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        wheel=(ImageView)findViewById(R.id.imagecannon);
        wheel.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        //trova il baricentro
        final float xc = wheel.getWidth() / 2;
        final float yc = wheel.getHeight() / 2;

        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
               //wheel.clearAnimation(); //secondo colpo torna a 0
                mCurrAngle = Math.toDegrees(Math.atan2(x - xc, yc - y));
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mPrevAngle = mCurrAngle;
                mCurrAngle = Math.toDegrees(Math.atan2(x - xc, yc - y));
                animate(mPrevAngle, mCurrAngle, 0);
                System.out.println(mCurrAngle);
                break;
            }
            case MotionEvent.ACTION_UP : {

                mCurrAngle = Math.toDegrees(Math.atan2(x - xc, yc - y));

                if (mCurrAngle <305 && mCurrAngle>235){
                    mCurrAngle=270;
                    animate(mPrevAngle, mCurrAngle, 0);
                }

                if (mCurrAngle <235 && mCurrAngle>167.5){
                    mCurrAngle=200;
                    animate(mPrevAngle, mCurrAngle, 0);
                }

                if (mCurrAngle <167.5 && mCurrAngle>90){
                    mCurrAngle=135;
                    animate(mPrevAngle, mCurrAngle, 0);
                }

                if (mCurrAngle <90 && mCurrAngle>32.5){
                    mCurrAngle=45;
                    animate(mPrevAngle, mCurrAngle, 0);
                }
              /*  if ((mCurrAngle <32.5 && mCurrAngle>=0)||(mCurrAngle<360 && mCurrAngle>=305)){
                    mCurrAngle=340;
                    animate(mPrevAngle, mCurrAngle, 0);
                }*/
            break;
            }
        }
        return true;
    }

    private void animate(double fromDegrees, double toDegrees, long durationMillis) {
        final RotateAnimation rotate = new RotateAnimation((float) fromDegrees, (float) toDegrees,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(durationMillis);
        rotate.setFillEnabled(true);
        rotate.setFillAfter(true);

        //per settare l'arco di rotazione
       /* if(mCurrAngle<=0 && mCurrAngle>180) {
            animate(45, 45, 0);
        }*/

        wheel.startAnimation(rotate);
        System.out.println(mCurrAngle);
    }

}

//SNAP calamita
