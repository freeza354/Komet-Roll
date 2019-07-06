package com.rezafaiz.vertical_roll;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;

public class score extends AppCompatActivity {

    TextView scoreLabel;
    TextView highScoreLabel;

    Button showLederboard, mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        getSupportActionBar().hide();

        scoreLabel = findViewById(R.id.scoreLabel);
        highScoreLabel = findViewById(R.id.highScoreLabel);

        showLederboard = findViewById(R.id.leadButton);

        showLederboard.setVisibility(View.INVISIBLE);

        int scoreGame = getIntent().getIntExtra("SCORE",0);
        scoreLabel.setText(scoreGame + "");

        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        int highScoreGame = settings.getInt("HIGH_SCORE", 0);

        if (scoreGame > highScoreGame){
            highScoreLabel.setText("High Score : " + scoreGame);

            //Save high score
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", scoreGame);
            editor.commit();
        }else {
            highScoreLabel.setText("High Score : " + highScoreGame);
        }

        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            showLederboard.setVisibility(View.VISIBLE);
        }
    }

    public void tryAgain(View view){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void backToMenu(View view){
        startActivity(new Intent(getApplicationContext(), MainMenu.class));
    }

    public void toLeaderboard(){

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

}
