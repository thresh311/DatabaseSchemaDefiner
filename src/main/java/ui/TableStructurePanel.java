/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import helpers.UIHelper;
import interfaces.DefinitionPanelActionListener;
import interfaces.ReportMessageListener;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import model.Column;
import model.Index;
import model.Table;
import model.TableRedefinition;
import ui.ColumnsTable.ButtonCellRenderer;
import interfaces.ButtonCellListener;
import interfaces.SortingControlsCellListener;
import ui.ColumnsTable.ColumnDefinitionPanel;
import ui.ColumnsTable.ColumnsTableDefaultCellRenderer;
import ui.ColumnsTable.ColumnsTableModel;
import ui.ColumnsTable.SortingControlsCellRenderer;
import ui.ColumnsTable.SortingControlsPanel;

/**
 *
 * @author juanv
 */
public class TableStructurePanel extends javax.swing.JPanel {

    private TableRedefinition newTableStructure;
    private List<Column> columns;
    private List<Index> indexes;
    private Set<UUID> deletedColumnsIds;
    private Table originalTableStructure;
    private ReportMessageListener messageListener;
    private ColumnDefinitionPanel columnDefinitionPanel;
    private boolean isColumnEditing;

    /**
     * Creates new form TableReviewPanel
     */
    public TableStructurePanel() {
        initComponents();
        jScrollPane3.getVerticalScrollBar().setUnitIncrement(16);
        columnDefinitionPanel = new ColumnDefinitionPanel();
        columnDefinitionPanel.setActionListener(new ColumnDefinitionPanelActionListener());
        btnAddColumn.addMouseListener(new TableStructurePanelMouseAdapter());
    }

    private void initializeTableFields() {

        tfOriginalTableName.setText(originalTableStructure.getName());
        tfOriginalCharset.setText(originalTableStructure.getCollationName());
        tfOriginalEngine.setText(originalTableStructure.getEngine());
        tfOriginalSchema.setText(originalTableStructure.getSchema());

        tfNewTableName.setText(newTableStructure.getName());
        tfNewCharset.setText(newTableStructure.getCollationName());
        tfNewEngine.setText(newTableStructure.getEngine());

        taTableComment.setText(newTableStructure.getComment());

        initializeColumnsTable();

    }

