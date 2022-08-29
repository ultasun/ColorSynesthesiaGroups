package ultasun.csg.gui;

/**
 * Runnables used by worker threads to update the GUI when they are complete.  
 * Note: these worker threads do this by invoking java.awt.EventQueue.invokeLater()
 * @author ultasun
 */
public class Runnables {
    /**
     * Runnable used by JColorSynesthesiaGroups to restart execution in the
 Swing/AWT thread after a page load has been completed on a separate
 Thread.
     *
     * @author ultasun
     */
    public static class JcsgFillForeignKeyTable implements Runnable {

        private final JColorSynesthesiaGroups jcsg;
        private final Exception e;

        public JcsgFillForeignKeyTable(JColorSynesthesiaGroups jcsg, Exception e) {
            this.jcsg = jcsg;
            this.e = e;
        }

        @Override
        public void run() {
            jcsg.finishFillForeignKeyTable(e);
        }
    }

    /**
     * Runnable used by JColorSynesthesiaGroups to restart execution in the
 Swing/AWT thread after a search has been completed on a separate Thread.
     *
     * @author ultasun
     */
    public static class JcsgExecuteQuery implements Runnable {

        private final JColorSynesthesiaGroups jcsg;

        public JcsgExecuteQuery(JColorSynesthesiaGroups jcsg) {
            this.jcsg = jcsg;
        }

        @Override
        public void run() {
            jcsg.finishExecuteQuery();
        }
    }

}
