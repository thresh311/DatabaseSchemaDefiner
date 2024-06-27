/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.ColumnsTable;

import interfaces.DefinitionPanelActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.Column;

/**
 *
 * @author juanv
 */
public class ColumnDefinitionPanel extends javax.swing.JPanel {

    private Column newColumn, originalColumn;
    private DefinitionPanelActionListener listener;

    /**
     * Creates new form ColumnDefinitionPanel
     */
    public ColumnDefinitionPanel() {
        initComponents();
        btnSave.addMouseListener(new ColumnDefinitionPanelMouseAdapter());
        btnCancel.addMouseListener(new ColumnDefinitionPanelMouseAdapter());
    }

    public void setActionListener(DefinitionPanelActionListener l) {
        listener = l;
    }

    public void setNewColumn(Column c) {
        newColumn = c.clone();
        initializeNewColumnFields();
    }
    
    public Column getNewColumn() {
        return newColumn;
    }

    private void initializeNewColumnFields() {
        cbNewAllowNull.setSelected(false);
        taComment.setText("");
        tfNewCollationName.setText("");
        tfNewDefaultValue.setText("");
        tfNewExtra.setText("");
        tfNewName.setText("");
        tfNewType.setText("");
        if (newColumn != null) {
            cbNewAllowNull.setSelected(newColumn.getIsNullable());
            taComment.setText(newColumn.getComment());
            tfNewCollationName.setText(newColumn.getCollationName());
            tfNewDefaultValue.setText(newColumn.getDefaultValue());
            tfNewExtra.setText(newColumn.getExtra());
            tfNewName.setText(newColumn.getName());
            tfNewType.setText(newColumn.getType());
        }

    }

    private void initializeOriginalColumnFields() {
        cbOrAllowNull.setSelected(false);
        tfOrCollationName.setText("");
        tfOrDefaultValue.setText("");
        tfOrExtra.setText("");
        tfOrName.setText("");
        tfOrType.setText("");
        if (originalColumn != null) {
            cbOrAllowNull.setSelected(originalColumn.getIsNullable());
            tfOrCollationName.setText(originalColumn.getCollationName());
            tfOrDefaultValue.setText(originalColumn.getDefaultValue());
            tfOrExtra.setText(originalColumn.getExtra());
            tfOrName.setText(originalColumn.getName());
            tfOrType.setText(originalColumn.getType());
        }

    }

    public void setOriginalColumn(Column c) {
        originalColumn = c;
        initializeOriginalColumnFields();
    }

    class ColumnDefinitionPanelMouseAdapter extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == btnSave) {
                if (listener != null) {
                    newColumn.setIsNullable(cbNewAllowNull.isSelected());                    
                    newColumn.setComment(taComment.getText());
                    newColumn.setCollationName(tfNewCollationName.getText());
                    newColumn.setDefaultValue(tfNewDefaultValue.getText());
                    newColumn.setExtra(tfNewExtra.getText());
                    newColumn.setName(tfNewName.getText());
                    newColumn.setType(tfNewType.getText());
                    listener.onSaveClicked();
                }
            }
            if (e.getSource() == btnCancel) {
                if (listener != null) {
                    listener.onCancelClicked();
                }
            }
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taComment = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        tfOrName = new javax.swing.JTextField();
        tfOrDefaultValue = new javax.swing.JTextField();
        tfOrType = new javax.swing.JTextField();
        tfOrCollationName = new javax.swing.JTextField();
        tfOrExtra = new javax.swing.JTextField();
        tfNewCollationName = new javax.swing.JTextField();
        cbOrAllowNull = new javax.swing.JCheckBox();
        cbNewAllowNull = new javax.swing.JCheckBox();
        tfNewType = new javax.swing.JTextField();
        tfNewDefaultValue = new javax.swing.JTextField();
        tfNewName = new javax.swing.JTextField();
        tfNewExtra = new javax.swing.JTextField();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        jLabel1.setText("Original Name:");

        jLabel2.setText("Original Allow Null:");

        jLabel3.setText("Original Default Value:");

        jLabel4.setText("Original Type:");

        jLabel5.setText("Original Collation Name:");

        jLabel6.setText("Original Extra:");

        jLabel7.setText("Comment:");

        taComment.setColumns(20);
        taComment.setRows(5);
        jScrollPane1.setViewportView(taComment);

        jLabel8.setText("New Name:");

        jLabel9.setText("New Allow Null:");

        jLabel10.setText("New Default Value:");

        jLabel11.setText("New Type:");

        jLabel12.setText("New Collation Name:");

        jLabel13.setText("New Extra:");

        tfOrName.setEditable(false);

        tfOrDefaultValue.setEditable(false);

        tfOrType.setEditable(false);

        tfOrCollationName.setEditable(false);

        tfOrExtra.setEditable(false);

        tfNewCollationName.setEditable(false);

        cbOrAllowNull.setEnabled(false);

        btnCancel.setText("Cancelar");

        btnSave.setText("Guardar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbOrAllowNull, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfOrName))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfOrType, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfOrCollationName, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfOrExtra, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfOrDefaultValue, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)))
                                .addGap(2, 2, 2)))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbNewAllowNull, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfNewType, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfNewCollationName, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfNewDefaultValue, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfNewName, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfNewExtra, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)))
                                .addGap(2, 2, 2))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel11, jLabel12, jLabel13, jLabel8, jLabel9});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCancel, btnSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfOrName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(tfNewName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel9)
                        .addComponent(cbOrAllowNull))
                    .addComponent(cbNewAllowNull))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel10)
                    .addComponent(tfOrDefaultValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfNewDefaultValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tfOrType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(tfNewType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tfOrCollationName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(tfNewCollationName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tfOrExtra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(tfNewExtra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnSave))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox cbNewAllowNull;
    private javax.swing.JCheckBox cbOrAllowNull;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea taComment;
    private javax.swing.JTextField tfNewCollationName;
    private javax.swing.JTextField tfNewDefaultValue;
    private javax.swing.JTextField tfNewExtra;
    private javax.swing.JTextField tfNewName;
    private javax.swing.JTextField tfNewType;
    private javax.swing.JTextField tfOrCollationName;
    private javax.swing.JTextField tfOrDefaultValue;
    private javax.swing.JTextField tfOrExtra;
    private javax.swing.JTextField tfOrName;
    private javax.swing.JTextField tfOrType;
    // End of variables declaration//GEN-END:variables
}
