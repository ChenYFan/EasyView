package localserver;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class LocalHttpServer extends NanoHTTPD {
    //在本地创建一个NANOHTTPD服务器
    //端口随机，提供静态页面展示res/assets目录下的文件
    //自动content-type识别
    //自动路径补全：1.若路径为目录，则自动补全index.html 2.若路径为文件，且不存在该文件，则自动补全.html后缀 
    //监听127.0.0.1，随机端口（若端口占用则尝试重新随机）完成启动后返回端口号

    private static final String TAG = "LocalHttpServer";
    private static final String ASSETS_PATH = "src/main/res/assets";
    private static final String INDEX_FILE = "index.html";
    private static final String MIME_DEFAULT_BINARY = "application/octet-stream";


    public LocalHttpServer() throws IOException {
        super(0);
    }

    // @Override
    // public Response serve(IHTTPSession session) {
    //     String uri = session.getUri();
    //     if (uri.equals("/")) {
    //         uri = "/" + INDEX_FILE;
    //     }
    //     String filename = ASSETS_PATH + uri;
    //     File file = new File(filename);
    //     if (!file.exists()) {
    //         filename = filename + ".html";
    //         file = new File(filename);
    //     }
    //     if (!file.exists()) {
    //         return newFixedLengthResponse(Status.NOT_FOUND, MIME_PLAINTEXT, "EurekacEasyView - File Not Found");
    //     }
    //     try {
    //         FileInputStream fis = new FileInputStream(file);
    //         return newFixedLengthResponse(Status.OK, getMimeTypeForFile(filename), fis, file.length());
    //     } catch (FileNotFoundException e) {
    //         return newFixedLengthResponse(Status.INTERNAL_ERROR, MIME_PLAINTEXT, "EurekacEasyView - Internal Error");
    //     }

    // }


  
    @Override
    public Response serve(IHTTPSession session) {
  //测试：直接返回html页面
        String html = "<html><body><h1>Hello, World!</h1></body></html>";
        return newFixedLengthResponse(html);
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


    public static void main(String[] args) {
        try {
            LocalHttpServer server = new LocalHttpServer();
            server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            System.out.println("Server started, listening on port: " + server.getPort());
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }


}
