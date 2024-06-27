/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.ColumnsTable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import model.Column;
import model.Table;
import model.TableRedefinition;

/**
 *
 * @author juanv
 */
public class ColumnsTableModel extends DefaultTableModel {

    private List<Column> columns;
    private TableRedefinition newTable;
    private Set<UUID> deletedColumns;
    private Table originalTable;
    private String[] columnNames = {"", "Order", "Old Name", "New name", "Old Type", "New Type", "", ""};

    public ColumnsTableModel(TableRedefinition table, List<Column> columns, Set<UUID> deletedColumnsIds) {
        super();
        this.newTable = table;
        this.columns = columns;
        this.deletedColumns = deletedColumnsIds;
        this.originalTable = table.getOriginalTable();
    }

    @Override
    public int getRowCount() {
        return columns == null ? 0 : columns.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 1) {
            return Integer.class;
        }
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

        switch (columnIndex) {
            case 0:
            case 6:
            case 7:
                return true;
        }

        return false;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Column newColumn = columns.get(rowIndex);
        UUID originalColId = newTable.getColumnsMappings().get(newColumn.getId());
        Column originalColumn = null;
        if (originalColId != null) {
            originalColumn = originalTable.getColumn(originalColId);
        }
        switch (columnIndex) {
            case 1:
                return newColumn.getOrdinalPosition();
            case 2:
                return originalColumn == null ? "" : originalColumn.getName();
            case 3:
                return newColumn.getName();
            case 4:
                return originalColumn == null ? "" : originalColumn.getType();
            case 5:
                return newColumn.getType();
            case 6:
                return "Modify";
            case 7:
                return isDeletedColumn(rowIndex)? "Undo Delete" : "Delete";
        }

        return "";
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        Column newColumn = columns.get(row);
        UUID originalColId = newTable.getColumnsMappings().get(newColumn.getId());
        Column originalColumn = null;
        if (originalColId != null) {
            originalColumn = originalTable.getColumn(originalColId);
        }
        switch (column) {
            case 1:
                newColumn.setOrdinalPosition((Integer) aValue);
                break;
            case 3:
                newColumn.setName((String) aValue);
                break;
            case 5:
                newColumn.setType((String) aValue);
                break;
            default:
                return;
        }

         fireTableCellUpdated(row, column);
    }

    public boolean isNewColumn(int index) {
        Column column = columns.get(index);
        return newTable.isNewColumn(column.getId());
    }

    public boolean isDeletedColumn(int index) {
        Column column = columns.get(index);
        return deletedColumns.contains(column.getId());
    }

    public Column getColumn(int index) {
        return columns.get(index);
    }

}
