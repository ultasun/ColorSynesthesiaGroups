package ultasun.csg.gui;

/**
 *
 * @author ultasun
 */
public class JcsgFillForeignKeyTable implements Runnable {
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
