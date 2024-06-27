/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.ColumnsTable;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;
import java.util.function.Function;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author juanv
 */
public class ButtonCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private Function<Integer, Boolean> isEnabledSupplier;
    private ButtonCellListener listener;
    private JButton renderer;
    private JButton editor;
    private String editorValue;

    public ButtonCellRenderer(JTable table,
            Function<Integer, Boolean> isEnabledSupplier,
            ButtonCellListener listener) {
        this.isEnabledSupplier = isEnabledSupplier;
        this.listener = listener;
        this.renderer = new JButton();
        this.editor = new JButton();
        editor.addActionListener(e -> {
            int row = table.convertRowIndexToModel(table.getEditingRow());
            fireEditingStopped();
            listener.onClick(row);
        });
    }

    @Override
    public Object getCellEditorValue() {
        return editorValue;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        int modelIndex = table.convertRowIndexToModel(row);        
        
        renderer.setText((String) value);
        renderer.setEnabled(isEnabledSupplier.apply(modelIndex));

        return renderer;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        int modelIndex = table.convertRowIndexToModel(row);
        
        editorValue = (String) value;

        editor.setText(editorValue);
        editor.setEnabled(isEnabledSupplier.apply(modelIndex));

        return editor;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return false; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return true; //To change body of generated methods, choose Tools | Templates.
    }

}
