package cn.eurekac.easyview.utils;

import android.webkit.ValueCallback;
import android.webkit.WebView;

import cn.eurekac.easyview.src.fuckJSON;

import java.util.concurrent.CountDownLatch;

public class LocalStorageUtils {
    private WebView webView = null;

    public LocalStorageUtils(WebView webView) {
        this.webView = webView;
    }

    public void removeAll() {
        WebViewEvaluateJavascript("localStorage.clear()");
    }

    public fuckJSON getAll() {
        fuckJSON data = new fuckJSON();
        data.fromString(fuckJSON.parseString((String) WebViewEvaluateJavascript("JSON.stringify(localStorage)")));
        return data;
    }

    public void setAll(fuckJSON data) {
        removeAll();
        WebViewEvaluateJavascript("(function(){var data = "
                + URIUtils.encodeURI(data.toString())
                + ";data = JSON.parse(decodeURIComponent(data));for(var key in data){localStorage.setItem(key,data[key]);}})()");
    }
    private String WebViewEvaluateJavascript(String script) {
        final String[] result = {null};
        final CountDownLatch latch = new CountDownLatch(1);
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(script, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        result[0] = value;
                        latch.countDown();
                    }
                });
            }
        });
        try {
            latch.await();
            return result[0].substring(1, result[0].length() - 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
