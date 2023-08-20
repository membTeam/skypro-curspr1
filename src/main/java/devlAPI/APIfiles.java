package devlAPI;

import devlAPI.enumType.ETypeFile;
import devlRecord.RecordResProcExt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class APIfiles {
    public static RecordResProcExt readFile(String file, ETypeFile typeFile) {

        RecordResProcExt res;
        String path = "";
        if (typeFile == ETypeFile.initial) {
            path = "anyData/initialData/";
        } else if (typeFile == ETypeFile.sqlcode) {
            path = "anyData/SQLscript/";
        } else { // файл конфигурации
            path = "";
        }

        path += file;
        try {
            byte[] byteFromFile = Files.readAllBytes(Paths.get(path));
            String txtFile = new String(byteFromFile);

            res = new RecordResProcExt(txtFile);
        } catch (IOException ex) {
            res = RecordResProcExt.getExtResultErr (ex.getMessage());
        }
        catch (Exception ex){
            res = RecordResProcExt.getExtResultErr (ex.getMessage());
        }

        return res;
    }
}