    private void initializeColumnsTable() {

        UIHelper.swapComponents(columnsTableTopPanel, columnDefinitionPanel, generalColumnActionsPanel);

        isColumnEditing = false;

        columnsTable.setModel(new ColumnsTableModel(newTableStructure, columns, deletedColumnsIds));

        TableRowSorter<ColumnsTableModel> sorter
                = new TableRowSorter<>((ColumnsTableModel) columnsTable.getModel());
        List<RowSorter.SortKey> sortKeys
                = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.setSortable(0, false);
        sorter.setSortable(1, false);
        sorter.setSortable(2, false);
        sorter.setSortable(3, false);
        sorter.setSortable(4, false);
        sorter.setSortable(5, false);
        sorter.setSortable(6, false);
        sorter.setSortable(7, false);

        columnsTable.setRowSorter(sorter);

        SortingControlsCellRenderer sortingControls = new SortingControlsCellRenderer(
                new SortingControlsCellListener() {

            @Override
            public void onSendToTopClicked(int row) {
                insertColumnInPosition(row, 1);
                ((AbstractTableModel) columnsTable.getModel()).fireTableDataChanged();
                columnsTable.setRowSelectionInterval(columnsTable.convertRowIndexToView(row), columnsTable.convertRowIndexToView(row));
            }

            @Override
            public void onSendToBottomClicked(int row) {
                insertColumnInPosition(row, columns.size());
                ((AbstractTableModel) columnsTable.getModel()).fireTableDataChanged();
                columnsTable.setRowSelectionInterval(columnsTable.convertRowIndexToView(row), columnsTable.convertRowIndexToView(row));
            }

            @Override
            public void onSendUpClicked(int row) {
                ColumnsTableModel tableModel = (ColumnsTableModel) columnsTable.getModel();
                Column c = tableModel.getColumn(row);
                if (c.getOrdinalPosition() <= 1) {
                    return;
                }
                insertColumnInPosition(row, c.getOrdinalPosition() - 1);
                ((AbstractTableModel) columnsTable.getModel()).fireTableDataChanged();
                columnsTable.setRowSelectionInterval(columnsTable.convertRowIndexToView(row), columnsTable.convertRowIndexToView(row));
            }

            @Override
            public void onSendDownClicked(int row) {
                ColumnsTableModel tableModel = (ColumnsTableModel) columnsTable.getModel();
                Column c = tableModel.getColumn(row);
                if (c.getOrdinalPosition() >= columns.size()) {
                    return;
                }
                insertColumnInPosition(row, c.getOrdinalPosition() + 1);
                ((AbstractTableModel) columnsTable.getModel()).fireTableDataChanged();
                columnsTable.setRowSelectionInterval(columnsTable.convertRowIndexToView(row), columnsTable.convertRowIndexToView(row));
            }

        }
        );
        columnsTable.getColumnModel().getColumn(0).setCellRenderer(sortingControls);
        columnsTable.getColumnModel().getColumn(0).setCellEditor(sortingControls);

        ButtonCellRenderer btnRenderer = new ButtonCellRenderer(
                columnsTable,
                row -> !isColumnEditing,
                row -> {
                    ColumnsTableModel tableModel = (ColumnsTableModel) columnsTable.getModel();
                    Column c = tableModel.getColumn(row);

                    showColumnEditingPanel(c);

                });
        columnsTable.getColumnModel().getColumn(6).setCellRenderer(btnRenderer);
        columnsTable.getColumnModel().getColumn(6).setCellEditor(btnRenderer);

        btnRenderer = new ButtonCellRenderer(
                columnsTable,
                row -> !isColumnEditing,
                row -> {
                    ColumnsTableModel tableModel = (ColumnsTableModel) columnsTable.getModel();
                    Column c = tableModel.getColumn(row);

                    if (tableModel.isDeletedColumn(row)) {
                        deletedColumnsIds.remove(c.getId());
                        ((AbstractTableModel) columnsTable.getModel()).fireTableDataChanged();
                        return;
                    }

                    if (tableModel.isNewColumn(row)) {
                        String[] options = {"Aceptar", "Cancelar"};
                        int result = JOptionPane.showOptionDialog(this,
                                "Esta seguro que desea eliminar la nueva columna '" + c.getName() + "'? Esta operacion no es reversible",
                                "Eliminar Nueva Columna",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[0]);
                        if (result == JOptionPane.YES_OPTION) {
                            insertColumnInPosition(row, columns.size());
                            columns.remove(c);
                        }
                    } else {
                        deletedColumnsIds.add(c.getId());
                    }
                    ((AbstractTableModel) columnsTable.getModel()).fireTableDataChanged();

                });
        columnsTable.getColumnModel().getColumn(7).setCellRenderer(btnRenderer);
        columnsTable.getColumnModel().getColumn(7).setCellEditor(btnRenderer);

        columnsTable.removeColumn(columnsTable.getColumn("Order"));

        columnsTable.setDefaultRenderer(Integer.class, new ColumnsTableDefaultCellRenderer());
        columnsTable.setDefaultRenderer(Object.class, new ColumnsTableDefaultCellRenderer());

    }

    private void showColumnEditingPanel(Column column) {

        isColumnEditing = true;

        columnDefinitionPanel.setNewColumn(column);

        if (!newTableStructure.isNewTable() && !newTableStructure.isNewColumn(column.getId())) {
            UUID originalColId = newTableStructure.getColumnsMappings().get(column.getId());
            columnDefinitionPanel.setOriginalColumn(originalTableStructure.getColumn(originalColId));
        } else {
            columnDefinitionPanel.setOriginalColumn(null);
        }

        UIHelper.swapComponents(columnsTableTopPanel, generalColumnActionsPanel, columnDefinitionPanel);

        ((AbstractTableModel) columnsTable.getModel()).fireTableDataChanged();

    }

    public void setTable(TableRedefinition table) {
        this.newTableStructure = table;
        this.originalTableStructure = table.getOriginalTable();
        this.columns = table.getColumns().stream().map(c -> c.clone()).collect(Collectors.toList());
        this.indexes = table.getIndexes().stream().map(i -> i.clone()).collect(Collectors.toList());
        this.deletedColumnsIds = new HashSet<>(table.getDeletedColumns());
        initializeTableFields();
    }

