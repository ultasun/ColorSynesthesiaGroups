package ultasun.csg.printing;

import ultasun.csg.punchtable.PunchTableRow;
import java.util.List;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author ultasun
 */
public class ModelPrintTable implements TableModel {
    private final List<String> orderedKeys;
    private final List<PunchTableRow> tableValues;
    private final boolean insertColumnHeaders;
    public ModelPrintTable(List<PunchTableRow> values) {
        this(values, values.get(0).getKeys(), false);
    }
    
    public ModelPrintTable(List<PunchTableRow> values, List<String> printKeys, boolean insertColumnHeaders) {
        this.orderedKeys = printKeys;
        this.tableValues = values;
        this.insertColumnHeaders = insertColumnHeaders;
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
        try {
            if(insertColumnHeaders) {
                if(rowIndex == 0) {
                    return orderedKeys.get(columnIndex);
                } else {
                    return tableValues.get(rowIndex-1).get(orderedKeys.get(columnIndex));
                }
            } else {
            return tableValues.get(rowIndex).get(orderedKeys.get(columnIndex));
            }
        } catch (Exception e) {
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
