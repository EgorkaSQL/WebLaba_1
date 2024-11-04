import com.fastcgi.FCGIInterface;


public class ResponseSender {
    private final FCGIInterface fcgi;

    public ResponseSender(FCGIInterface fcgi) {
        this.fcgi = fcgi;
    }

    public void sendResponse(String content) {
        String httpResponse = String.format(
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: %d\r\n\r\n%s",
                content.length(), content);
        System.out.println(httpResponse);
    }

    public void sendErrorResponse(String error) {
        String httpResponse = "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + error.length() + "\r\n" +
                "\r\n" +
                error;
        System.out.println(httpResponse);
    }
}