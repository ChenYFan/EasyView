package cn.eurekac.easyview;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.webkit.WebView;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import cn.eurekac.easyview.src.fuckJSON;
import cn.eurekac.easyview.utils.Base64Utils;
import cn.eurekac.easyview.utils.CallBack;

import cn.eurekac.easyview.apis.EVWifiManager;
import cn.eurekac.easyview.utils.LocalStorageUtils;
import cn.eurekac.easyview.utils.PersistantFileStorageUtils;

public class EasyViewAPI {
    private Context contextUI = null;
    private CallBack MainActivityCallBacker = null;
    private WebView webView = null;
    public enum AvailableAPI {
        SYSTEM$GET_SYSTEM_INFO,
        SYSTEM$GO_BACK,
        SYSTEM$GO_RESTART,
        SYSTEM$GO_RESUME,
        SYSTEM$GO_DESTROY,

        LOCAL$GET_GOBACK_ALLOWED,
        LOCAL$SET_GOBACK_ALLOWED,
        LOCAL$GET_GOBACK_HOOKED,
        LOCAL$SET_GOBACK_HOOKED,

//        LOCAL$BACKUP_LOCAL_STORAGE,
//        LOCAL$RESTORE_LOCAL_STORAGE,

        LOCAL$SAVE_FILE,
        LOCAL$LOAD_FILE,

        UTILS$PING_WITH_TIME,
        UTILS$PING,


        WIFI$WIFI_SCAN
    }


    private fuckJSON GET_SYSTEM_INFO() {
        fuckJSON data = new fuckJSON();
        data.set("os", System.getProperty("os.name"));
        data.set("os_version", System.getProperty("os.version"));
        data.set("os_arch", System.getProperty("os.arch"));
        data.set("java_version", System.getProperty("java.version"));
        data.set("java_vendor", System.getProperty("java.vendor"));
        data.set("user_name", System.getProperty("user.name"));
        data.set("user_home", System.getProperty("user.home"));
        data.set("user_dir", System.getProperty("user.dir"));
        data.set("file_separator", System.getProperty("file.separator"));
        data.set("path_separator", System.getProperty("path.separator"));
        data.set("line_separator", System.getProperty("line.separator"));
        return data;
    }

    private fuckJSON PING_WITH_TIME(fuckJSON param) {
        fuckJSON data = new fuckJSON();
        double now = System.currentTimeMillis();
        data.set("time", now);
        data.set("delay", now - Double.parseDouble(param.get("time").toString()));
        return data;
    }

    private fuckJSON PING() {
        fuckJSON data = new fuckJSON();
        data.set("status", "success");
        return data;
    }

    private fuckJSON GO_BACK() throws InvocationTargetException, IllegalAccessException {
        this.MainActivityCallBacker.invoke("SYSTEM$GO_BACK");
        fuckJSON data = new fuckJSON();
        data.set("status", "success");
        return data;
    }


//    private fuckJSON BACKUP_LOCAL_STORAGE() {
//        fuckJSON data = new fuckJSON();
//        PersistantFileStorageUtils pfs = new PersistantFileStorageUtils(this.contextUI);
//        LocalStorageUtils ls = new LocalStorageUtils(this.webView);
//        fuckJSON lsData = ls.getAll();
//        pfs.save("LocalStorage.json", lsData.getString());
//        data.set("status", "success");
//        return data;
//    }
//    private fuckJSON RESTORE_LOCAL_STORAGE() {
//        fuckJSON data = new fuckJSON();
//        PersistantFileStorageUtils pfs = new PersistantFileStorageUtils(this.contextUI);
//        LocalStorageUtils ls = new LocalStorageUtils(this.webView);
//        fuckJSON lsData = new fuckJSON();
//        String pfsRead = pfs.load("LocalStorage.json");
//        if (pfsRead.equals("")) {
//            data.set("status", "failed");
//            return data;
//        }
//        lsData.fromString(pfsRead);
//        ls.setAll(lsData);
//        System.out.println(lsData.getString());
//        data.set("status", "success");
//        System.out.println(data.getString());
//        return data;
//    }


    private fuckJSON LOAD_FILE(fuckJSON param) {
        fuckJSON data = new fuckJSON();
        String fileName = param.get("fileName").toString();
        PersistantFileStorageUtils pfs = new PersistantFileStorageUtils(this.contextUI);
        byte[] fileContentByte = new PersistantFileStorageUtils(this.contextUI).loadAsByte(fileName);
        data.set("status", "success");
        data.set("content", Base64Utils.encode(fileContentByte));
        return data;
    }

    private fuckJSON SAVE_FILE(fuckJSON param) {
        fuckJSON data = new fuckJSON();
        String fileName = param.get("fileName").toString();
        String fileContent = param.get("content").toString();
        byte[] fileContentByte = Base64Utils.decode(fileContent);
        PersistantFileStorageUtils pfs = new PersistantFileStorageUtils(this.contextUI);
        pfs.save(fileName, fileContentByte);
        data.set("status", "success");
        return data;
    }

    private fuckJSON WIFI_SCAN() {
        EVWifiManager wifiManager = new EVWifiManager(this.contextUI);
        wifiManager.checkPermissions();
        wifiManager.startScan();
        List<ScanResult> scanResults = wifiManager.getScannedNetworks();
        fuckJSON data = new fuckJSON();
        data.set("networks", scanResults);
        return data;
    }




    public static Boolean isAPIExist(String api) {
        try {
            AvailableAPI.valueOf(api);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public EasyViewAPI() {}

    public fuckJSON excute(String api, fuckJSON param) {
        switch (AvailableAPI.valueOf(api)) {
            case SYSTEM$GET_SYSTEM_INFO:
                return GET_SYSTEM_INFO();
            case UTILS$PING_WITH_TIME:
                return PING_WITH_TIME(param);
//            case LOCAL$BACKUP_LOCAL_STORAGE:
//                return BACKUP_LOCAL_STORAGE();
//            case LOCAL$RESTORE_LOCAL_STORAGE:
//                return RESTORE_LOCAL_STORAGE();

            case LOCAL$LOAD_FILE:
                return LOAD_FILE(param);
            case LOCAL$SAVE_FILE:
                return SAVE_FILE(param);
            case UTILS$PING:
                return PING();
            case WIFI$WIFI_SCAN:
                return WIFI_SCAN();
            default:
                return null;
        }
    }

    public void setWebview(WebView webView) {
        this.webView = webView;
    }
    public void setContext(Context context) {
        this.contextUI = context;
    }
    public void setCallBacker(CallBack Callbacker) {
        this.MainActivityCallBacker = Callbacker;
    }
}
