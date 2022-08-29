package ultasun.csg.iohelper;

import ultasun.csg.lib.ColorSynesthesiaGroups;
import ultasun.csg.lib.PunchTableColorGrouper;
import ultasun.csg.lib.PunchTableRow;
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
import ultasun.csg.gui.TableModels.ColorGroupTableModel;

/**
 * For a given fully processed (run() invoked twice) ColorGrouperQueries object,
 * this class provides static methods to print the results, or show the results in 
 * JTables/JFrames on screen.
 * 
 * NOTE: True headless printing is not currently functional, and may not be possible
 * using the Swing built-in convenience method.  It might not be possible at all due
 * to the JTable always throwing HeadlessException.  
 * @author ultasun
 */
public class CsgPrinter {
    /**
     * Start-up a print job for each color group.  
     * 
     * @param csg The fully processed ColorSynesthesiaGroups object.  
     * @param headless Display minimum (true) or maximum (false) user dialogs?  
     * @throws Exception Any Exception encountered while trying to print.  
     */
    public static void printJobPerColor(ColorSynesthesiaGroups csg, boolean headless) throws Exception {
        synchronized (csg) { // This is not redundant, we need to make sure cg does not change after the next line.
            PunchTableColorGrouper cg = csg.getPtcg();
            boolean showAllData = csg.isShowAllColumns();
            String queryName = csg.getCsgName();
            Map<String, JTable> queue = makeJTables(cg, showAllData);
            int i = 1;
            for (String s : queue.keySet()) {
                JTable thisTable = queue.get(s);
                JFrame jf = getNewTableFrame(thisTable, buildJFrameTitle(queryName, s, thisTable.getRowCount()));
                if(!headless) {
                    JOptionPane.showMessageDialog(jf, "Will now show the print dialog for color group " + s.toUpperCase() + " from query group " + queryName, "Color group " + i + " of " + queue.size() + " from " + queryName, JOptionPane.INFORMATION_MESSAGE);
                    jf.setVisible(true);
                } else {
                    // This is a joke, but I don't know how to make real headless mode work.  
                    jf.setVisible(true);
                }
                MessageFormat header = new MessageFormat(s.toUpperCase() + "  |  " + thisTable.getRowCount() + " rows  |  " + queryName + "  |  " + "Page {0,number,integer}");
                boolean result = thisTable.print(JTable.PrintMode.FIT_WIDTH, null, header, !headless, null, !headless);
                jf.setVisible(false);
                jf.dispose();
                if (!result) {
                    return;
                } else {
                    i++;
                }
            }
        }
    }

    /**
     * Given a fully processed ColorGrouperQueries object, display a JFrame for
     * each color group.  This will show the identical table which would be 
     * printed.  
     * 
     * @param csg A fully processed ColorSynesthesiaGroups object.  
     */
    public static void showJFrames(ColorSynesthesiaGroups csg) {
        synchronized (csg) { // This is not redundant, we need to make sure cg does not change after the next line.
            PunchTableColorGrouper cg = csg.getPtcg();
            boolean showAllData = csg.isShowAllColumns();
            String queryName = csg.getCsgName();
            int i = 10;
            Map<String, JTable> queue = makeJTables(cg, showAllData);
            for (String s : queue.keySet()) {
                JTable thisTable = queue.get(s);
                JFrame jf = getNewTableFrame(thisTable, buildJFrameTitle(queryName, s, thisTable.getRowCount()));
                jf.setLocation(i, i);
                jf.setVisible(true);
                i += 10;
            }
        }
    }
    
    private static String buildJFrameTitle(String queryName, String color, int elements) {
        return queryName + " query, " + color.toUpperCase() + " group, " + elements+ " elements";
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
            JTable newTable = new JTable(new ColorGroupTableModel(thisColorGroup, feedKeys));
            newTable.setAutoCreateRowSorter(true);
            result.put(color, newTable);
        }
        return result;
    }
}
