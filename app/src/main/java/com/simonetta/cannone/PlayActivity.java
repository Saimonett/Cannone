package com.simonetta.cannone;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.util.Log;
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
    private Button bShoot;
    LevelListDrawable drawable;
    ImageView bask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        bShoot= (Button)findViewById(R.id.button_shoot);
        wheel=(ImageView)findViewById(R.id.imagecannon);
        wheel.setOnTouchListener(this);
        drawable=(LevelListDrawable)bShoot.getBackground();
        drawable.setLevel(0);
        bShoot.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        drawable.setLevel(1);
                        view.postDelayed(new Runnable() {
                            public void run() {
                                int level=drawable.getLevel();
                                if (level>0 && level<6) {
                                    drawable.setLevel(level + 1);
                                    view.postDelayed(this, 100);
                                }
                            }
                        }, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        int level=drawable.getLevel();
                        if (level>0) {
                            //qui bisogna sparare
                            Log.d("Sparo","Forza :"+level);
                        }
                        drawable.setLevel(0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (motionEvent.getX()<0 || motionEvent.getX()>=view.getWidth() ||
                            motionEvent.getY()<0 || motionEvent.getY()>=view.getHeight())
                        drawable.setLevel(0);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        //trova il baricentro
        int[] offset=new int[2];
        wheel.getLocationOnScreen(offset);
        final float xc = wheel.getWidth() / 2 +offset[0];
        final float yc = wheel.getHeight() / 2 +offset[1];
        v.getLocationOnScreen(offset);
        final float x = event.getX() + offset[0];
        final float y = event.getY()+ offset[1];

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
               //wheel.clearAnimation(); //secondo colpo torna a 0
                mCurrAngle = Math.toDegrees(Math.atan2(y - yc,x-xc));
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mPrevAngle = mCurrAngle;
                mCurrAngle = Math.toDegrees(Math.atan2( y - yc, x-xc));
                animate(mPrevAngle, mCurrAngle, 0);
                break;
            }
            case MotionEvent.ACTION_UP : {

                mCurrAngle = Math.toDegrees(Math.atan2( y - yc, x-xc));

                if (mCurrAngle >=-125 && mCurrAngle<-55){
                    mCurrAngle=-90;
                    animate(mPrevAngle, mCurrAngle, 0);
                }

                else if ((mCurrAngle>167 && mCurrAngle<180)||(mCurrAngle<-125 && mCurrAngle>-180)){
                    mCurrAngle=-156;
                    animate(mPrevAngle, mCurrAngle, 0);
                }

                else if (mCurrAngle <167 && mCurrAngle>=90){
                    mCurrAngle=138;
                    animate(mPrevAngle, mCurrAngle, 0);
                }

                else if (mCurrAngle <90 && mCurrAngle>=10){
                    mCurrAngle=43;
                    animate(mPrevAngle, mCurrAngle, 0);
                }
                else{
                    mCurrAngle=336;
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
