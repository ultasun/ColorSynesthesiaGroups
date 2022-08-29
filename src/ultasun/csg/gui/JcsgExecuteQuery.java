package ultasun.csg.gui;

/**
 *
 * @author ultasun
 */
public class JcsgExecuteQuery implements Runnable {

    private final JColorSynesthesiaGroups jcsg;

    public JcsgExecuteQuery(JColorSynesthesiaGroups jcsg) {
        this.jcsg = jcsg;
    }

    @Override
    public void run() {
        jcsg.finishExecuteQuery();
    }
}
