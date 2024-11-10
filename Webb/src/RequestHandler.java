import com.fastcgi.FCGIInterface;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RequestHandler
{
    private final FCGIInterface fcgi;

    public RequestHandler(FCGIInterface fcgi)
    {
        this.fcgi = fcgi;
    }

    public Dot readRequest() throws IOException
    {
        String requestMethod = fcgi.request.params.getProperty("REQUEST_METHOD");
        if (!"POST".equals(requestMethod))
        {
            throw new RuntimeException("Invalid request method: " + requestMethod);
        }
        fcgi.request.inStream.fill();
        int contentLength = fcgi.request.inStream.available();
        ByteBuffer buffer = ByteBuffer.allocate(contentLength);
        int readBytes = fcgi.request.inStream.read(buffer.array(), 0, contentLength);

        byte[] requestBodyRaw = new byte[readBytes];
        buffer.get(requestBodyRaw);
        String requestBody = new String(requestBodyRaw, StandardCharsets.UTF_8);

        JsonObject json;
        try (JsonReader reader = Json.createReader(new StringReader(requestBody)))
        {
            json = reader.readObject();
        }
        catch (Exception e)
        {
            System.err.println("JSON parsing error: " + e.getMessage());
            return null;
        }

        if (!json.containsKey("x") || !json.containsKey("y") || !json.containsKey("r"))
        {
            System.out.println("Missing parameters");
            return null;
        }

        JsonNumber xNumber = json.getJsonNumber("x");
        JsonNumber yNumber = json.getJsonNumber("y");
        JsonNumber rNumber = json.getJsonNumber("r");

        double x = xNumber.doubleValue();
        double y = yNumber.doubleValue();
        double r = rNumber.doubleValue();

        if (x < -3 || x > 5 || y < -r / 2 || y > r) {
            throw new IllegalArgumentException("Неверные данные X или Y");
        }

        return new Dot(x, y, r);
    }
}