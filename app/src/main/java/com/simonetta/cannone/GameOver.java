package com.simonetta.cannone;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        tvPunti.setText( punteggioLivello + "punti" );

        FloatingActionButton play_again = (FloatingActionButton) findViewById(R.id.play_again);
        play_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity_second = new Intent(GameOver.this, PlayActivity.class);
                startActivity(activity_second);
            }
        });
    }

}
