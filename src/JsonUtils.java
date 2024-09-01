import java.io.BufferedReader;
import java.io.IOException;

class JsonUtils {
    public static String parseFileToJson(BufferedReader fileReader) throws IOException {
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
        return jsonData.toString();
    }

}