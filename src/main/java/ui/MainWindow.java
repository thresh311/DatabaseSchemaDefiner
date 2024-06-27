/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import helpers.DBHelper;
import helpers.SettingsHelper;
import helpers.UIHelper;
import interfaces.ReportMessageListener;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Window;
import javax.swing.JComponent;
import model.Table;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import interfaces.SettingsPanelActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.SwingWorker;
import model.Column;
import model.ForeignKeyConstraint;
import model.PrimaryKeyConstraint;
import model.TableRedefinition;
import interfaces.DefinitionPanelActionListener;
import javax.swing.JFrame;

/**
 *
 * @author juanv
 */
public class MainWindow 
        extends javax.swing.JFrame
        implements ReportMessageListener,
        SettingsPanelActionListener {

    private SettingsPanel settingsPanel;
    private TableStructurePanel tableStructurePanel;
    private DBHelper dbHelper;
    private SettingsHelper settings;
    private JPanel currentlyShownPanel;
    private List<TableRedefinition> tablesRedefinitions;
    private DefaultMutableTreeNode tablesNode;

    /**
     * Creates new form MainWindow
     */
    public MainWindow(DBHelper dbHelper, SettingsHelper settings) {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.dbHelper = dbHelper;
        this.settings = settings;
        settingsPanel = new SettingsPanel(dbHelper, settings, this);
        settingsPanel.setReportMessageListener(this);
        tableStructurePanel = new TableStructurePanel();
        settingsPanel.setReportMessageListener(this);

        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.add(tableStructurePanel, BorderLayout.CENTER);
        currentlyShownPanel = tableStructurePanel;

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");

        DefaultMutableTreeNode settingsNode = new DefaultMutableTreeNode("General Settings");

        tablesNode = new DefaultMutableTreeNode("Tables");

        rootNode.add(settingsNode);
        rootNode.add(tablesNode);

        tablesTree.setModel(new DefaultTreeModel(rootNode));
        tablesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tablesTree.setRootVisible(false);

        tablesTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tablesTree.getLastSelectedPathComponent();

            if (node == null) {
                return;
            }

            Object nodeInfo = node.getUserObject();
            if (node.isLeaf()) {
                if (!(nodeInfo instanceof TableRedefinition)) {
                    UIHelper.swapComponents(mainContentPanel, currentlyShownPanel, settingsPanel);
                    currentlyShownPanel = settingsPanel;
                } else {
                    UIHelper.swapComponents(mainContentPanel, currentlyShownPanel, tableStructurePanel);
                    currentlyShownPanel = tableStructurePanel;
                    tableStructurePanel.setTable((TableRedefinition) nodeInfo);
                }
            }
        });

        if ((new File(settings.getSetting(SettingsHelper.TABLES_FILE)).exists())) {
            loadTableRedefinitionsFromFile();
        }

    }

    @Override
    public void reportMessage(String message) {
        taMessageReport.setText(message);
    }

    @Override
    public void executeTablesImport() {

        tablesTree.setEnabled(false);

        SwingWorker worker = new SwingWorker<List<Table>, String>() {
            @Override
            public List<Table> doInBackground() {
                try {
                    dbHelper.openConnection();
                    List<String> names = dbHelper.getAllTableNames();
                    List<Table> tables = names.stream().map(t -> {
                        try {
                            publish(t);
                            return dbHelper.getTableStructure(t);
                        } catch (SQLException ex) {
                            reportMessage("Hubo un error cargando la informacion de la tabla: " + t + "\n" + ex.toString());
                            return null;
                        }
                    }).collect(Collectors.toList());

                    reportMessage("Las tablas se importaron correctamente.\n");

                    return tables;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    reportMessage("No se pudo conectar a la base de datos.\n" + ex.toString());
                } finally {
                    try {
                        dbHelper.closeConnection();
                    } catch (SQLException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String name : chunks) {
                    reportMessage("Importing " + name);
                }
            }

            @Override
            protected void done() {
                try {
                    if (get() != null) {
                        List<Table> tables = get();
                        generateTableRedefinitions(tables);
                        saveTableRedefinitionsToFile();
                    }
                } catch (Exception ex) {
                    reportMessage("Ocurrio un error durante el procesado de las tablas importadas. \n" + ex.toString());
                } finally {
                    refreshTreeNodes();
                    tablesTree.setEnabled(true);
                    settingsPanel.enableSettingsControls();
                }
            }

        };

        worker.execute();

    }

    private void generateTableRedefinitions(List<Table> importedTables) {

        reportMessage("Procesando las tablas importadas.\n");

        tablesRedefinitions = new ArrayList<>();
        HashMap<String, UUID> tablesIds = new HashMap<>();
        HashMap<String, UUID> columnsIds = new HashMap<>();

        for (Table table : importedTables) {
            TableRedefinition tableRedefinition = new TableRedefinition(table);

            String baseIdentifier = table.getSchema() + "/" + table.getName();

            tablesIds.put(baseIdentifier, tableRedefinition.getId());

            for (Column column : tableRedefinition.getColumns()) {
                columnsIds.put(baseIdentifier + "/" + column.getName(),
                        column.getId());
            }

            tablesRedefinitions.add(tableRedefinition);
        }

        for (TableRedefinition table : tablesRedefinitions) {
            Table originalStructure = table.getOriginalTable();

            for (ForeignKeyConstraint ForeignKey : originalStructure.getForeignKeyConstraints()) {

            }

        }

        reportMessage("Las tablas importadas se procesaron correctamente.\n");

    }

    private void refreshTreeNodes() {
        tablesNode.removeAllChildren();
        if (tablesRedefinitions == null) {
            return;
        }
        tablesRedefinitions.stream().forEach(t -> tablesNode.add(new DefaultMutableTreeNode(t)));
    }

    @Override
    public boolean saveTableRedefinitionsToFile() {
        try (FileOutputStream fileOutputStream
                = new FileOutputStream(settings.getSetting(SettingsHelper.TABLES_FILE))) {
            try (ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);) {

                objectOutputStream.writeObject(tablesRedefinitions);
                objectOutputStream.flush();
                reportMessage("Se guardo exitosamente el esquema en el archivo.");
            }
        } catch (IOException ex) {
            reportMessage("Ocurrio un error inesperado durante el guardado de la estructura del esquema.\n" + ex.toString());
            return false;
        }
        return true;
    }
   
    public void loadTableRedefinitionsFromFile() {
        try (FileInputStream fileInputStream = new FileInputStream(settings.getSetting(SettingsHelper.TABLES_FILE))) {
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            tablesRedefinitions = (List<TableRedefinition>) objectInputStream.readObject();
            objectInputStream.close();
            reportMessage("Se importo correctamente la definicion del esquema.");
            refreshTreeNodes();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            reportMessage("No se pudo encontrar el archivo de definicion del esquema a importar.\n" + ex.toString());
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            reportMessage("Ocurrio un error inesperado durante la importacion.\n" + ex.toString());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            reportMessage("Ocurrio un error inesperado durante la importacion.\n" + ex.toString());
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablesTree = new javax.swing.JTree();
        spMainContent = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        taMessageReport = new javax.swing.JTextArea();
        mainContentPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSplitPane1.setDividerLocation(200);

        jScrollPane1.setViewportView(tablesTree);

        jSplitPane1.setLeftComponent(jScrollPane1);

        spMainContent.setDividerLocation(375);
        spMainContent.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        spMainContent.setResizeWeight(1.0);

        taMessageReport.setEditable(false);
        taMessageReport.setColumns(20);
        taMessageReport.setRows(5);
        jScrollPane2.setViewportView(taMessageReport);

        spMainContent.setBottomComponent(jScrollPane2);

        javax.swing.GroupLayout mainContentPanelLayout = new javax.swing.GroupLayout(mainContentPanel);
        mainContentPanel.setLayout(mainContentPanelLayout);
        mainContentPanelLayout.setHorizontalGroup(
            mainContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 602, Short.MAX_VALUE)
        );
        mainContentPanelLayout.setVerticalGroup(
            mainContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 392, Short.MAX_VALUE)
        );

        spMainContent.setTopComponent(mainContentPanel);

        jSplitPane1.setRightComponent(spMainContent);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 815, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {

            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException ex) {
                    System.out.println("The driver for the DB connection is missing.");
                }

                SettingsHelper settings = new SettingsHelper();

                settings.loadSettings();

                String serverIp = settings.getSetting(SettingsHelper.SERVER_IP);
                String port = settings.getSetting(SettingsHelper.SERVER_PORT);
                String schemaName = settings.getSetting(SettingsHelper.SCHEMA_NAME);
                String username = settings.getSetting(SettingsHelper.USERNAME);
                String password = settings.getSetting(SettingsHelper.PASSWORD);

                DBHelper dbHelper = new DBHelper(serverIp, port, schemaName, username, password);

                new MainWindow(dbHelper, settings).setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel mainContentPanel;
    private javax.swing.JSplitPane spMainContent;
    private javax.swing.JTextArea taMessageReport;
    private javax.swing.JTree tablesTree;
    // End of variables declaration//GEN-END:variables

}
