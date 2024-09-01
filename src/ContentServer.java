import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class ContentServer extends JsonUtils {
    private String serverHost;
    private int serverPort;
    private String dataFile;
    private LamportClock lamportClock = new LamportClock();
    private static final int POLLING_INTERVAL = 5000;
    private long lastModifiedTime = -1;

    public ContentServer(String serverHost, int serverPort, String dataFile) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.dataFile = dataFile;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java ContentServer <serverHost:port> <dataFile>");
            return;
        }

        String[] serverInfo = args[0].split(":");
        if (serverInfo.length != 2) {
            System.err.println("Error: Server information should be in the format <serverHost:port>");
            return;
        }

        String serverHost = serverInfo[0];
        int serverPort;
        try {
            serverPort = Integer.parseInt(serverInfo[1]);
        } catch (NumberFormatException e) {
            System.err.println("Error: Port number must be an integer.");
            return;
        }

        String dataFile = args[1];

        new ContentServer(serverHost, serverPort, dataFile).start();
    }

    public void start() {
        sendWeatherData();

        File file = new File(dataFile);
        if (!file.exists()) {
            System.err.println("Error: File " + dataFile + " does not exist.");
            return;
        }
        lastModifiedTime = file.lastModified();

        while (true) {
            try {
                File currentFile = new File(dataFile);
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
        try (   //Socket socket = new Socket(serverHost, serverPort);
             BufferedReader fileReader = new BufferedReader(new FileReader(dataFile));
             //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
             ) {

            // Read weather data from file and format it as JSON
            String jsonString = parseFileToJson(fileReader);

            

            // Prepare request headers
            String requestLine = "PUT /weather.json HTTP/1.1";
            String userAgent = "User-Agent: ATOMClient/1/0";
            String contentType = "Content-Type: application/json";
            String contentLength = "Content-Length: " + jsonString.length();
            String lamportValue = "Lamport-Time: " + lamportClock.tick();

            // Send PUT request
            System.out.println(requestLine);
            System.out.println(userAgent);
            System.out.println(contentType);
            System.out.println(contentLength);
            System.out.println(lamportValue);
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
