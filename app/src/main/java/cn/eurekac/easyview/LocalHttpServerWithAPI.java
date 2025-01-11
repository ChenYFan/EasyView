package cn.eurekac.easyview;

import fi.iki.elonen.NanoWSD;
import cn.eurekac.easyview.src.fuckJSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.content.Context;
import android.content.res.AssetManager;
import android.webkit.WebView;

import cn.eurekac.easyview.utils.CallBack;

public class LocalHttpServerWithAPI extends NanoWSD {
    private static CallBack MainActivityCallBacker = null;
    private Context contextUI = null;
    public EasyViewAPI easyViewAPI = null;
    private AssetManager asset = null;
    private WebView webView = null;
    private Boolean goBackHooked = false;
    private Boolean goBackAllowed = false;
    private ApiWebSocket apiWebSocket = null;
    private static final String TAG = "LocalHttpServer";
    private static final String INDEX_FILE = "index.html";
    private static final String API_PREFIX = "/api/v1";
    private static final String MIME_DEFAULT_BINARY = "application/octet-stream";
    private HashMap<String, WebSocket> webSockets = new HashMap<String, WebSocket>();

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        System.out.println("Serving " + uri);
        if (uri.startsWith(API_PREFIX)) {
            System.out.println("API Request Catched,redirect to WebSocket");
            return super.serve(session);
        } else {
            if (uri.endsWith("/")) {
                uri = INDEX_FILE;
            }
            return serveFile(uri);
        }
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshakeRequest) {
        System.out.println("WebSocket opened: " + handshakeRequest.getUri());
        apiWebSocket = new ApiWebSocket(handshakeRequest);
        apiWebSocket.setEasyViewAPI(easyViewAPI);
        webSockets.put(handshakeRequest.getUri(), apiWebSocket);
        return apiWebSocket;
    }


    private static class ApiWebSocket extends WebSocket {
        private EasyViewAPI easyViewAPI = null;

        public ApiWebSocket(IHTTPSession handshakeRequest) {
            super(handshakeRequest);
        }

        public void setEasyViewAPI(EasyViewAPI easyViewAPI) {
            this.easyViewAPI = easyViewAPI;
        }
        public EasyViewAPI getEasyViewAPI() {
            return this.easyViewAPI;
        }

        @Override
        protected void onMessage(WebSocketFrame message) {
            String msg = message.getTextPayload();
            fuckJSON Req = new fuckJSON();
            Req.fromString(msg);
            String ReqID = Req.get("id").toString();
            String ReqAction = Req.get("action").toString();
            fuckJSON ReqData = (fuckJSON) Req.get("data");
            if (ReqData == null) {
                ReqData = new fuckJSON();
            }
//            System.out.println("ReqID: " + ReqID);
//            System.out.println("ReqAction: " + ReqAction);
//            System.out.println("ReqData: " + ReqData.toString());
            fuckJSON Res = new fuckJSON();
            Res.set("id", ReqID);
            if (this.easyViewAPI.isAPIExist(ReqAction)) {
                Res.set("status", "success");
                Res.set("msg", "API exist");
                Res.set("data", this.easyViewAPI.excute(ReqAction, ReqData));
            } else {
                System.out.println("API " + ReqAction + " not exist");
                Res.set("status", "error");
                Res.set("msg", "API not exist");
                Res.set("data", new HashMap<String,Object>());
            }
            try {
                send(Res.getString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPong(WebSocketFrame pong) {
            System.out.println("Pong received: " + pong.getTextPayload());
        }

        @Override
        protected void onOpen() {
            System.out.println("WebSocket opened.");
        }

        @Override
        protected void onClose(WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
            System.out.println("WebSocket closed: ");
        }

        @Override
        protected void onException(IOException exception) {
            System.out.println("WebSocket exception: " + exception);
        }
    }


    private Response serveFile(String uri) {
        try {
            return newChunkedResponse(Response.Status.OK, getMimeTypeForFile(uri), asset.open("www" + uri));
        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Error 404, file not found.");
        }
    }


    public int getPort() {
        return super.getListeningPort();
    }

    private static final Map<String, String> MIME_TYPES = new HashMap<String, String>() {{
        put("htm", "text/html");
        put("html", "text/html");
        put("xml", "text/xml");
        put("css", "text/css");
        put("txt", "text/plain");
        put("asc", "text/plain");
        put("gif", "image/gif");
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("png", "image/png");
        put("mp3", "audio/mpeg");
        put("m3u", "audio/mpeg-url");
        put("mp4", "video/mp4");
        put("ogv", "video/ogg");
        put("flv", "video/x-flv");
        put("mov", "video/quicktime");
        put("js", "application/javascript");
        put("pdf", "application/pdf");
        put("doc", "application/msword");
        put("ogg", "application/x-ogg");
    }};

    public static String getMimeTypeForFile(String filename) {
        String mime = MIME_DEFAULT_BINARY;
        int dot = filename.lastIndexOf('.');
        if (dot >= 0) {
            String extension = filename.substring(dot + 1).toLowerCase();
            mime = MIME_TYPES.get(extension);
        }
        return mime;
    }

    public void setAsset(AssetManager asset) {
        this.asset = asset;
    }

    public Boolean isGoBackHooked() {
        return goBackHooked;
    }

    public Boolean isGoBackAllowed() {
        return goBackAllowed;
    }

    public void setGoBackHooked(Boolean goBackHooked) {
        this.goBackHooked = goBackHooked;
    }

    public void setGoBackAllowed(Boolean goBackAllowed) {
        this.goBackAllowed = goBackAllowed;
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void clearDeadWebSocket() {
        List<String> deadKeys = new ArrayList<String>();
        for (String key : webSockets.keySet()) {
            WebSocket ws = webSockets.get(key);
            if (!ws.isOpen()) {
                deadKeys.add(key);
            }
        }
        for (String key : deadKeys) {
            webSockets.remove(key);
        }
    }

    public void sendWebSocketMessage(WebSocket ws, fuckJSON message) {
        executorService.submit(() -> {
            try {
                ws.send(message.getString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void broadCastWebSocket(fuckJSON data) {
        if (webSockets.isEmpty()) {
            System.out.println("No WebSocket connected");
            return;
        }
        for (WebSocket ws : webSockets.values()) {
            System.out.println(data.getString());
            if (ws.isOpen()) sendWebSocketMessage(ws, data);
        }
        clearDeadWebSocket();
    }


    public void TryGoBackWithHook() {
        fuckJSON data = new fuckJSON();
        data.set("action", "SYSTEM$GO_BACK");
        data.set("id", "BROADCAST");
        fuckJSON innnerdata = new fuckJSON();
        innnerdata.set("time", System.currentTimeMillis());
        data.set("data", innnerdata);
        broadCastWebSocket(data);
    }

    public LocalHttpServerWithAPI() throws IOException {
        super("127.0.0.1", 0);
    }

    public void setWebview(WebView webView) {
        this.webView = webView;
    }
    public void setContextUI(Context context) {
        this.contextUI = context;
    }
    public void setCallBacker(CallBack Callbacker) {
        this.MainActivityCallBacker = Callbacker;
    }
    public void startEasyViewAPI() {
        System.out.println("EasyView API Start");
        easyViewAPI = new EasyViewAPI();
        easyViewAPI.setContext(this.contextUI);
        easyViewAPI.setCallBacker(this.MainActivityCallBacker);
        easyViewAPI.setWebview(this.webView);
    }


}
