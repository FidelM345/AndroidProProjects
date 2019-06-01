package com.example.thebeast.smartadmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


public class FirstAidWeb_View extends AppCompatActivity {
    ProgressBar progressBar;
    ImageView imageView;
    WebView webView;
    Toolbar toolbar;
    String weburl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstaidweb_view);

        progressBar =findViewById(R.id.myProgressbar);
        imageView =findViewById(R.id.myImageview);
        webView =findViewById(R.id.myWebview);
       // toolbar = findViewById(R.id.toolbar);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);



        webView.getSettings().setJavaScriptEnabled(true);//enables the javascript

        webView.setWebViewClient(new WebViewClient());// allows the web pages to open in the app
        //and not google chrome.

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                progressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setSupportActionBar(toolbar);
               // getSupportActionBar().setTitle(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);

                imageView.setImageBitmap(icon);
            }
        });


        getIcomingExtra();
    }


    public void getIcomingExtra() {

        if (getIntent().hasExtra("weburl")) {
            weburl = getIntent().getStringExtra("weburl");
            webView.loadUrl("http://"+weburl);




        } else {

            Toast.makeText(this, "No content", Toast.LENGTH_LONG).show();

        }

    }



    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){

            webView.goBack();

        }else {

            Intent intent=new Intent(getApplicationContext(),Web_View.class);
            startActivity(intent);
        }
    }
}
