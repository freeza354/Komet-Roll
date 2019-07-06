package com.rezafaiz.vertical_roll;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements Runnable {

    //New
    private ImageView obs1, obs2;
    private int obs1H, obs1W, obs1Init, obs2H, obs2W, obs2Init;
    private Drawable obs1Left, obs1Right, obs2Left, obs2Right;

    //Ad
    private InterstitialAd mIntAd;

    private TextView scorelabel;
    private TextView startlabel;
    private ImageView box;
    private ImageView orange;
    private ImageView pink;

    //Get Size
    private int frameHeight, frameWidth;
    private int boxSize;
    private int screenWidth;
    private int screenHeight;

    //Position
    private float boxX, boxY;
    private float orangeX, orangeY;
    private float pinkX, pinkY;
    private float obs1X, obs1Y;
    private float obs2X, obs2Y;

    //Score
    private int scoreGame = 0, timeCount;

    //Init Class
    private Handler handler = new Handler();
    private Timer timer = new Timer();

    //Status Check
    private boolean action_flag = false;
    private boolean start_flag = false;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        context.this = context;

        getSupportActionBar().hide();

        mIntAd = new InterstitialAd(this);
        mIntAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mIntAd.loadAd(new AdRequest.Builder().build());

        scorelabel = findViewById(R.id.scoreLabel);
        startlabel = findViewById(R.id.startLabel);

        box = findViewById(R.id.box);
        orange = findViewById(R.id.orange);
        pink = findViewById(R.id.pink);

        obs1 = findViewById(R.id.obs1new);
        obs2 = findViewById(R.id.obs2new);

        obs1Left = getResources().getDrawable(R.drawable.obs1new);
        obs1Right = getResources().getDrawable(R.drawable.obs1new2);

        obs2Left = getResources().getDrawable(R.drawable.obs2new);
        obs2Right = getResources().getDrawable(R.drawable.obs2new2);

        //Get screen size
//        WindowManager wm = getWindowManager();
//        Display display = wm.getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//
//        screenWidth = size.x;
//        screenHeight = size.y;
    }

    public void changePos(){

        //hitCheck();

        //Orange
        orangeY -= 12;

        float orangeCenterX = orangeX + orange.getWidth() / 2;
        float orangeCenterY = orangeY + orange.getWidth() / 2;

        if (checkCollision(orangeCenterX, orangeCenterY)){
            orangeY = -100;
            scoreGame += 10;
        }

        if (orangeY < (frameHeight * -1)){
            orangeY = frameHeight + 100;
            orangeX = (float)Math.floor(Math.random() * (frameWidth - orange.getWidth()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        //Pink
        pinkY -= 15;

        float pinkCenterX = pinkY + orange.getWidth() / 2;
        float pinkCenterY = pinkY + orange.getWidth() / 2;

        if (checkCollision(pinkCenterX, pinkCenterY)){
            orangeY -= 100;
            scoreGame += 10;
        }

        if (pinkY < (frameHeight * -1)){
            pinkY = frameHeight + 100;
            pinkX = (float)Math.floor(Math.random() * (frameWidth - pink.getWidth()));
        }

        orange.setX(orangeX);
        orange.setY(orangeY);

        //Obstacle

        obs1Y -= 9;

        float obsSideX = obs1X + obs1.getWidth();
        float obsSideY = obs1Y - obs1.getWidth();

        if (checkCollision(obs1X, obs1Y)){
            GameOver();
        }

        if (obs1Y < (frameHeight * -1)){
            obs1Y = frameHeight + 100;
            obs1X = 0;
        }

        obs1.setX(obs1X);
        obs1.setY(obs1Y);

        //Obstacle 2

        obs2Y -= 9;

        float obsSideX2 = obs1X + obs1.getWidth();
        float obsSideY2 = obs2Y - obs1.getWidth();

        if (checkCollision(obs2X, obs2Y)){
            GameOver();
        }

        if (obs2Y < (frameHeight * -1)){
            obs2Y = frameHeight + 100;
            obs2X = (frameWidth - obs2.getWidth());
        }

        obs2.setX(obs1X);
        obs2.setY(obs1Y);

        //Move Box
        if (action_flag){
            //Touch
            boxX += 14;
        }else {
            //Release
            boxX -= 14;
        }

        //Check box pos
        if (boxX < 0) {
            boxX = 0;
        }

        if (boxX > frameWidth - boxSize){
            boxX = frameWidth - boxSize;
        }
        box.setX(boxX);

        scorelabel.setText("Score : " + scoreGame);
    }

    public boolean checkCollision(float x, float y){
        if (boxSize + boxY >= y && boxX <= x && x <= boxX + boxSize && y >= 0){
            return true;
        }
        return false;
    }

    public void GameOver(){
        timer.cancel();
//        timer = null;

        mIntAd.show();

        //Show scoreGame...
        Intent intent = new Intent(getApplicationContext(), score.class);
        intent.putExtra("SCORE", scoreGame);
        startActivity(intent);
    }

    public void StartGame(){

        if (!start_flag){
            startlabel.setVisibility(View.INVISIBLE);

            if (frameHeight == 0){

                FrameLayout frame = findViewById(R.id.frame);
                frameHeight = frame.getHeight();
                frameWidth = frame.getWidth();
                boxSize = box.getHeight();
                boxX = box.getX();
                boxY = box.getY();

            }

            box.setX(0f);

            obs2.setY(-3000f);
            obs1.setY(-3000f);
            pink.setY(-3000f);
            orange.setY(-3000f);

            obs1Y = obs1.getY();
            obs2Y = obs2.getY();
            orangeY = orange.getY();
            pinkY = pink.getY();

            box.setVisibility(View.VISIBLE);
            obs1.setVisibility(View.VISIBLE);
            obs2.setVisibility(View.VISIBLE);
            orange.setVisibility(View.VISIBLE);
            pink.setVisibility(View.VISIBLE);

            timeCount = 0;
            scoreGame = 0;
            scorelabel.setText("Score : 0");

            timer = new Timer();
        }

        start_flag = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (start_flag){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }
        },0, 20);

    }

    public boolean onTouchEvent(MotionEvent me){

        if (!start_flag){
            StartGame();
        }else {
            if (me.getAction() == MotionEvent.ACTION_DOWN){
                action_flag = true;
            }else if (me.getAction() == MotionEvent.ACTION_UP){
                action_flag = false;
            }
        }

        return true;
    }

    //Disable return button
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

    @Override
    public void run() {

    }
}