    private void insertColumnInPosition(int rowIndex, int newOrdinalPosition) {

        ColumnsTableModel tableModel = (ColumnsTableModel) columnsTable.getModel();
        Column column = tableModel.getColumn(rowIndex);

        if (column.getOrdinalPosition() == newOrdinalPosition) {
            return;
        }

        if (column.getOrdinalPosition() < newOrdinalPosition) {
            columns.stream()
                    .filter(c -> column.getOrdinalPosition() < c.getOrdinalPosition() && c.getOrdinalPosition() <= newOrdinalPosition)
                    .forEach(c -> c.setOrdinalPosition(c.getOrdinalPosition() - 1));
        } else {
            columns.stream()
                    .filter(c -> column.getOrdinalPosition() > c.getOrdinalPosition() && c.getOrdinalPosition() >= newOrdinalPosition)
                    .forEach(c -> c.setOrdinalPosition(c.getOrdinalPosition() + 1));
        }

        column.setOrdinalPosition(newOrdinalPosition);

    }

    public void setReportMessageListener(ReportMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    private void reportMessage(String message) {
        if (messageListener != null) {
            messageListener.reportMessage(message);
        }
    }

    class ColumnDefinitionPanelActionListener implements DefinitionPanelActionListener {

        @Override
        public void onSaveClicked() {
            isColumnEditing = false;

            Column editedColumn = columnDefinitionPanel.getNewColumn();

            int columnIndex = IntStream.range(0, columns.size())
                    .filter(i -> columns.get(i).getId() == editedColumn.getId())
                    .findFirst()
                    .orElse(-1);

            if (columnIndex == -1) {
                columns.add(editedColumn);
            } else {
                columns.set(columnIndex, editedColumn);
            }

            UIHelper.swapComponents(columnsTableTopPanel, columnDefinitionPanel, generalColumnActionsPanel);

            ((AbstractTableModel) columnsTable.getModel()).fireTableDataChanged();
        }

        @Override
        public void onCancelClicked() {
            isColumnEditing = false;

            UIHelper.swapComponents(columnsTableTopPanel, columnDefinitionPanel, generalColumnActionsPanel);

            ((AbstractTableModel) columnsTable.getModel()).fireTableDataChanged();
        }

    }

    class TableStructurePanelMouseAdapter extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == btnAddColumn) {
                Column newColumn = new Column();

                newColumn.setOrdinalPosition(columns.size() + 1);

                showColumnEditingPanel(newColumn);
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

        generalColumnActionsPanel = new javax.swing.JPanel();
        btnAddColumn = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        tableStructurePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tfOriginalSchema = new javax.swing.JTextField();
        tfOriginalTableName = new javax.swing.JTextField();
        tfOriginalEngine = new javax.swing.JTextField();
        tfOriginalCharset = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        taTableComment = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tfNewCharset = new javax.swing.JTextField();
        tfNewEngine = new javax.swing.JTextField();
        tfNewTableName = new javax.swing.JTextField();
        tableColumnsPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        columnsTable = new javax.swing.JTable();
        columnsTableTopPanel = new javax.swing.JPanel();
        foreignKeysPanel = new javax.swing.JPanel();
        uniqueKeysPanel = new javax.swing.JPanel();
        indexesPanel = new javax.swing.JPanel();

        btnAddColumn.setText("Add Column");

