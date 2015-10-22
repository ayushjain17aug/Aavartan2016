package com.technocracy.app.aavartan.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.technocracy.app.aavartan.R;
import com.technocracy.app.aavartan.adapter.ServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


public class SplashScreen extends AppCompatActivity {

    /*private static final float ROTATE_FROM = 0.0f;
    private static final float ROTATE_TO = -10.0f * 360.0f;*/

    Random r = new Random();
    private int SPLASH_TIME_OUT = (r.nextInt(3000) + 1500);


    private static String version_url = "http://aavartan.org/aavartanapp2015/version.php";
    private static final String TAG_VERSION = "version";
    private static final String TAG_PLAY_URL = "play_url";

    private final Integer AppVersion = 1;
    private Integer version;
    private String play_url;
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        /* To color the Navigation Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }*/

        //to make the activity full scree
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

        /*ImageView loader = (ImageView) findViewById(R.id.imgSplash);

        RotateAnimation r; // = new RotateAnimation(ROTATE_FROM, ROTATE_TO);
        r = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        r.setDuration((long) 9000);
        r.setRepeatCount(0);
        loader.startAnimation(r);*/

        if(!isNetworkConnected()){
            new Handler().postDelayed(new Runnable() {
                //
                //Showing splash screen with a timer. This will be useful when you
                // want to show case your app logo / company
                //
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                    startActivity(i);
                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }else {
            try{
                new CheckVersion().execute();
            }catch (Exception e){

            }
        }
    }


    private class CheckVersion extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(version_url, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            Log.w("URL to be fetch : ", jsonStr);
            if (jsonStr != null) {
                    try {
                    JSONObject jsonVer = new JSONObject(jsonStr);
                    version = jsonVer.getInt("version");
                    JSONObject jsonUrl = new JSONObject(jsonStr);
                    play_url = jsonUrl.getString("play_url");
                    Log.w("Value of Version :",version.toString());
                    Log.w("Value of Fetched URL",play_url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if(AppVersion==version){
                Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                startActivity(i);
                // close this activity
                finish();
            }
            else{
                alertDialog.setTitle("Update Available!");
                alertDialog.setMessage("Download the latest version of app?");
                alertDialog.setIcon(R.drawable.avartan_logo_background_100);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(play_url)));
                        finish();
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                        startActivity(i);
                        dialog.cancel();
                        finish();
                    }
                });
                alertDialog.show();
            }
        }
    }

    boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo()!=null);
    }
}