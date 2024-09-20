import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JsonUtils {
    public static String parseFileToJson(String file) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        StringBuilder jsonData = new StringBuilder();
        jsonData.append("{\n");
        String line;
        boolean first = true;
        while ((line = fileReader.readLine()) != null) {
            if (!first) {
                jsonData.append(",\n");
            }
            first = false;
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                jsonData.append(String.format("\"%s\": \"%s\"", key, value));
            }
        }
        jsonData.append("\n}");
        fileReader.close();
        return jsonData.toString();
    }

    public static Map<String, String> parseJSON(String jsonString) throws Exception {
        jsonString = jsonString.trim();
        if (!jsonString.startsWith("{") || !jsonString.endsWith("}")) {
            throw new Exception("Invalid JSON format: Missing curly braces.");
        }
        Map<String, String> jsonMap = new HashMap<>();
        jsonString = jsonString.substring(1, jsonString.length() - 1).trim();
        String[] keyValuePairs = jsonString.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":(?=([^\"]*\"[^\"]*\")*[^\"]*$)", 2);
            if (keyValue.length != 2) {
                throw new Exception("Invalid JSON format: Malformed key-value pair.");
            }
            String key = parseString(keyValue[0].trim());
            String value = parseString(keyValue[1].trim());
            jsonMap.put(key, value);
        }
        return jsonMap;
    }

    private static String parseString(String value) throws Exception {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        throw new Exception("Invalid JSON format: Expected string value.");
    }

    public static String stringifyJson(Map<String, String> map) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            jsonBuilder.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                jsonBuilder.append("\"").append(entry.getValue()).append("\"");
            } else {
                jsonBuilder.append(entry.getValue());
            }
            jsonBuilder.append(",");
        }
        jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
}