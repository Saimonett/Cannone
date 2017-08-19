package com.simonetta.cannone;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

//public class MainActivity extends AppCompatActivity {
public class MainActivity2 extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH=dm.widthPixels;
        Constants.SCREEN_HEIGHT=dm.heightPixels;

        setContentView(new GamePanel(this));
/*
        Button button_replay = (Button) findViewById(R.id.button_replay);
        button_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(MainActivity2.this, PlayActivity.class);
                startActivity(activity);
                finish();
            }
        });
  */

    }
}
