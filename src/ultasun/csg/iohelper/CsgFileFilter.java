package ultasun.csg.iohelper;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author ultasun
 */
public class CsgFileFilter extends FileFilter {

    @Override
    public String getDescription() {
        return "Color Synesthesia Groups (*.csg)";
    }

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
