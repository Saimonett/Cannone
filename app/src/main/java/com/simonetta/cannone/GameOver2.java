package com.simonetta.cannone;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class GameOver2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over2);

        Intent datiPassati = getIntent();

        int punteggioLivello = datiPassati.getIntExtra("messagePunti2",0);

        TextView tvPunti = (TextView) findViewById(R.id.punteggioTot);
        tvPunti.setText( punteggioLivello + " points" );


        Button fab = (Button) findViewById(R.id.button_tryAgain);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity_second = new Intent(GameOver2.this, PlayActivity.class);
                startActivity(activity_second);
                finish();
            }
        });

    }

}
