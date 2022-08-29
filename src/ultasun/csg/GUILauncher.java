package ultasun.csg;

import ultasun.csg.gui.JColorSynesthesiaGroups;
import java.io.File;

/**
 *
 * @author ultasun
 */
public class GUILauncher {
    public static void main(String[] args) {
        if(args.length > 1 && "headless".equals(args[0])) {
            new TerminalLauncher(true, new File(args[1])).run();
        } else if (args.length == 1) {
            JColorSynesthesiaGroups.newInstance(new File(args[0]));
        } else {
            JColorSynesthesiaGroups.newInstance();
        }
    }
}
