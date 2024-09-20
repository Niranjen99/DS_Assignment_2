import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FileUtils {
    public static void readDataFile(File dataFile, List<ContentData> contentDataList) {
        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                Map<String, String> data = JsonUtils.parseJSON(line);
                contentDataList.add(new ContentData(
                        JsonUtils.parseJSON(data.get("Data")),
                        Long.parseLong(data.get("LastUpdateTime"))));
            }
        } catch (IOException e) {
            System.out.println("Error occurred while reading 'mainDatafile.data': " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error occurred while processing file content: " + e.getMessage());
        }
    }

    public static void writeListToFile(File file, List<ContentData> list) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new FileWriter(file));
            for (ContentData content : list) {
                bufferedWriter.write(content.toString());
                bufferedWriter.write('\n');
            }
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println("Error occured while writing to file");
            e.printStackTrace();
        }
    }

    public static void writeToTempFile(String fileName, String data) {
        try {
            File file = new File("DataFiles/" + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
            System.out.println("Data written to temp file '" + fileName + "'!");
        } catch (IOException e) {
            System.out.println("Failed while writing to temp file '" + fileName + "'!");
            e.printStackTrace();
        }
    }

    public static void deleteTempFile(String fileName) {
        File file = new File("DataFiles/" + fileName);
        file.delete();
        System.out.println("Deleted temp file '" + fileName + "'");
    }
}
