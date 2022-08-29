package ultasun.csg.gui;

import ultasun.csg.lib.PunchTableRow;
import java.util.List;
import java.util.Map;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * TableModels used throughout the GUI portions of ColorGrouperQueries.  
 * @author ultasun
 */
public class TableModels {
    /**
     * The TableModel used by CsgToJtables to process a ColorGrouperQueries
     * object into a JTable.
     *
     * @author ultasun
     */
    public static class ColorGroupTableModel implements TableModel {
        private final List<String> orderedKeys;
        private final List<PunchTableRow> tableValues;

        /**
         * Constructs the TableModel using a List of objects which contain
         * multiple columns of information, stored internally as a Map. So an
         * ordered List of keys must also be passed.
         *
         * @param values The list of PunchTableRow objects from a color group.
         * @param printKeys The ordered list of columns to print from each row.
         */
        public ColorGroupTableModel(List<PunchTableRow> values, List<String> printKeys) {
            this.orderedKeys = printKeys;
            this.tableValues = values;
        }

        @Override
        public int getRowCount() {
            return tableValues.size();
        }

        @Override
        public int getColumnCount() {
            return orderedKeys.size();
        }

        @Override
        public String getColumnName(int columnIndex) {
            return orderedKeys.get(columnIndex);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if(rowIndex < tableValues.size()) {
                return tableValues.get(rowIndex).get(orderedKeys.get(columnIndex));
            } else {
                return "";
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
        }
    }

    /**
     * Provides the means to load a list of foreign key names into JTable column
     * headers, and the data for each column.
     *
     * @author ultasun
     */
    public static class ForeignKeysTableModel implements TableModel {

        private final List<String> orderedKeys;
        private final Map<String, List<String>> tableValues;
        private final int maxRowCount;

        /**
         * Provides the means to load a list of foreign key names into JTable
         * column headers, and the data for each column.
         *
         * @param orderedKeys The list of column headers/titles, in the order to
         * show them.
         * @param tableValues A Map whose keys are elements from orderedKeys,
         * and values are lists of data to show in the respective column.
         */
        public ForeignKeysTableModel(List<String> orderedKeys, Map<String, List<String>> tableValues) {
            this.orderedKeys = orderedKeys;
            this.tableValues = tableValues;
            int greatestRowCount = 0;
            for (String k : tableValues.keySet()) {
                if (tableValues.get(k).size() > greatestRowCount) {
                    greatestRowCount = tableValues.get(k).size();
                }
            }
            this.maxRowCount = greatestRowCount;
        }

        @Override
        public int getRowCount() {
            return maxRowCount;
        }

        @Override
        public int getColumnCount() {
            return orderedKeys.size();
        }

        @Override
        public String getColumnName(int columnIndex) {
            return orderedKeys.get(columnIndex);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex < orderedKeys.size()) {
                List<String> columnData = tableValues.get(orderedKeys.get(columnIndex));
                if (rowIndex < columnData.size()) {
                    Object result = columnData.get(rowIndex);
                    if (result != null) {
                        return result;
                    }
                }
            }
            return "";
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
        }
    }
}
