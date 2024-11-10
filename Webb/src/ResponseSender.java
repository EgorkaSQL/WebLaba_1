import com.fastcgi.FCGIInterface;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResponseSender
{
    private final FCGIInterface fcgi;

    public ResponseSender(FCGIInterface fcgi)
    {
        this.fcgi = fcgi;
    }

    public void sendResponse(String content)
    {
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        String httpResponse = String.format(
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: %d\r\n\r\n",
                contentBytes.length);
        try
        {
            fcgi.request.outStream.write(httpResponse.getBytes(StandardCharsets.UTF_8));
            fcgi.request.outStream.write(contentBytes);
            fcgi.request.outStream.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendErrorResponse(String error)
    {
        String httpResponse = "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + error.length() + "\r\n" +
                "\r\n" +
                error;
        System.out.println(httpResponse);
    }
}