        javax.swing.GroupLayout generalColumnActionsPanelLayout = new javax.swing.GroupLayout(generalColumnActionsPanel);
        generalColumnActionsPanel.setLayout(generalColumnActionsPanelLayout);
        generalColumnActionsPanelLayout.setHorizontalGroup(
            generalColumnActionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, generalColumnActionsPanelLayout.createSequentialGroup()
                .addGap(0, 858, Short.MAX_VALUE)
                .addComponent(btnAddColumn))
        );
        generalColumnActionsPanelLayout.setVerticalGroup(
            generalColumnActionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnAddColumn)
        );

        tableStructurePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Table Properties"));

        jLabel1.setText("Original Schema:");

        jLabel2.setText("Original Name:");

        jLabel3.setText("Original Engine:");

        jLabel4.setText("Original Charset:");

        jLabel5.setText("Comment:");

        tfOriginalSchema.setEditable(false);

        tfOriginalTableName.setEditable(false);

        tfOriginalEngine.setEditable(false);

        tfOriginalCharset.setEditable(false);

        taTableComment.setColumns(20);
        taTableComment.setRows(5);
        jScrollPane1.setViewportView(taTableComment);

        jLabel6.setText("New Name:");

        jLabel7.setText("New Engine:");

        jLabel8.setText("New Charset:");

        tfNewCharset.setEditable(false);

        tfNewEngine.setEditable(false);

        javax.swing.GroupLayout tableStructurePanelLayout = new javax.swing.GroupLayout(tableStructurePanel);
        tableStructurePanel.setLayout(tableStructurePanelLayout);
        tableStructurePanelLayout.setHorizontalGroup(
            tableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableStructurePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tableStructurePanelLayout.createSequentialGroup()
                        .addGroup(tableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tableStructurePanelLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfOriginalSchema))
                            .addGroup(tableStructurePanelLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfOriginalTableName))
                            .addGroup(tableStructurePanelLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfOriginalEngine))
                            .addGroup(tableStructurePanelLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfOriginalCharset, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tableStructurePanelLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfNewTableName))
                            .addGroup(tableStructurePanelLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfNewCharset, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE))
                            .addGroup(tableStructurePanelLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfNewEngine))))
                    .addGroup(tableStructurePanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );

        tableStructurePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5});

        tableStructurePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel6, jLabel7, jLabel8});

        tableStructurePanelLayout.setVerticalGroup(
            tableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableStructurePanelLayout.createSequentialGroup()
                .addGroup(tableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfOriginalSchema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tableStructurePanelLayout.createSequentialGroup()
                        .addGroup(tableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(tfOriginalTableName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfOriginalEngine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfOriginalCharset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tableStructurePanelLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 51, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(tableStructurePanelLayout.createSequentialGroup()
                        .addGroup(tableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(tfNewTableName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfNewEngine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfNewCharset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        tableColumnsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Columns"));

        columnsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        columnsTable.setRowHeight(24);
        columnsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(columnsTable);

        columnsTableTopPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout tableColumnsPanelLayout = new javax.swing.GroupLayout(tableColumnsPanel);
        tableColumnsPanel.setLayout(tableColumnsPanelLayout);
        tableColumnsPanelLayout.setHorizontalGroup(
            tableColumnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableColumnsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tableColumnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 951, Short.MAX_VALUE)
                    .addComponent(columnsTableTopPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tableColumnsPanelLayout.setVerticalGroup(
            tableColumnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tableColumnsPanelLayout.createSequentialGroup()
                .addComponent(columnsTableTopPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        foreignKeysPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Foreign Keys"));

        javax.swing.GroupLayout foreignKeysPanelLayout = new javax.swing.GroupLayout(foreignKeysPanel);
        foreignKeysPanel.setLayout(foreignKeysPanelLayout);
        foreignKeysPanelLayout.setHorizontalGroup(
            foreignKeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 975, Short.MAX_VALUE)
        );
        foreignKeysPanelLayout.setVerticalGroup(
            foreignKeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 118, Short.MAX_VALUE)
        );

        uniqueKeysPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Unique Keys"));

        javax.swing.GroupLayout uniqueKeysPanelLayout = new javax.swing.GroupLayout(uniqueKeysPanel);
        uniqueKeysPanel.setLayout(uniqueKeysPanelLayout);
        uniqueKeysPanelLayout.setHorizontalGroup(
            uniqueKeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 975, Short.MAX_VALUE)
        );
        uniqueKeysPanelLayout.setVerticalGroup(
            uniqueKeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 225, Short.MAX_VALUE)
        );

        indexesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Indexes"));

        javax.swing.GroupLayout indexesPanelLayout = new javax.swing.GroupLayout(indexesPanel);
        indexesPanel.setLayout(indexesPanelLayout);
        indexesPanelLayout.setHorizontalGroup(
            indexesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 975, Short.MAX_VALUE)
        );
        indexesPanelLayout.setVerticalGroup(
            indexesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 46, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(uniqueKeysPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(foreignKeysPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(indexesPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tableColumnsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tableStructurePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableStructurePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tableColumnsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(foreignKeysPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uniqueKeysPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(indexesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jScrollPane3.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddColumn;
    private javax.swing.JTable columnsTable;
    private javax.swing.JPanel columnsTableTopPanel;
    private javax.swing.JPanel foreignKeysPanel;
    private javax.swing.JPanel generalColumnActionsPanel;
    private javax.swing.JPanel indexesPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea taTableComment;
    private javax.swing.JPanel tableColumnsPanel;
    private javax.swing.JPanel tableStructurePanel;
    private javax.swing.JTextField tfNewCharset;
    private javax.swing.JTextField tfNewEngine;
    private javax.swing.JTextField tfNewTableName;
    private javax.swing.JTextField tfOriginalCharset;
    private javax.swing.JTextField tfOriginalEngine;
    private javax.swing.JTextField tfOriginalSchema;
    private javax.swing.JTextField tfOriginalTableName;
    private javax.swing.JPanel uniqueKeysPanel;
    // End of variables declaration//GEN-END:variables
}
