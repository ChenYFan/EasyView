package cn.eurekac.easyview;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
//Log
import android.util.Log;

import com.gyf.immersionbar.ImmersionBar;

import cn.eurekac.easyview.src.fuckJSON;
import cn.eurekac.easyview.utils.CallBack;
import cn.eurekac.easyview.utils.PersistantFileStorageUtils;
import cn.eurekac.easyview.utils.LocalStorageUtils;

public class MainActivity extends AppCompatActivity {
    private LocalHttpServerWithAPI localHttpServerWithAPI = null;
    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        ImmersionBar.with(this).init();
        setContentView(R.layout.activity_main);


        try {
            localHttpServerWithAPI = new LocalHttpServerWithAPI();
            localHttpServerWithAPI.setAsset(getAssets());
            localHttpServerWithAPI.setContextUI(this);
            localHttpServerWithAPI.setCallBacker(new CallBack(this, this.getClass().getMethod("SystemCallBacker", String.class)));
            localHttpServerWithAPI.start();
            int port = localHttpServerWithAPI.getPort();
            Log.d("LocalHttpServer", "Server started at port " + port);
            this.createWebView(port);
            localHttpServerWithAPI.setWebview(webView);
            localHttpServerWithAPI.startEasyViewAPI();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("SetJavaScriptEnabled")
    private void createWebView(int port) {
        webView = (WebView) findViewById(R.id.wb);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);

        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient());
//        webView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
//                    webView.goBack();
//                    return true;
//                }
//                return false;
//            }
//        });
        String url = "http://127.0.0.1:" + port + "/index.html";
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (this.localHttpServerWithAPI.isGoBackHooked()) {
            this.localHttpServerWithAPI.TryGoBackWithHook();
        } else if (this.localHttpServerWithAPI.isGoBackAllowed() && this.webView.canGoBack()) {
            this.webView.goBack();
        } else {
            this.RealBack();
        }
    }

    public void RealBack() {
        super.onBackPressed();
    }

    public void RealRestart() {
        super.onRestart();
    }

    public void RealResume() {
        super.onResume();
    }

    public void RealDestroy() {
        super.onDestroy();
    }

    public void SystemCallBacker(String action) {
        switch (action) {
            case "SYSTEM$GO_BACK":
                this.RealBack();//奇怪的是这并不管用
                break;
            case "SYSTEM$GO_RESTART":
                this.RealRestart();
                break;
            case "SYSTEM$GO_RESUME":
                this.RealResume();
                break;
            case "SYSTEM$GO_DESTROY":
                this.RealDestroy();
                System.exit(0);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("onDestroy");
        if (this.localHttpServerWithAPI != null) {
            this.localHttpServerWithAPI.stop();
        }
    }


//    private fuckJSON configObject = null;
//    private void loadConfig(){
//        try {
//            InputStream inputStream = getResources().openRawResource(R.raw.config);
//            int size = inputStream.available();
//            byte[] buffer = new byte[size];
//            inputStream.read(buffer);
//            inputStream.close();
//            String jsonString = new String(buffer, StandardCharsets.UTF_8);
//            configObject = new fuckJSON();
//            configObject.fromString(jsonString);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    public String getConfig(String key){
//        if (configObject == null) loadConfig();
//        return configObject.get(key).toString();
//    }


}