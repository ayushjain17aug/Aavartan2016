package com.technocracy.app.aavartan.onlyIntent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.technocracy.app.aavartan.activity.VigyaanActivity;
import com.technocracy.app.aavartan.model.ConnectionDetector;
import com.technocracy.app.aavartan.R;

import net.steamcrafted.loadtoast.LoadToast;

public class ViewPDF extends AppCompatActivity {

    private Toolbar mToolbar;
    WebView mWebView;
    LoadToast lt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String LinkToPDF = VigyaanActivity.LinkPDF;
        final Boolean[] isInternetPresent = {false};
        final ConnectionDetector[] cd = new ConnectionDetector[1];
        lt = new LoadToast(ViewPDF.this);
        lt.setText("Loading Google Doc...");
        Display display = getWindowManager().getDefaultDisplay();
        int height = display.getHeight();
        lt.setTranslationY(height-300);
        lt.setTextColor(Color.YELLOW).setBackgroundColor(0xFF5C6BC0).setProgressColor(Color.RED);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewPDF.this);
        alertDialog.setTitle("Download");
        alertDialog.setMessage("Do you want to Download PDF?");
        alertDialog.setIcon(R.drawable.ic_logo_small);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(LinkToPDF));
                startActivity(intent);
                finish();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                cd[0] = new ConnectionDetector(getApplicationContext());
                isInternetPresent[0] = cd[0].isConnectingToInternet();
                if (isInternetPresent[0]) {
                    // Internet Connection is Present
                    lt.show();
                    mWebView = (WebView) findViewById(R.id.webView);
                    mWebView.getSettings().setJavaScriptEnabled(true);
                    mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + LinkToPDF);
                    mWebView.setWebViewClient(new WebViewClient() {

                        public void onPageFinished(WebView view, String url) {
                            lt.success();
                        }
                    });
                    dialog.cancel();
                } else {
                    // Internet connection is not present
                    Toast.makeText(getApplicationContext(),"Internet Connection not present!",Toast.LENGTH_LONG).show();
                }
            }
        });
        alertDialog.show();
    }
}
