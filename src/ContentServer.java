import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;


public class ContentServer {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 4567;
    private static final String DATA_FILE = "weather_data.txt";
    private LamportClock lamportClock = new LamportClock();
    private static final int POLLING_INTERVAL = 5000;
    private long lastModifiedTime = -1;

    public static void main(String[] args) {
        new ContentServer().start();
    }


    public void start() {

        sendWeatherData();
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.err.println("Error: File " + DATA_FILE + " does not exist.");
            return;
        }
        lastModifiedTime = file.lastModified();

        while (true) {
            try {
                File currentFile = new File(DATA_FILE);
                if (currentFile.lastModified() != lastModifiedTime) {
                    lastModifiedTime = currentFile.lastModified();
                    sendWeatherData();
                }
                TimeUnit.MILLISECONDS.sleep(POLLING_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt(); // Restore interrupted status
                break;
            }
        }
    }
    

    public void sendWeatherData() {
        try (  //Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedReader fileReader = new BufferedReader(new FileReader(DATA_FILE));
             //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
             ) {

            // Read weather data from file and format it as JSON
            StringBuilder jsonData = new StringBuilder();
            jsonData.append("{\n");

            String line;
            boolean first = true;
            while ((line = fileReader.readLine()) != null) {
                if (!first) {
                    jsonData.append(",\n");
                }
                first = false;

                // Format each line as a JSON key-value pair
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    jsonData.append(String.format("\"%s\": \"%s\"", key, value));
                }
            }

            jsonData.append("\n}");
            String jsonString = jsonData.toString();

            // Prepare request headers
            String requestLine = "PUT /weather.json HTTP/1.1";
            String userAgent = "User-Agent: ATOMClient/1/0";
            String contentType = "Content-Type: application/json";
            String contentLength = "Content-Length: " + jsonString.length();
            String Lamportvalue= "Lamport-Time: " +lamportClock.tick();

            // Send PUT request
            System.out.println(requestLine);
            System.out.println(userAgent);
            System.out.println(contentType);
            System.out.println(contentLength);
            System.out.println(Lamportvalue);
            System.out.println(); // Blank line to end headers
            System.out.println(jsonString); // Send JSON data
            System.out.println(); // End of data

            // Reading response and updating Lamport clock
           /*  String responseLine;
            while ((responseLine = in.readLine()) != null) {
                if (responseLine.startsWith("Lamport-Time:")) {
                    int receivedLamportTime = Integer.parseInt(responseLine.split(":")[1].trim());
                    lamportClock.update(receivedLamportTime);
                }
                System.out.println(responseLine);
            } */

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
