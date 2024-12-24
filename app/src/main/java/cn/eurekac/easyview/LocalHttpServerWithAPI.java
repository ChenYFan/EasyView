package cn.eurekac.easyview;

import fi.iki.elonen.NanoWSD;
import cn.eurekac.easyview.utils.fuckJSON;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;


import android.content.Context;
import android.content.res.AssetManager;

public class LocalHttpServerWithAPI extends NanoWSD {
    private Context contextUI = null;
    private static EasyViewAPI easyViewAPI = null;
    public AssetManager asset = null;
    private static final String TAG = "LocalHttpServer";
    private static final String INDEX_FILE = "index.html";
    private static final String API_PREFIX = "/api/v1";
    private static final String MIME_DEFAULT_BINARY = "application/octet-stream";

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
        return new ApiWebSocket(handshakeRequest);
    }

    private static class ApiWebSocket extends WebSocket {

        public ApiWebSocket(IHTTPSession handshakeRequest) {
            super(handshakeRequest);
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
            System.out.println("ReqID: " + ReqID);
            System.out.println("ReqAction: " + ReqAction);
            System.out.println("ReqData: " + ReqData.toString());
            fuckJSON Res = new fuckJSON();

            Res.set("id", ReqID);
            if (!easyViewAPI.isAPIExist(ReqAction)) {
                System.out.println("API " + ReqAction + " not exist");
                Res.set("status", "error");
                Res.set("msg", "API not exist");
                Res.set("data", new Object());
            } else {
                System.out.println("API " + ReqAction + " exist");
                Res.set("status", "success");
                Res.set("msg", "API exist");
                Res.set("data", easyViewAPI.excute(ReqAction, ReqData));
            }
            System.out.println(Res.getString());
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


    public LocalHttpServerWithAPI(Context context) throws IOException {
        super("127.0.0.1", 0);
        contextUI = context;
        easyViewAPI = new EasyViewAPI(contextUI);
    }


}
