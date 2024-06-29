/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.ColumnsTable;

import interfaces.SortingControlsCellListener;
import java.awt.Color;
import java.awt.Component;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author juanv
 */
public class SortingControlsCellRenderer
        extends AbstractCellEditor
        implements TableCellRenderer, TableCellEditor {

    private SortingControlsPanel renderer;
    private SortingControlsCellListener listener;

    public SortingControlsCellRenderer(SortingControlsCellListener listener) {
        this.renderer = new SortingControlsPanel();
        this.listener = listener;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        int modelIndex = table.convertRowIndexToModel(row);
        boolean isNew = ((ColumnsTableModel) table.getModel()).isNewColumn(modelIndex);
        boolean isDeleted = ((ColumnsTableModel) table.getModel()).isDeletedColumn(modelIndex);

        if (!isSelected) {
            renderer.setBackground(table.getBackground());
            renderer.setForeground(table.getForeground());
            if (isNew) {
                renderer.setBackground(Color.decode("#badc58"));
            }
            if (isDeleted) {
                renderer.setBackground(Color.decode("#ff7979"));
            }
        } else {
            renderer.setBackground(table.getSelectionBackground());
            renderer.setForeground(table.getSelectionForeground());
            if (isNew) {
                renderer.setBackground(Color.decode("#6ab04c"));
            }
            if (isDeleted) {
                renderer.setBackground(Color.decode("#eb4d4b"));
            }
        }

        return renderer;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        int modelIndex = table.convertRowIndexToModel(row);
        boolean isNew = ((ColumnsTableModel) table.getModel()).isNewColumn(modelIndex);
        boolean isDeleted = ((ColumnsTableModel) table.getModel()).isDeletedColumn(modelIndex);

        SortingControlsPanel editor = new SortingControlsPanel();
        editor.initSortingActionsListener(listener, modelIndex);

        if (!isSelected) {
            editor.setBackground(table.getBackground());
            editor.setForeground(table.getForeground());
            if (isNew) {
                editor.setBackground(Color.decode("#badc58"));
            }
            if (isDeleted) {
                editor.setBackground(Color.decode("#ff7979"));
            }
        } else {
            editor.setBackground(table.getSelectionBackground());
            editor.setForeground(table.getSelectionForeground());
            if (isNew) {
                editor.setBackground(Color.decode("#6ab04c"));
            }
            if (isDeleted) {
                editor.setBackground(Color.decode("#eb4d4b"));
            }
        }

        return editor;
    }



}
