package ultasun.csg.iohelper;

import ultasun.csg.ColorSynesthesiaGroups;
import ultasun.csg.punchtable.PunchTableColorGrouper;
import ultasun.csg.punchtable.PunchTableRow;
import ultasun.csg.gui.ModelPunchTable;
import java.awt.Component;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * @author ultasun
 */
public class CsgToJtables {
    public static void printJobPerColor(ColorSynesthesiaGroups csg) {
        synchronized (csg) { // This is not redundant, we need to make sure cg does not change after the next line.
            PunchTableColorGrouper cg = csg.getPtcg();
            boolean showAllData = csg.isShowAllColumns();
            String queryName = csg.getCsgName();
            Map<String, JTable> queue = makeJTables(cg, showAllData);
            int i = 1;
            for (String s : queue.keySet()) {
                JTable thisTable = queue.get(s);
                JFrame jf = getNewTableFrame(thisTable, queryName + " query, " + s.toUpperCase() + " group");
                JOptionPane.showMessageDialog(jf, "Will now show the print dialog for color group " + s.toUpperCase() + " from query group " + queryName, "Color group " + i + " of " + queue.size() + " from " + queryName, JOptionPane.INFORMATION_MESSAGE);
                jf.setVisible(true);
                try {
                    MessageFormat header = new MessageFormat(s.toUpperCase() + "    |    " + queryName + "    |    Page {0,number,integer}");
                    boolean result = thisTable.print(JTable.PrintMode.FIT_WIDTH, null, header);
                    jf.setVisible(false);
                    jf.dispose();
                    if (!result) {
                        return;
                    } else {
                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    JOptionPane.showMessageDialog(thisTable, "Failed to print " + s.toUpperCase() + " group from " + queryName + ":\n" + e.getLocalizedMessage(), "Failed to print " + s.toUpperCase() + " group from " + queryName, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void showJFrames(ColorSynesthesiaGroups csg) {
        synchronized (csg) { // This is not redundant, we need to make sure cg does not change after the next line.
            PunchTableColorGrouper cg = csg.getPtcg();
            boolean showAllData = csg.isShowAllColumns();
            String queryName = csg.getCsgName();
            int i = 10;
            Map<String, JTable> queue = makeJTables(cg, showAllData);
            for (String s : queue.keySet()) {
                JFrame jf = getNewTableFrame(queue.get(s), queryName + " query, " + s.toUpperCase() + " group");
                jf.setLocation(i, i);
                jf.setVisible(true);
                i += 10;
            }
        }
    }

    private static JFrame getNewTableFrame(JTable thisTable, String title) {
        thisTable.setFillsViewportHeight(true);
        thisTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JFrame jf = new JFrame(title);
        jf.setSize(800, 400);
        JScrollPane jsp = new JScrollPane(thisTable);
        jsp.setDoubleBuffered(true);
        jf.add(jsp);
        resizeJTableColumns(thisTable);
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        thisTable.setVisible(true);
        return jf;
    }

    /**
     * https://stackoverflow.com/a/17627497
     *
     * @param table
     */
    private static void resizeJTableColumns(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 15; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            if (width > 300) {
                width = 300;
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    private static Map<String, JTable> makeJTables(PunchTableColorGrouper cg, boolean showAllData) {
        Map<String, JTable> result = new HashMap<>();
        for (String color : cg.getColors()) {
            List<PunchTableRow> thisColorGroup = cg.getResultPrintSets().get(color);
            List<String> feedKeys;
            if (showAllData) {
                feedKeys = new ArrayList<>(cg.getOrderedKeyList());
            } else {
                feedKeys = new ArrayList<>(cg.getForeignKeys());
                feedKeys.add(0, cg.getPrimaryKey());
            }
            JTable newTable = new JTable(new ModelPunchTable(thisColorGroup, feedKeys, false));
            newTable.setAutoCreateRowSorter(true);
            result.put(color, newTable);
        }

        return result;
    }
}
