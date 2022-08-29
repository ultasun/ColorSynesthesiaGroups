package ultasun.csg.gui;

import java.util.List;
import java.util.Map;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author ultasun
 */
public class ModelForeignKeys implements TableModel {

    private final List<String> orderedKeys;
    private final Map<String, List<String>> tableValues;
    private final int maxRowCount;

    public ModelForeignKeys(List<String> orderedKeys, Map<String, List<String>> tableValues) {
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
