package ultasun.csg.iohelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author ultasun
 */
public class GetTextFromFile {
    public static String read(String fileName) {
        File file = new File(fileName);
        return read(file);
    }
    
    public static String read(File file) {
        String result = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String l = "";
            while (l != null) {
                l = reader.readLine();
                if (l != null) {
                    result += l + "\n";
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        return result;        
    }
}
