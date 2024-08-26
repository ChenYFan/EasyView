package cn.eurekac.easyview;

import localserver.LocalHttpServer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
//Log
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        try {
            LocalHttpServer server = new LocalHttpServer();
            server.asset = getAssets();
            server.start();

            int port = server.getPort();
            Log.d("LocalHttpServer", "Server started at port " + port);
            this.createWebView(port);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void createWebView(int port) {
        final WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });

        String url = "http://127.0.0.1:" + port + "/index.html";
        webView.loadUrl(url);
    }

    /* 接管返回键 */
    // @Override
    // public void onBackPressed() {
    // if (this.webView.canGoBack()) {
    // this.webView.goBack();
    // } else {
    // super.onBackPressed();
    // }
    // }

}