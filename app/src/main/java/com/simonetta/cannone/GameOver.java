package com.simonetta.cannone;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class GameOver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent datiPassati = getIntent();

        int punteggioLivello = datiPassati.getIntExtra("messagePunti",0);

        TextView tvPunti = (TextView) findViewById(R.id.punteggioTot);
        tvPunti.setText( punteggioLivello + " points" );

            Button button_saveSpider = (Button) findViewById(R.id.button_saveSpider);
            button_saveSpider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent activity = new Intent(GameOver.this, MainActivity2.class);
                    startActivity(activity);
                    finish();
                }
            });

    }

}
