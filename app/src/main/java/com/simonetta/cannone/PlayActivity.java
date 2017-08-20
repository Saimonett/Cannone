package com.simonetta.cannone;

        import android.animation.ObjectAnimator;
        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Color;
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
    private int l_quinto_t = 133;
    private int l_quarto_t = 133;
    private int l_terzo_t = 105;
    private int l_secondo_t = 98; //lunghezza secondo tirante
    private int pos_cannone = 0; //flag per individuare in che tirante sparare proiettile

    //led di partenza di ogni tirante

    private int led_start_2 = l_primo_t+l_quinto_t+l_quarto_t+l_terzo_t;
    private int led_start_3 = l_primo_t+l_quinto_t+l_quarto_t;
    private int led_start_4 = l_primo_t+l_quinto_t;
    private int led_start_5 = l_primo_t;

    private int led_start_anello1 = l_primo_t+l_quinto_t+l_quarto_t+l_terzo_t+l_secondo_t;
    private int led_start_anello2 = l_primo_t+l_quinto_t+l_quarto_t+l_terzo_t+l_secondo_t+led_start_anello1;
    private int led_start_anello3 = l_primo_t+l_quinto_t+l_quarto_t+l_terzo_t+l_secondo_t+led_start_anello1+led_start_anello2;

    int punteggioTotale = 0;
    int punteggio1 = 0;
    int punteggio2 = 0;
    int punteggio3 = 0;
    int punteggio4 = 0;
    int punteggio5 = 0;

    private JSONArray primo_t, secondo_t, terzo_t, quarto_t, quinto_t, mezzo_proiettile, mezzo_proiettile_2, mezzo_proiettile_3, mezzo_proiettile_4, mezzo_proiettile_5;

    private int [][]ragnatela=new int[1072][4];// per ogni px abbiamo 4 colonne che identificano i valori di a, rgb
    private Handler mNetworkHandler, mMainHandler;

    private NetworkThread mNetworkThread = null;
    private boolean running=false;


    public boolean ramo1shooted = false;
    public boolean ramo2shooted = false;
    public boolean ramo3shooted = false;
    public boolean ramo4shooted = false;
    public boolean ramo5shooted = false;

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

                //RAMO VERTICALE IN ALTO
                if (mCurrAngle >=-125 && mCurrAngle<-55 && ramo1shooted==false){
                    mCurrAngle=-90;
                    animate(mPrevAngle, mCurrAngle, 0);
                    pos_cannone = 1;
                    setDisplayPixels();
                }
                //RAMO IN ALTO A SINISTRA
                else if ((mCurrAngle>167 && mCurrAngle<180 && ramo2shooted==false)||(mCurrAngle<-125 && mCurrAngle>-180 && ramo2shooted==false)){
                    mCurrAngle=-156;
                    animate(mPrevAngle, mCurrAngle, 0);
                    pos_cannone = 2;
                    setDisplayPixels5();
                }

                //RAMO IN BASSO A SINISTRA
                else if (mCurrAngle <167 && mCurrAngle>=90 && ramo3shooted==false){
                    mCurrAngle=138;
                    animate(mPrevAngle, mCurrAngle, 0);
                    pos_cannone = 3;
                    setDisplayPixels3();
                }

                //RAMO IN BASSO A DESTRA
                else if (mCurrAngle <90 && mCurrAngle>=10 && ramo4shooted==false){
                    mCurrAngle=43;
                    animate(mPrevAngle, mCurrAngle, 0);
                    pos_cannone = 4;
                    setDisplayPixels4();
                }

                //RAMO IN ALTO A DESTRA
                //così non rispetta più le posizioni forzate giustamente quindi arrivati ad aver sparato su tutti i tiranti bisogna lanciare il gameover!!!!
                else if (ramo5shooted==false){
                    mCurrAngle=336;
                    animate(mPrevAngle, mCurrAngle, 0);
                    pos_cannone = 5;
                    setDisplayPixels2();
                }
                //????????????????????????????????
                else{
                    // gameOver();
                }
                break;
            }
        }
        return true;
    }

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
                                    view.postDelayed(this, 70);
                                }
                                if (level==6){
                                    drawable.setLevel(1);
                                    run();
                                }
                            }
                        }, 100);
                        break;

                    case MotionEvent.ACTION_UP:

                        int level=drawable.getLevel();


