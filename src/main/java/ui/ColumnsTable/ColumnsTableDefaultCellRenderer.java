/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.ColumnsTable;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author juanv
 */
public class ColumnsTableDefaultCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        int modelIndex = table.convertRowIndexToModel(row);
        Boolean isNew = ((ColumnsTableModel) table.getModel()).isNewColumn(modelIndex);
        Boolean isDeleted = ((ColumnsTableModel) table.getModel()).isDeletedColumn(modelIndex);
        setHorizontalAlignment(SwingConstants.CENTER);
        
        if (!isSelected) {
            setBackground(table.getBackground());
            if (isNew) {
                setBackground(Color.decode("#badc58"));
            }
            if (isDeleted) {
                setBackground(Color.decode("#ff7979"));
            }
        } else {
            setBackground(table.getSelectionBackground());
            if (isNew) {
                setBackground(Color.decode("#6ab04c"));
            }
            if (isDeleted) {
                setBackground(Color.decode("#eb4d4b"));
            }
        }

        return this;
    }
}
