package com.simonetta.cannone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Simonetta on 22/03/17.
 */

public class SecondActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Button button_play = (Button) findViewById(R.id.button_play);
        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity_second = new Intent(SecondActivity.this, PlayActivity.class);
                startActivity(activity_second);
            }
        });

        Button button_rankings = (Button) findViewById(R.id.button_rankings);
        button_rankings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity_second = new Intent(SecondActivity.this, RankingsActivity.class);
                startActivity(activity_second);
            }
        });

        Button button_instructions = (Button) findViewById(R.id.button_instructions);
        button_instructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity_second = new Intent(SecondActivity.this, InstructionesActivity.class);
                startActivity(activity_second);
            }
        });
    }
}
