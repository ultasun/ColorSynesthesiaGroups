package ultasun.csg.iohelper;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author ultasun
 */
public class AllFilesFilter extends FileFilter {

    @Override
    public boolean accept(File file) {
        return true;
    }

    @Override
    public String getDescription() {
        return "All files";
    }
    
}
