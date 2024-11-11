import com.fastcgi.FCGIInterface;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Main
{
    public static void main(String[] args)
    {
        FCGIInterface fcgi = new FCGIInterface();
        RequestHandler requestHandler = new RequestHandler(fcgi);
        ResponseSender responseSender = new ResponseSender(fcgi);

        while (fcgi.FCGIaccept() >= 0)
        {
            try
            {
                if (fcgi.request == null)
                {
                    System.err.println("Запрос - null");
                    continue;
                }

                Dot dot = requestHandler.readRequest();
                long startTime = System.currentTimeMillis();
                String result = checkCoords(dot.getX(), dot.getY(), dot.getR());
                long execTime = System.currentTimeMillis() - startTime;
                result = addExecTimeToResponse(result, execTime);
                responseSender.sendResponse(result);
            }
            catch (Exception e)
            {
                System.err.println("Exception: " + e.getMessage());
                responseSender.sendErrorResponse("Error processing request");
            }
        }
    }

    private static String checkCoords(double x, double y, double r)
    {
        boolean isInCircle = x >= 0 && y >= 0 && (x * x + y * y <= (r / 2) * (r / 2));
        boolean isInTriangle = x <= 0 && y >= 0 && x >= -r / 2 && y <= r && y <= (2 * x + r);
        boolean isInRectangle = x <= 0 && y <= 0 && x >= -r / 2 && y >= -r;

        boolean isInArea = isInCircle || isInTriangle || isInRectangle;

        String now = ZonedDateTime.now(ZoneId.of("Europe/Moscow")).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String jsonResponse = Json.createObjectBuilder()
                .add("isInArea", isInArea)
                .add("currentTime", now)
                .add("x", x)
                .add("y", y)
                .add("r", r)
                .build().toString();

        return jsonResponse;
    }

    private static String addExecTimeToResponse(String jsonResponse, long execTime)
    {
        JsonObject jsonObject = Json.createReader(new StringReader(jsonResponse)).readObject();
        jsonObject = Json.createObjectBuilder(jsonObject)
                .add("execTime", execTime)
                .build();
        return jsonObject.toString();
    }
}