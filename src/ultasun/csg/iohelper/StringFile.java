package ultasun.csg.iohelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Give a String, get a String; easy file input/output.
 *
 * @author ultasun
 */
public class StringFile {

    /**
     * Get the text from a String, return an empty string if there is an
     * Exception.
     *
     * @param fileName String path to the file.
     * @return String file contents.
     * @throws java.io.IOException
     */
    public static String read(String fileName) throws IOException {
        File file = new File(fileName);
        return read(file);
    }

    /**
     * Get the text from a String, or return an empty String if there is an
     * Exception.
     *
     * @param file File path to file.
     * @return String the file contents.
     * @throws java.io.IOException
     */
    public static String read(File file) throws IOException {
        String result = "";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String l = "";
        while (l != null) {
            l = reader.readLine();
            if (l != null) {
                result += l + "\n";
            }
        }
        reader.close();
        return result;
    }

    /**
     * Writes a String to a file.
     * @param text String to write.
     * @param file String of file path to write to.
     * @throws IOException 
     */
    public static void write(String text, String file) throws IOException {
        write(text, new File(file));
    }
    
    /**
     * Writes a String to a file.
     * @param text String to write.
     * @param file File of file path to write to.
     * @throws IOException  
     */
    public static void write(String text, File file) throws IOException {
        FileWriter fw = new FileWriter(file);
        fw.append(text);
        fw.close();
    }
}
