package com.rezafaiz.vertical_roll;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainMenu extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "MenuActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_LEADERBOARD_UI = 9004;
    private static final int RC_ACHIEVEMENT_UI = 9003;

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private GoogleApiClient apiClient;

    SignInButton signInButton;
    private Object GoogleSignInClient;
    private TextView mStatusText;

    private InterstitialAd mIntAd;

    ImageButton playButton, creditsButton;
    Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        getSupportActionBar().hide();

        signInButton = findViewById(R.id.SignInButton);
        playButton = findViewById(R.id.playButton);
        creditsButton = findViewById(R.id.creditButton);
        exitButton = findViewById(R.id.exitButton);

        signInButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        creditsButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);

        //Handle Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());


        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mIntAd = new InterstitialAd(this);
        mIntAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mIntAd.loadAd(new AdRequest.Builder().build());

        mIntAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mIntAd.loadAd(new AdRequest.Builder().build());
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void startSignInIntent(){
        startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    public void showLeaderBoard(){
        Games.getLeaderboardsClient(this, (GoogleSignIn.getLastSignedInAccount(this)))
                .getLeaderboardIntent(getString(R.string.leaderboard))
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });
    }

    public void showAchievement(){

        if (GoogleSignIn.getLastSignedInAccount(this) == null){
            startSignInIntent();
        }else {
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlock(getString(R.string.achievement_welcome));
        }

        Games.getAchievementsClient(this, (GoogleSignIn.getLastSignedInAccount(this)))
                .getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_ACHIEVEMENT_UI);
                    }
                });
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
            }else {
                String msg = result.getStatus().getStatusMessage();
                if (msg == null || msg.isEmpty()){
                    msg = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this).setMessage(msg)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
//            mStatusText.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));

            findViewById(R.id.SignInButton).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusText.setText(R.string.signed_out);

            findViewById(R.id.SignInButton).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    public void showAd(){
        if (mIntAd.isLoaded()) {
            mIntAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.SignInButton:
                Log.d(TAG, "BUTTON_SIGN_IN");
                Toast.makeText(this, "Signing in...", Toast.LENGTH_SHORT).show();
                startSignInIntent();
                //showAchievement();
                break;

            case R.id.playButton:
                showAd();
                startActivity(new Intent(this, MainActivity.class));
                break;

            case R.id.creditButton:
                showAd();
                startActivity(new Intent(this, CreditActivity.class));
                break;

            case R.id.exitButton:
                finish();
                break;
        }


    }
}
