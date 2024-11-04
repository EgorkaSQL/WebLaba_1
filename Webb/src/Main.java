import com.fastcgi.FCGIInterface;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();
        RequestHandler requestHandler = new RequestHandler(fcgi);
        ResponseSender responseSender = new ResponseSender(fcgi);

        while (fcgi.FCGIaccept() >= 0) {
            try {
                if (fcgi.request == null) {
                    System.err.println("FCGI request is null!");
                    continue;
                }

                Dot dot = requestHandler.readRequest();
                String result = checkCoords(dot.getX(), dot.getY(), dot.getR());
                responseSender.sendResponse(result);
            } catch (Exception e) {
                System.err.println("Exception occurred: " + e.getMessage());
                responseSender.sendErrorResponse("Error processing request");
            }
        }
    }

    private static String checkCoords(double x, double y, double r) {
        boolean isInArea = (x >= 0 && y >= 0 && (r * r >= (x * x + y * y))) ||
                (x <= 0 && y <= 0 && (y >= -x - r / 2));

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String jsonResponse = Json.createObjectBuilder()
                .add("isInArea", isInArea)
                .add("currentTime", now)
                .add("x", x)
                .add("y", y)
                .add("r", r)
                .build().toString();

        return jsonResponse;
    }
}