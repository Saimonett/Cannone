package com.simonetta.cannone;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlayActivity extends Activity implements OnTouchListener, Runnable{
    private ImageView wheel;
    private double mCurrAngle = 0;
    private double mPrevAngle = 0;
    private Button bShoot;
    LevelListDrawable drawable;
    ImageView bask;

    private int l_primo_t = 51; //lunghezza primo tirante
    private int l_secondo_t = 133; //lunghezza secondo tirante
    private int l_terzo_t = 131;
    private int l_quarto_t = 105;
    private int l_quinto_t = 97;

    private JSONArray primo_t, secondo_t, terzo_t, quarto_t, quinto_t, mezzo_proiettile;

    private int [][]ragnatela=new int[1072][4];// per ogni px abbiamo 4 colonne che identificano i valori di a, rgb
    private Handler mNetworkHandler, mMainHandler;

    private NetworkThread mNetworkThread = null;
    private boolean running=false;


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
                                    view.postDelayed(this, 300);
                                }
                            }
                        }, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        int level=drawable.getLevel();
                        if (level>0) {
                            //qui bisogna sparare
                            Log.d("Sparo","Forza :"+level);
                            try {
                                for(int j=0;j<(l_primo_t/2);j++){
                                    mezzo_proiettile = setProiettile(j);
                                    handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                //QUI bisogna gestire i messaggi che vengono dalla ragnatela

                Toast.makeText(PlayActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
            }
        };
        startHandlerThread();
    }


    @Override
    protected void onResume() {
        super.onResume();
        running=true;
        run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        running=false;
    }

    public void run() {
        //qui eseguite le azioni che volete che vengano fatte continuamente
        Log.d("Tick","tick");
        //il secondo parametro di postDeleayed indica quanto tempo passa (in millisec) tra un'invocazione e la successiva
        if (running) mMainHandler.postDelayed(this,1000);
    }

    public void startHandlerThread() {
        mNetworkThread = new NetworkThread(mMainHandler);
        mNetworkThread.start();
        mNetworkHandler = mNetworkThread.getNetworkHandler();
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


    private void handleNetworkRequest(int what, Object payload, int arg1, int arg2) {
        Message msg = mNetworkHandler.obtainMessage();
        msg.what = what;
        msg.obj = payload;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.sendToTarget();
    }


    JSONArray setProiettile(int j){//muoviamo il proiettile in su
        JSONObject tmp;
        JSONArray mezzo_proiettile = new JSONArray();

        ragnatela[j][0]=0;// coloriamo i primi 3 led verdi
        ragnatela[j][1]=0;
        ragnatela[j][2]=0;
        ragnatela[j][3]=0;

        ragnatela[j+1][0]=100;// coloriamo i primi 3 led verdi
        ragnatela[j+1][1]=0;
        ragnatela[j+1][2]=255;
        ragnatela[j+1][3]=0;

        ragnatela[j+2][0]=255;// coloriamo i primi 3 led verdi
        ragnatela[j+2][1]=0;
        ragnatela[j+2][2]=255;
        ragnatela[j+2][3]=0;

        // anche gli ultimi si accendono

        ragnatela[l_primo_t-j][0]=0;// coloriamo i primi 3 led verdi
        ragnatela[l_primo_t-j][1]=0;
        ragnatela[l_primo_t-j][2]=0;
        ragnatela[l_primo_t-j][3]=0;

        ragnatela[l_primo_t-j-1][0]=100;// coloriamo i primi 3 led verdi
        ragnatela[l_primo_t-j-1][1]=0;
        ragnatela[l_primo_t-j-1][2]=255;
        ragnatela[l_primo_t-j-1][3]=0;

        ragnatela[l_primo_t-j-2][0]=255;// coloriamo i primi 3 led verdi
        ragnatela[l_primo_t-j-2][1]=0;
        ragnatela[l_primo_t-j-2][2]=255;
        ragnatela[l_primo_t-j-2][3]=0;

        try{
            for (int i = 0; i < 1072; i++) {
                tmp = new JSONObject();
                tmp.put("a", ragnatela[i][0]);
                tmp.put("r", ragnatela[i][1]);
                tmp.put("g", ragnatela[i][2]);
                tmp.put("b", ragnatela[i][3]);

                mezzo_proiettile.put(tmp);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return mezzo_proiettile;
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
