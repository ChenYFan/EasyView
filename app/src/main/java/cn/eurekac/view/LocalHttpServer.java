package localserver;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import android.content.res.AssetManager;
import android.util.Log;
public class LocalHttpServer extends NanoHTTPD {
    public AssetManager asset = null;
    private static final String TAG = "LocalHttpServer";
    private static final String INDEX_FILE = "index.html";
    private static final String MIME_DEFAULT_BINARY = "application/octet-stream";

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (uri.endsWith("/")) {
            uri = INDEX_FILE;
        }
        return serveFile(uri);
    }


    private Response serveFile(String uri) {
        try {
            return newChunkedResponse(Response.Status.OK, getMimeTypeForFile(uri), asset.open("www"+uri));
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


    public LocalHttpServer() throws IOException {
        super("127.0.0.1",0);
    }


}
