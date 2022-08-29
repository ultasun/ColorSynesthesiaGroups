package ultasun.csg.launcher;

import ultasun.csg.gui.JColorSynesthesiaGroups;
import java.io.File;

/**
 *
 * @author ultasun
 */
public class GUILauncher {
    public static void main(String[] args) {
        if(args.length > 0) {
            if(args[0].toLowerCase().startsWith("-headless")) {
                TerminalLauncher.main(args);
            } else {
                JColorSynesthesiaGroups.newInstance(new File(args[0]));
            }
        } else {
            JColorSynesthesiaGroups.newInstance();
        }
    }
}