//  ------------------- START CASE LEVEL 1 -----------------------------------------------

                        if (level<=1) {
                            //qui bisogna sparare
                            Log.d("Sparo","Forza :"+level);

                            if(pos_cannone==1 && ramo1shooted!=true)   {
                                try {

                                    for(int j=0;j<(l_primo_t/15);j++){
                                        mezzo_proiettile = setProiettile(j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio1=50;
                                ramo1shooted=true;
                            }
                            else if(pos_cannone==2 && ramo2shooted!=true)   {
                                try {
                                    int led_start_2 = l_primo_t + l_quinto_t + l_quarto_t + l_terzo_t;
                                    for(int j=0;j<(l_secondo_t/15);j++){
                                        mezzo_proiettile = setProiettile2(led_start_2, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio2=50;
                                ramo2shooted=true;
                            }

                            if(pos_cannone==3 && ramo3shooted!=true)   {
                                try {
                                    int led_start_3 = l_primo_t + l_quinto_t + l_quarto_t;
                                    for(int j=0;j<(l_terzo_t/15);j++){
                                        mezzo_proiettile = setProiettile3(led_start_3, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio1=50;
                                ramo3shooted=true;
                            }
                            if(pos_cannone==4 && ramo4shooted!=true)   {
                                try {
                                    int led_start_4 = l_primo_t + l_quinto_t;
                                    for(int j=0;j<(l_quarto_t/15);j++){
                                        mezzo_proiettile = setProiettile4(led_start_4, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio4=50;
                                ramo4shooted=true;
                            }
                            if(pos_cannone==5 && ramo5shooted!=true)   {
                                try {
                                    for(int j=0;j<(l_quinto_t/15);j++){
                                        mezzo_proiettile = setProiettile5(l_primo_t,j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio5=50;
                                ramo5shooted=true;
                            }
                            if(ramo1shooted==true && ramo2shooted==true && ramo3shooted==true && ramo4shooted==true && ramo5shooted==true){
                                gameOver();
                            }
                        }

//  ------------------- FINISH CASE LEVEL 1 -----------------------------------------------


//  ------------------- START CASE LEVEL 2 -----------------------------------------------

                        if (level==2) {
                            //qui bisogna sparare
                            Log.d("Sparo","Forza :"+level);

                            if(pos_cannone==1 && ramo1shooted!=true)   {
                                try {

                                    for(int j=0;j<(l_primo_t/8);j++){
                                        mezzo_proiettile = setProiettile(j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio1=100;
                                ramo1shooted=true;
                            }
                            else if(pos_cannone==2 && ramo2shooted!=true)   {
                                try {
                                    int led_start_2 = l_primo_t + l_quinto_t + l_quarto_t + l_terzo_t;
                                    for(int j=0;j<(l_secondo_t/8);j++){
                                        mezzo_proiettile = setProiettile2(led_start_2, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio2=100;
                                ramo2shooted=true;
                            }

                            if(pos_cannone==3 && ramo3shooted!=true)   {
                                try {
                                    int led_start_3 = l_primo_t + l_quinto_t + l_quarto_t;
                                    for(int j=0;j<(l_terzo_t/8);j++){
                                        mezzo_proiettile = setProiettile3(led_start_3, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio3=100;
                                ramo3shooted=true;
                            }
                            if(pos_cannone==4 && ramo4shooted!=true)   {
                                try {
                                    int led_start_4 = l_primo_t + l_quinto_t;
                                    for(int j=0;j<(l_quarto_t/8);j++){
                                        mezzo_proiettile = setProiettile4(led_start_4, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio4=100;
                                ramo4shooted=true;
                            }
                            if(pos_cannone==5 && ramo5shooted!=true)   {
                                try {
                                    for(int j=0;j<(l_quinto_t/8);j++){
                                        mezzo_proiettile = setProiettile5(l_primo_t,j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio5=100;
                                ramo5shooted=true;
                            }
                            if(ramo1shooted==true && ramo2shooted==true && ramo3shooted==true && ramo4shooted==true && ramo5shooted==true){
                                gameOver();
                            }
                        }

//  ------------------- FINISH CASE LEVEL 2 -----------------------------------------------



//  ------------------- START CASE LEVEL 2 -----------------------------------------------

                        if (level>2 && level<=3) {
                            //qui bisogna sparare
                            Log.d("Sparo","Forza :"+level);

                            if(pos_cannone==1 && ramo1shooted!=true)   {
                                try {

                                    for(int j=0;j<(l_primo_t/4);j++){
                                        mezzo_proiettile = setProiettile(j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio1=150;
                                ramo1shooted=true;
                            }
                            else if(pos_cannone==2 && ramo2shooted!=true)   {
                                try {
                                    int led_start_2 = l_primo_t + l_quinto_t + l_quarto_t + l_terzo_t;
                                    for(int j=0;j<(l_secondo_t/4);j++){
                                        mezzo_proiettile = setProiettile2(led_start_2, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio2=150;
                                ramo2shooted=true;
                            }

                            if(pos_cannone==3 && ramo3shooted!=true)   {
                                try {
                                    int led_start_3 = l_primo_t + l_quinto_t + l_quarto_t;
                                    for(int j=0;j<(l_terzo_t/4);j++){
                                        mezzo_proiettile = setProiettile3(led_start_3, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio3=150;
                                ramo3shooted=true;
                            }
                            if(pos_cannone==4 && ramo4shooted!=true)   {
                                try {
                                    int led_start_4 = l_primo_t + l_quinto_t;
                                    for(int j=0;j<(l_quarto_t/4);j++){
                                        mezzo_proiettile = setProiettile4(led_start_4, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio4=150;
                                ramo4shooted=true;
                            }
                            if(pos_cannone==5 && ramo5shooted!=true)   {
                                try {
                                    for(int j=0;j<(l_quinto_t/4);j++){
                                        mezzo_proiettile = setProiettile5(l_primo_t,j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio5=150;
                                ramo5shooted=true;
                            }
                            if(ramo1shooted==true && ramo2shooted==true && ramo3shooted==true && ramo4shooted==true && ramo5shooted==true){
                                gameOver();
                            }
                        }

//  ------------------- FINISH CASE LEVEL 2 -----------------------------------------------





//  ------------------- START CASE LEVEL 3 -----------------------------------------------

                        if (level>3 && level<=4) {
                            //qui bisogna sparare
                            Log.d("Sparo","Forza :"+level);

                            if(pos_cannone==1 && ramo1shooted!=true)   {
                                try {

                                    for(int j=0;j<(l_primo_t/3);j++){
                                        mezzo_proiettile = setProiettile(j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio1=200;
                                ramo1shooted=true;
                            }
                            else if(pos_cannone==2 && ramo2shooted!=true)   {
                                try {
                                    int led_start_2 = l_primo_t + l_quinto_t + l_quarto_t + l_terzo_t;
                                    for(int j=0;j<(l_secondo_t/3);j++){
                                        mezzo_proiettile = setProiettile2(led_start_2, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio2=200;
                                ramo2shooted=true;
                            }

                            if(pos_cannone==3 && ramo3shooted!=true)   {
                                try {
                                    int led_start_3 = l_primo_t + l_quinto_t + l_quarto_t;
                                    for(int j=0;j<(l_terzo_t/3);j++){
                                        mezzo_proiettile = setProiettile3(led_start_3, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio3=200;
                                ramo3shooted=true;
                            }
                            if(pos_cannone==4 && ramo4shooted!=true)   {
                                try {
                                    int led_start_4 = l_primo_t + l_quinto_t;
                                    for(int j=0;j<(l_quarto_t/3);j++){
                                        mezzo_proiettile = setProiettile4(led_start_4, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio4=200;
                                ramo4shooted=true;
                            }
                            if(pos_cannone==5 && ramo5shooted!=true)   {
                                try {
                                    for(int j=0;j<(l_quinto_t/3);j++){
                                        mezzo_proiettile = setProiettile5(l_primo_t,j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio5=200;
                                ramo5shooted=true;
                            }
                            if(ramo1shooted==true && ramo2shooted==true && ramo3shooted==true && ramo4shooted==true && ramo5shooted==true){
                                gameOver();
                            }
                        }

//  ------------------- FINISH CASE LEVEL 3 -----------------------------------------------





//  ------------------- START CASE LEVEL 4 -----------------------------------------------

                        else if (level>4) {
                            //qui bisogna sparare
                            Log.d("Sparo","Forza :"+level);

                            if(pos_cannone==1 && ramo1shooted!=true)   {
                                try {

                                    for(int j=0;j<(l_primo_t/2);j++){
                                        mezzo_proiettile = setProiettile(j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio1=400;
                                ramo1shooted=true;
                            }
                            else if(pos_cannone==2 && ramo2shooted!=true)   {
                                try {
                                    int led_start_2 = l_primo_t + l_quinto_t + l_quarto_t + l_terzo_t;
                                    for(int j=0;j<(l_secondo_t/2);j++){
                                        mezzo_proiettile = setProiettile2(led_start_2, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio2=400;
                                ramo2shooted=true;
                            }

                            if(pos_cannone==3 && ramo3shooted!=true)   {
                                try {
                                    int led_start_3 = l_primo_t + l_quinto_t + l_quarto_t;
                                    for(int j=0;j<(l_terzo_t/2);j++){
                                        mezzo_proiettile = setProiettile3(led_start_3, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio3=400;
                                ramo3shooted=true;
                            }
                            if(pos_cannone==4 && ramo4shooted!=true)   {
                                try {
                                    int led_start_4 = l_primo_t + l_quinto_t;
                                    for(int j=0;j<(l_quarto_t/2);j++){
                                        mezzo_proiettile = setProiettile4(led_start_4, j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio4=400;
                                ramo4shooted=true;
                            }
                            if(pos_cannone==5 && ramo5shooted!=true)   {
                                try {
                                    for(int j=0;j<(l_quinto_t/2);j++){
                                        mezzo_proiettile = setProiettile5(l_primo_t,j);
                                        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                punteggio5=400;
                                ramo5shooted=true;
                            }
                            if(ramo1shooted==true && ramo2shooted==true && ramo3shooted==true && ramo4shooted==true && ramo5shooted==true){
                                gameOver();
                            }
                        }

//  ------------------- FINISH CASE LEVEL 4 -----------------------------------------------



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
        }
        );
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
        //while(true) {
            run();
        //}
    }

    @Override
    protected void onPause() {
        super.onPause();
        running=false;
    }

    public void run() {
        Log.d("Sparo","GIOVANNI");
        coloraAnelli();

       // mMainHandler.postDelayed(this,5000);
        //  coloraBuco ();

    }

    public void startHandlerThread() {
        mNetworkThread = new NetworkThread(mMainHandler);
        mNetworkThread.start();
        mNetworkHandler = mNetworkThread.getNetworkHandler();
    }

    private void handleNetworkRequest(int what, Object payload, int arg1, int arg2) {
        Message msg = mNetworkHandler.obtainMessage();
        msg.what = what;
        msg.obj = payload;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.sendToTarget();
    }

    JSONArray setProiettile(int j){//il j di setProiettile equivale al count degli altri setProiettile2,3,4,5
        JSONObject tmp;
        JSONArray simonetta = new JSONArray();
/*
        if (j==1){
            if((ragnatela[led_start_anello1][1]!=0) ||(ragnatela[led_start_anello1][2]!=0) || (ragnatela[led_start_anello1][3]!=0)){
                gameOver(j);
            }else {
                punteggio1+=7;
                Log.d("Tick","tick");
            }
        }

        if (j==9){
            if((ragnatela[led_start_anello2][1]!=0) ||(ragnatela[led_start_anello2][2]!=0) || (ragnatela[led_start_anello2][3]!=0)){
                gameOver(j);
            }else {
                punteggio1+=7;
                Log.d("Tick","tick");
            }
        }

        if (j==19){
            if((ragnatela[led_start_anello3][1]!=0) ||(ragnatela[led_start_anello3][2]!=0) || (ragnatela[led_start_anello3][3]!=0)){
                gameOver(j);
            }else {
                punteggio1+=7;
                Log.d("Tick","tick");
            }
        }
*/
       /* ragnatela[j][0]=0;// coloriamo i primi 3 led verdi
        ragnatela[j][1]=0;
        ragnatela[j][2]=0;
        ragnatela[j][3]=0;
*/
        ragnatela[j+1][0]=100;// coloriamo i primi 3 led verdi
        ragnatela[j+1][1]=0;
        ragnatela[j+1][2]=255;
        ragnatela[j+1][3]=0;

        ragnatela[j+2][0]=255;// coloriamo i primi 3 led verdi
        ragnatela[j+2][1]=0;
        ragnatela[j+2][2]=255;
        ragnatela[j+2][3]=0;

        // anche gli ultimi si accendono
/*
        ragnatela[l_primo_t-j-1][0]=0;// coloriamo i primi 3 led verdi
        ragnatela[l_primo_t-j-1][1]=0;
        ragnatela[l_primo_t-j-1][2]=0;
        ragnatela[l_primo_t-j-1][3]=0;
*/
        ragnatela[l_primo_t-j-2][0]=100;// coloriamo i primi 3 led verdi
        ragnatela[l_primo_t-j-2][1]=0;
        ragnatela[l_primo_t-j-2][2]=255;
        ragnatela[l_primo_t-j-2][3]=0;

        ragnatela[l_primo_t-j-3][0]=255;// coloriamo i primi 3 led verdi
        ragnatela[l_primo_t-j-3][1]=0;
        ragnatela[l_primo_t-j-3][2]=255;
        ragnatela[l_primo_t-j-3][3]=0;

        try{
            for (int i = 0; i < 1072; i++) {
                tmp = new JSONObject();
                tmp.put("a", ragnatela[i][0]);
                tmp.put("r", ragnatela[i][1]);
                tmp.put("g", ragnatela[i][2]);
                tmp.put("b", ragnatela[i][3]);

                simonetta.put(tmp);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return simonetta;
    }
    JSONArray setProiettile2(int j, int count){//muoviamo il proiettile in su
        JSONObject tmp2;
        JSONArray mezzo_proiettile_2 = new JSONArray();
/*
        if (count==1){
            if((ragnatela[led_start_anello2-18][1]!=0) || (ragnatela[led_start_anello2-18][2]!=0) || (ragnatela[led_start_anello2-18][3]!=0)){
                gameOver(count);
            }else {
                punteggio2+=7;
                Log.d("Tick","tick");
            }
        }

        if (count==9){
            if((ragnatela[led_start_anello3-33][1]!=0) ||(ragnatela[led_start_anello3-33][2]!=0) || (ragnatela[led_start_anello3-33][3]!=0)){
                gameOver(count);
            }else {
                punteggio2+=7;
                Log.d("Tick","tick");
            }
        }

        if (count==19){
            if((ragnatela[552-51][1]!=0) ||(ragnatela[552-51][2]!=0) || (ragnatela[552-51][3]!=0)){
                gameOver(count);
            }else {
                punteggio2+=7;
                Log.d("Tick","tick");
            }
        }
*/
/*
        ragnatela[j+count][0]=0;// coloriamo i primi 3 led verdi
        ragnatela[j+count][1]=0;
        ragnatela[j+count][2]=0;
        ragnatela[j+count][3]=0;
*/
        ragnatela[j+count+1][0]=100;// coloriamo i primi 3 led verdi
        ragnatela[j+count+1][1]=0;
        ragnatela[j+count+1][2]=255;
        ragnatela[j+count+1][3]=0;

        ragnatela[j+count+2][0]=255;// coloriamo i primi 3 led verdi
        ragnatela[j+count+2][1]=0;
        ragnatela[j+count+2][2]=255;
        ragnatela[j+count+2][3]=0;

        // anche gli ultimi si accendono
/*
        ragnatela[j+l_secondo_t-count+2][0]=0;// coloriamo i primi 3 led verdi
        ragnatela[j+l_secondo_t-count+2][1]=0;
        ragnatela[j+l_secondo_t-count+2][2]=0;
        ragnatela[j+l_secondo_t-count+2][3]=0;
*/
        ragnatela[j+l_secondo_t-count+1][0]=100;// coloriamo i primi 3 led verdi
        ragnatela[j+l_secondo_t-count+1][1]=0;
        ragnatela[j+l_secondo_t-count+1][2]=255;
        ragnatela[j+l_secondo_t-count+1][3]=0;

        ragnatela[j+l_secondo_t-count][0]=255;// coloriamo i primi 3 led verdi
        ragnatela[j+l_secondo_t-count][1]=0;
        ragnatela[j+l_secondo_t-count][2]=255;
        ragnatela[j+l_secondo_t-count][3]=0;

        try{
            for (int i = 0; i < 1072; i++) {
                tmp2 = new JSONObject();
                tmp2.put("a", ragnatela[i][0]);
                tmp2.put("r", ragnatela[i][1]);
                tmp2.put("g", ragnatela[i][2]);
                tmp2.put("b", ragnatela[i][3]);

                mezzo_proiettile_2.put(tmp2);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return mezzo_proiettile_2;
    }
    JSONArray setProiettile3(int j, int count){//muoviamo il proiettile in su
        JSONObject tmp3;
        JSONArray mezzo_proiettile_3 = new JSONArray();
/*
        if (count==1){
            if((ragnatela[led_start_anello2+79][1]!=0) ||(ragnatela[led_start_anello2+79][2]!=0) || (ragnatela[led_start_anello2+79][3]!=0)){
                gameOver(count);
            }else {
                punteggio3+=7;
                Log.d("Tick","tick");
            }
        }

        if (count==9){
            if((ragnatela[led_start_anello3+179][1]!=0) ||(ragnatela[led_start_anello3+179][2]!=0) || (ragnatela[led_start_anello3+179][3]!=0)){
                gameOver(count);
            }else {
                punteggio3+=7;
                Log.d("Tick","tick");
            }
        }

        if (count==19){
            if((ragnatela[led_start_anello3+322][1]!=0) ||(ragnatela[led_start_anello3+322][2]!=0) || (ragnatela[led_start_anello3+322][3]!=0)){
                gameOver(count);
            }else {
                punteggio3+=7;
                Log.d("Tick","tick");
            }
        }
*//*
        ragnatela[j+count][0]=0;// coloriamo i primi 3 led verdi
        ragnatela[j+count][1]=0;
        ragnatela[j+count][2]=0;
        ragnatela[j+count][3]=0;
*/
        ragnatela[j+count+1][0]=100;// coloriamo i primi 3 led verdi
        ragnatela[j+count+1][1]=0;
        ragnatela[j+count+1][2]=255;
        ragnatela[j+count+1][3]=0;

        ragnatela[j+count+2][0]=255;// coloriamo i primi 3 led verdi
        ragnatela[j+count+2][1]=0;
        ragnatela[j+count+2][2]=255;
        ragnatela[j+count+2][3]=0;

        // anche gli ultimi si accendono
/*
        ragnatela[j+l_terzo_t-count+1][0]=0;// coloriamo i primi 3 led verdi
        ragnatela[j+l_terzo_t-count+1][1]=0;
        ragnatela[j+l_terzo_t-count+1][2]=0;
        ragnatela[j+l_terzo_t-count+1][3]=0;
*/
        ragnatela[j+l_terzo_t-count][0]=100;// coloriamo i primi 3 led verdi
        ragnatela[j+l_terzo_t-count][1]=0;
        ragnatela[j+l_terzo_t-count][2]=255;
        ragnatela[j+l_terzo_t-count][3]=0;

        ragnatela[j+l_terzo_t-count-1][0]=255;// coloriamo i primi 3 led verdi
        ragnatela[j+l_terzo_t-count-1][1]=0;
        ragnatela[j+l_terzo_t-count-1][2]=255;
        ragnatela[j+l_terzo_t-count-1][3]=0;

        try{
            for (int i = 0; i < 1072; i++) {
                tmp3 = new JSONObject();
                tmp3.put("a", ragnatela[i][0]);
                tmp3.put("r", ragnatela[i][1]);
                tmp3.put("g", ragnatela[i][2]);
                tmp3.put("b", ragnatela[i][3]);

                mezzo_proiettile_3.put(tmp3);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return mezzo_proiettile_3;
    }
    JSONArray setProiettile4(int j, int count){//muoviamo il proiettile in su
        JSONObject tmp4;
        JSONArray mezzo_proiettile_4 = new JSONArray();
/*
        if (count==1){
            if((ragnatela[led_start_anello2+46][1]!=0) ||(ragnatela[led_start_anello2+46][2]!=0) || (ragnatela[led_start_anello2+46][3]!=0)){
                gameOver(count);
            }else {
                punteggio4+=7;
                Log.d("Tick","tick");
            }
        }

        if (count==9){
            if((ragnatela[led_start_anello3-33][1]!=0) ||(ragnatela[led_start_anello3-33][2]!=0) || (ragnatela[led_start_anello3-33][3]!=0)){
                gameOver(count);
            }else {
                punteggio4+=7;
                Log.d("Tick","tick");
            }
        }

        if (count==19){
            if((ragnatela[led_start_anello3+112][1]!=0) ||(ragnatela[led_start_anello3+112][2]!=0) || (ragnatela[led_start_anello3+112][3]!=0)){
                gameOver(count);
            }else {
                punteggio4+=7;
                Log.d("Tick","tick");
            }
        }
*/
/*
        ragnatela[j+count][0]=0;// coloriamo i primi 3 led verdi
        ragnatela[j+count][1]=0;
        ragnatela[j+count][2]=0;
        ragnatela[j+count][3]=0;
*/
        ragnatela[j+count+1][0]=100;// coloriamo i primi 3 led verdi
        ragnatela[j+count+1][1]=0;
        ragnatela[j+count+1][2]=255;
        ragnatela[j+count+1][3]=0;

        ragnatela[j+count+2][0]=255;// coloriamo i primi 3 led verdi
        ragnatela[j+count+2][1]=0;
        ragnatela[j+count+2][2]=255;
        ragnatela[j+count+2][3]=0;

        // anche gli ultimi si accendono
/*
        ragnatela[j+l_quarto_t-count+1][0]=0;// coloriamo i primi 3 led verdi
        ragnatela[j+l_quarto_t-count+1][1]=0;
        ragnatela[j+l_quarto_t-count+1][2]=0;
        ragnatela[j+l_quarto_t-count+1][3]=0;
*/
        ragnatela[j+l_quarto_t-count][0]=100;// coloriamo i primi 3 led verdi
        ragnatela[j+l_quarto_t-count][1]=0;
        ragnatela[j+l_quarto_t-count][2]=255;
        ragnatela[j+l_quarto_t-count][3]=0;

        ragnatela[j+l_quarto_t-count-1][0]=255;// coloriamo i primi 3 led verdi
        ragnatela[j+l_quarto_t-count-1][1]=0;
        ragnatela[j+l_quarto_t-count-1][2]=255;
        ragnatela[j+l_quarto_t-count-1][3]=0;

        try{
            for (int i = 0; i < 1072; i++) {
                tmp4 = new JSONObject();
                tmp4.put("a", ragnatela[i][0]);
                tmp4.put("r", ragnatela[i][1]);
                tmp4.put("g", ragnatela[i][2]);
                tmp4.put("b", ragnatela[i][3]);

                mezzo_proiettile_4.put(tmp4);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return mezzo_proiettile_4;
    }
    JSONArray setProiettile5(int j,int count){//muoviamo il proiettile in su
        JSONObject tmp5;
        JSONArray mezzo_proiettile_5 = new JSONArray();

/*
        if (count==1){
            if((ragnatela[led_start_anello1+16][1]!=0) ||(ragnatela[led_start_anello1+16][2]!=0) || (ragnatela[led_start_anello1+16][3]!=0)){
                gameOver(count);
            }else {
                punteggio5+=7;
                Log.d("Tick","tick");
            }
        }

        if (count==9){
            if((ragnatela[led_start_anello2+35][1]!=0) ||(ragnatela[led_start_anello2+35][2]!=0) || (ragnatela[led_start_anello2+35][3]!=0)){
                gameOver(count);
            }else {
                punteggio5+=7;
                Log.d("Tick","tick");
            }
        }

        if (count==19){
            if((ragnatela[led_start_anello3+5][1]!=0) ||(ragnatela[led_start_anello3+5][2]!=0) || (ragnatela[led_start_anello3+5][3]!=0)){
                gameOver(count);
            }else {
                punteggio5+=7;
                Log.d("Tick","tick");
            }
        }
*/

/*
        ragnatela[j+count][0]=0;// coloriamo i primi 3 led verdi
        ragnatela[j+count][1]=0;
        ragnatela[j+count][2]=0;
        ragnatela[j+count][3]=0;
*/
        ragnatela[j+count+1][0]=100;// coloriamo i primi 3 led verdi
        ragnatela[j+count+1][1]=0;
        ragnatela[j+count+1][2]=255;
        ragnatela[j+count+1][3]=0;

        ragnatela[j+count+2][0]=255;// coloriamo i primi 3 led verdi
        ragnatela[j+count+2][1]=0;
        ragnatela[j+count+2][2]=255;
        ragnatela[j+count+2][3]=0;

        // anche gli ultimi si accendono
/*
        ragnatela[j+l_quinto_t-count+1][0]=0;// coloriamo i primi 3 led verdi
        ragnatela[j+l_quinto_t-count+1][1]=0;
        ragnatela[j+l_quinto_t-count+1][2]=0;
        ragnatela[j+l_quinto_t-count+1][3]=0;
*/
        ragnatela[j+l_quinto_t-count][0]=100;// coloriamo i primi 3 led verdi
        ragnatela[j+l_quinto_t-count][1]=0;
        ragnatela[j+l_quinto_t-count][2]=255;
        ragnatela[j+l_quinto_t-count][3]=0;

        ragnatela[j+l_quinto_t-count-1][0]=255;// coloriamo i primi 3 led verdi
        ragnatela[j+l_quinto_t-count-1][1]=0;
        ragnatela[j+l_quinto_t-count-1][2]=255;
        ragnatela[j+l_quinto_t-count-1][3]=0;

        try{
            for (int i = 0; i < 1072; i++) {
                tmp5 = new JSONObject();
                tmp5.put("a", ragnatela[i][0]);
                tmp5.put("r", ragnatela[i][1]);
                tmp5.put("g", ragnatela[i][2]);
                tmp5.put("b", ragnatela[i][3]);

                mezzo_proiettile_5.put(tmp5);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return mezzo_proiettile_5;
    }
    JSONArray setProiettileTot(int j){//il j di setProiettile equivale al count degli altri setProiettile2,3,4,5
        JSONObject tmp;
        JSONArray simonetta = new JSONArray();

        ragnatela[j+1][0]=255;// coloriamo i primi 3 led verdi
        ragnatela[j+1][1]=0;
        ragnatela[j+1][2]=0;
        ragnatela[j+1][3]=0;


        try{
            for (int i = 0; i < 1072; i++) {
                tmp = new JSONObject();
                tmp.put("a", ragnatela[i][0]);
                tmp.put("r", ragnatela[i][1]);
                tmp.put("g", ragnatela[i][2]);
                tmp.put("b", ragnatela[i][3]);

                simonetta.put(tmp);
            }
        } catch (JSONException exception) {
            // No errors expected here
        }
        return simonetta;
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

    private void gameOver(){
       // Log.d("TickGameOver","tickGameOver");
        punteggioTotale=punteggio1+punteggio2+punteggio3+punteggio4+punteggio5;
        if (punteggioTotale>1500){
            Intent activity_game = new Intent(PlayActivity.this, GameOver.class);
            activity_game.putExtra("messagePunti", punteggioTotale);
            startActivity(activity_game);
            finish();
        }

        if (punteggioTotale<=1500) {
            Intent activity_game2 = new Intent(PlayActivity.this, GameOver2.class);
            activity_game2.putExtra("messagePunti2", punteggioTotale);
            startActivity(activity_game2);
            finish();
        }

        if (punteggioTotale>300 && punteggioTotale<800){
            coloraAnello1();
        }
        if (punteggioTotale>=800 && punteggioTotale<1500){
            coloraAnello2();
        }
        if (punteggioTotale>=1500){
            coloraAnello3();
        }


       // punteggioTotale=0;


    }

    void setDisplayPixels(){
        try{
            JSONArray pixels_array = new JSONArray();
            Bitmap tempBMP = BitmapFactory.decodeResource(getResources(),R.drawable.cannone_pos1);
            tempBMP=Bitmap.createScaledBitmap(tempBMP,32,32,false);
            int[]pixels = new int [tempBMP.getHeight()*tempBMP.getWidth()];
            tempBMP.getPixels(pixels, 0, tempBMP.getWidth(), 0, 0, tempBMP.getWidth(), tempBMP.getHeight());
            for (int i =0; i<pixels.length; i++){
                int pixel = pixels[i];

                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);

                JSONObject tmp = new JSONObject();
                tmp.put("r",redValue);
                tmp.put("g",greenValue);
                tmp.put("b",blueValue);
                tmp.put("a",0);

                pixels_array.put(tmp);
            }

            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS,pixels_array,0,0);
        }catch (JSONException e){

        }
    }
    void setDisplayPixels2(){
        try{
            JSONArray pixels_array = new JSONArray();
            Bitmap tempBMP = BitmapFactory.decodeResource(getResources(),R.drawable.cannone_pos2);
            tempBMP=Bitmap.createScaledBitmap(tempBMP,32,32,false);
            int[]pixels = new int [tempBMP.getHeight()*tempBMP.getWidth()];
            tempBMP.getPixels(pixels, 0, tempBMP.getWidth(), 0, 0, tempBMP.getWidth(), tempBMP.getHeight());
            for (int i =0; i<pixels.length; i++){
                int pixel = pixels[i];

                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);

                JSONObject tmp = new JSONObject();
                tmp.put("r",redValue);
                tmp.put("g",greenValue);
                tmp.put("b",blueValue);
                tmp.put("a",0);

                pixels_array.put(tmp);
            }

            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS,pixels_array,0,0);
        }catch (JSONException e){

        }
    }
    void setDisplayPixels3(){
        try{
            JSONArray pixels_array = new JSONArray();
            Bitmap tempBMP = BitmapFactory.decodeResource(getResources(),R.drawable.cannone_pos3);
            tempBMP=Bitmap.createScaledBitmap(tempBMP,32,32,false);
            int[]pixels = new int [tempBMP.getHeight()*tempBMP.getWidth()];
            tempBMP.getPixels(pixels, 0, tempBMP.getWidth(), 0, 0, tempBMP.getWidth(), tempBMP.getHeight());
            for (int i =0; i<pixels.length; i++){
                int pixel = pixels[i];

                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);

                JSONObject tmp = new JSONObject();
                tmp.put("r",redValue);
                tmp.put("g",greenValue);
                tmp.put("b",blueValue);
                tmp.put("a",0);

                pixels_array.put(tmp);
            }

            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS,pixels_array,0,0);
        }catch (JSONException e){

        }
    }
    void setDisplayPixels4(){
        try{
            JSONArray pixels_array = new JSONArray();
            Bitmap tempBMP = BitmapFactory.decodeResource(getResources(),R.drawable.cannone_pos4);
            tempBMP=Bitmap.createScaledBitmap(tempBMP,32,32,false);
            int[]pixels = new int [tempBMP.getHeight()*tempBMP.getWidth()];
            tempBMP.getPixels(pixels, 0, tempBMP.getWidth(), 0, 0, tempBMP.getWidth(), tempBMP.getHeight());
            for (int i =0; i<pixels.length; i++){
                int pixel = pixels[i];

                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);

                JSONObject tmp = new JSONObject();
                tmp.put("r",redValue);
                tmp.put("g",greenValue);
                tmp.put("b",blueValue);
                tmp.put("a",0);

                pixels_array.put(tmp);
            }

            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS,pixels_array,0,0);
        }catch (JSONException e){

        }
    }
    void setDisplayPixels5(){
        try{
            JSONArray pixels_array = new JSONArray();
            Bitmap tempBMP = BitmapFactory.decodeResource(getResources(),R.drawable.cannone_pos5);
            tempBMP=Bitmap.createScaledBitmap(tempBMP,32,32,false);
            int[]pixels = new int [tempBMP.getHeight()*tempBMP.getWidth()];
            tempBMP.getPixels(pixels, 0, tempBMP.getWidth(), 0, 0, tempBMP.getWidth(), tempBMP.getHeight());
            for (int i =0; i<pixels.length; i++){
                int pixel = pixels[i];

                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);

                JSONObject tmp = new JSONObject();
                tmp.put("r",redValue);
                tmp.put("g",greenValue);
                tmp.put("b",blueValue);
                tmp.put("a",0);

                pixels_array.put(tmp);
            }

            handleNetworkRequest(NetworkThread.SET_DISPLAY_PIXELS,pixels_array,0,0);
        }catch (JSONException e){

        }
    }


    void coloraAnelli(){

        JSONArray tmp_anello = new JSONArray();
        JSONObject tmp_a1 = new JSONObject();

//faccio gli anelli con il buco
        for (int i = 0; i <1072; i++) {
            if (i>=0 && i<522){
                ragnatela[i][0]=255;
                ragnatela[i][1]=0;
                ragnatela[i][2]=0;
                ragnatela[i][3]=0;
            } else if(i>=522 && i<612) {
                ragnatela[i][0]=255;
                ragnatela[i][1]=0;
                ragnatela[i][2]=0;
                ragnatela[i][3]=0;
            }else if (i>=613 && i<=790){
                ragnatela[i][0]=255;
                ragnatela[i][1]=0;
                ragnatela[i][2]=0;
                ragnatela[i][3]=0;
            } else if (i>790 && i<1072){
                ragnatela[i][0]=255;
                ragnatela[i][1]=0;
                ragnatela[i][2]=0;
                ragnatela[i][3]=0;
              }
            try{
                tmp_a1.put("a", ragnatela[i][0]);
                tmp_a1.put("r", ragnatela[i][1]);
                tmp_a1.put("g", ragnatela[i][2]);
                tmp_a1.put("b", ragnatela[i][3]);

                tmp_anello.put(tmp_a1);
            } catch (JSONException exception) {
                // No errors expected here
            }
        }

        for(int j=0;j<(l_primo_t/2);j++){
            mezzo_proiettile = setProiettileTot(j);
        }
        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);

        //il secondo parametro di postDeleayed indica quanto tempo passa (in millisec) tra un'invocazione e la successiva

    }
    void coloraAnello1(){

        JSONArray tmp_anello = new JSONArray();
        JSONObject tmp_a1 = new JSONObject();

//faccio gli anelli con il buco
        for (int i = 0; i <1072; i++) {
            if (i>=0 && i<522){
                ragnatela[i][0]=255;
                ragnatela[i][1]=0;
                ragnatela[i][2]=0;
                ragnatela[i][3]=0;
            } else if(i>=522 && i<612) {
                ragnatela[i][0]=255;
                ragnatela[i][1]=255;
                ragnatela[i][2]=0;
                ragnatela[i][3]=0;
            }else if (i>=613 && i<=790){
                ragnatela[i][0]=255;
                ragnatela[i][1]=0;
                ragnatela[i][2]=0;
                ragnatela[i][3]=0;
            } else if (i>790 && i<1072){
                ragnatela[i][0]=255;
                ragnatela[i][1]=0;
                ragnatela[i][2]=0;
                ragnatela[i][3]=0;
            }
            try{
                tmp_a1.put("a", ragnatela[i][0]);
                tmp_a1.put("r", ragnatela[i][1]);
                tmp_a1.put("g", ragnatela[i][2]);
                tmp_a1.put("b", ragnatela[i][3]);

                tmp_anello.put(tmp_a1);
            } catch (JSONException exception) {
                // No errors expected here
            }
        }

        for(int j=0;j<(l_primo_t/2);j++){
            mezzo_proiettile = setProiettileTot(j);
        }
        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);

        //il secondo parametro di postDeleayed indica quanto tempo passa (in millisec) tra un'invocazione e la successiva

    }
    void coloraAnello2(){

        JSONArray tmp_anello = new JSONArray();
        JSONObject tmp_a1 = new JSONObject();

//faccio gli anelli con il buco
        for (int i = 0; i <1072; i++) {
            if (i>=0 && i<522){
                ragnatela[i][0]=255;
                ragnatela[i][1]=0;
                ragnatela[i][2]=0;
                ragnatela[i][3]=0;
            } else if(i>=522 && i<612) {
                ragnatela[i][0]=255;
                ragnatela[i][1]=255;
                ragnatela[i][2]=0;
                ragnatela[i][3]=0;
            }else if (i>=613 && i<=790){
                ragnatela[i][0]=255;
                ragnatela[i][1]=0;
                ragnatela[i][2]=255;
                ragnatela[i][3]=0;
            } else if (i>790 && i<1072){
                ragnatela[i][0]=255;
                ragnatela[i][1]=0;
                ragnatela[i][2]=0;
                ragnatela[i][3]=0;
            }
            try{
                tmp_a1.put("a", ragnatela[i][0]);
                tmp_a1.put("r", ragnatela[i][1]);
                tmp_a1.put("g", ragnatela[i][2]);
                tmp_a1.put("b", ragnatela[i][3]);

                tmp_anello.put(tmp_a1);
            } catch (JSONException exception) {
                // No errors expected here
            }
        }

        for(int j=0;j<(l_primo_t/2);j++){
            mezzo_proiettile = setProiettileTot(j);
        }
        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);

        //il secondo parametro di postDeleayed indica quanto tempo passa (in millisec) tra un'invocazione e la successiva

    }
    void coloraAnello3(){

        JSONArray tmp_anello = new JSONArray();
        JSONObject tmp_a1 = new JSONObject();

//faccio gli anelli con il buco
        for (int i = 0; i <1072; i++) {
            if (i>=0 && i<522){
                ragnatela[i][0]=255;
                ragnatela[i][1]=0;
                ragnatela[i][2]=0;
                ragnatela[i][3]=0;
            } else if(i>=522 && i<612) {
                ragnatela[i][0]=255;
                ragnatela[i][1]=255;
                ragnatela[i][2]=0;
                ragnatela[i][3]=0;
            }else if (i>=613 && i<=790){
                ragnatela[i][0]=255;
                ragnatela[i][1]=0;
                ragnatela[i][2]=255;
                ragnatela[i][3]=0;
            } else if (i>790 && i<1072){
                ragnatela[i][0]=255;
                ragnatela[i][1]=0;
                ragnatela[i][2]=0;
                ragnatela[i][3]=255;
            }
            try{
                tmp_a1.put("a", ragnatela[i][0]);
                tmp_a1.put("r", ragnatela[i][1]);
                tmp_a1.put("g", ragnatela[i][2]);
                tmp_a1.put("b", ragnatela[i][3]);

                tmp_anello.put(tmp_a1);
            } catch (JSONException exception) {
                // No errors expected here
            }
        }

        for(int j=0;j<(l_primo_t/2);j++){
            mezzo_proiettile = setProiettileTot(j);
        }
        handleNetworkRequest(NetworkThread.SET_PIXELS, mezzo_proiettile, 0, 0);

        //il secondo parametro di postDeleayed indica quanto tempo passa (in millisec) tra un'invocazione e la successiva

    }
}

