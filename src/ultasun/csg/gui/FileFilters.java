package ultasun.csg.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author ultasun
 */
public class FileFilters {

    /**
     * Used to indicate to a JFileChooser that it will show all files in a
     * directory to a user.
     *
     * @author ultasun
     */
    public static class AllFilesFilter extends FileFilter {

        /**
         * Show all files in the directory. Always returns true.
         *
         * @param file Any File. This object is not actually used in the method.
         * @return The boolean value true.
         */
        @Override
        public boolean accept(File file) {
            return true;
        }

        /**
         * The description to show in the drop-down menu.
         *
         * @return The String "All Files".
         */
        @Override
        public String getDescription() {
            return "All files";
        }
    }

    /**
     * Allows a JFileChooser to show only CSG files.
     *
     * @author ultasun
     */
    public static class CsgFileFilter extends FileFilter {

        /**
         * Set the text for the drop-down menu in the JFileChooser to
         *
         * @return The String "Color Synesthesia Groups (*.csg)"
         */
        @Override
        public String getDescription() {
            return "Color Synesthesia Groups (*.csg)";
        }

        /**
         * Accept only .CSG files.
         *
         * @param f A File candidate
         * @return true if the extension is .csg (does not check for valid file
         * syntax.)
         */
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            } else {
                String filename = f.getName().toLowerCase();
                return filename.endsWith(".csg");
            }
        }
    }
}
