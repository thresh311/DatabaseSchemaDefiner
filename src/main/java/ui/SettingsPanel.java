/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import helpers.DBHelper;
import helpers.SettingsHelper;
import interfaces.ReportMessageListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import model.Table;
import interfaces.SettingsPanelActionListener;

/**
 *
 * @author juanv
 */
public class SettingsPanel extends javax.swing.JPanel {

    private SettingsHelper settings;
    private ReportMessageListener messageListener;
    private DBHelper dbHelper;
    private SettingsPanelActionListener actionsListener;

    /**
     * Creates new form SettingsPanel
     */
    public SettingsPanel(DBHelper dbHelper, SettingsHelper settings, SettingsPanelActionListener listener) {
        initComponents();

        this.actionsListener = listener;
        this.settings = settings;
        this.dbHelper = dbHelper;

        initializeSettingsFields();

        btnTestConnection.addMouseListener(new SettingsPanelMouseAdapter());
        btnBrowseSchemaFile.addMouseListener(new SettingsPanelMouseAdapter());
        btnImportTables.addMouseListener(new SettingsPanelMouseAdapter());
        btnLoadDefinitionFile.addMouseListener(new SettingsPanelMouseAdapter());
        btnSaveSettings.addMouseListener(new SettingsPanelMouseAdapter());
        btnSaveState.addMouseListener(new SettingsPanelMouseAdapter());
        btnLoadSettings.addMouseListener(new SettingsPanelMouseAdapter());

        tfPassword.getDocument().addDocumentListener(new SettingsPanelDocumentListener());
        tfPassword.getDocument().putProperty("owner", tfPassword);
        tfUsername.getDocument().addDocumentListener(new SettingsPanelDocumentListener());
        tfUsername.getDocument().putProperty("owner", tfUsername);
        tfServerIp.getDocument().addDocumentListener(new SettingsPanelDocumentListener());
        tfServerIp.getDocument().putProperty("owner", tfServerIp);
        tfDBSchema.getDocument().addDocumentListener(new SettingsPanelDocumentListener());
        tfDBSchema.getDocument().putProperty("owner", tfDBSchema);
        tfServerPort.getDocument().addDocumentListener(new SettingsPanelDocumentListener());
        tfServerPort.getDocument().putProperty("owner", tfServerPort);
        tfSchemaFile.getDocument().addDocumentListener(new SettingsPanelDocumentListener());
        tfSchemaFile.getDocument().putProperty("owner", tfSchemaFile);

    }

    private void initializeSettingsFields() {
        tfPassword.setText(settings.getSetting(SettingsHelper.PASSWORD));
        tfUsername.setText(settings.getSetting(SettingsHelper.USERNAME));
        tfServerIp.setText(settings.getSetting(SettingsHelper.SERVER_IP));
        tfDBSchema.setText(settings.getSetting(SettingsHelper.SCHEMA_NAME));
        tfServerPort.setText(settings.getSetting(SettingsHelper.SERVER_PORT));
        tfSchemaFile.setText(settings.getSetting(SettingsHelper.TABLES_FILE));
    }

    public void setReportMessageListener(ReportMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    private void reportMessage(String message) {
        if (messageListener != null) {
            messageListener.reportMessage(message);
        }
    }

    private void testConnection() {
        try {
            dbHelper.openConnection();
            dbHelper.closeConnection();
            reportMessage("La conexion a la base de datos se establecio correctamente.");
        } catch (SQLException ex) {
            Logger.getLogger(SettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
            reportMessage("No se pudo conectar a la base de datos.\n" + ex.toString());
        }
    }

    class SettingsPanelDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            handleSettingChanged(e.getDocument().getProperty("owner"));
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            handleSettingChanged(e.getDocument().getProperty("owner"));
        }

        @Override
        public void changedUpdate(DocumentEvent e) {

        }

        private void handleSettingChanged(Object field) {
            if (field == tfDBSchema) {
                settings.setSetting(SettingsHelper.SCHEMA_NAME, tfDBSchema.getText());
                dbHelper.setSchemaName(tfDBSchema.getText());
            }
            if (field == tfPassword) {
                settings.setSetting(SettingsHelper.PASSWORD, tfPassword.getText());
                dbHelper.setPassword(tfPassword.getText());
            }
            if (field == tfSchemaFile) {
                settings.setSetting(SettingsHelper.TABLES_FILE, tfSchemaFile.getText());
            }
            if (field == tfServerIp) {
                settings.setSetting(SettingsHelper.SERVER_IP, tfServerIp.getText());
                dbHelper.setServerIp(tfServerIp.getText());
            }
            if (field == tfServerPort) {
                settings.setSetting(SettingsHelper.SERVER_PORT, tfServerPort.getText());
                dbHelper.setPort(tfServerPort.getText());
            }
            if (field == tfUsername) {
                settings.setSetting(SettingsHelper.USERNAME, tfUsername.getText());
                dbHelper.setUsername(tfUsername.getText());
            }
        }

    }
    
    private void disableSettingsControls() {
        btnBrowseSchemaFile.setEnabled(false);
        btnImportTables.setEnabled(false);
        btnLoadDefinitionFile.setEnabled(false);
        btnLoadSettings.setEnabled(false);
        btnSaveSettings.setEnabled(false);
        btnSaveState.setEnabled(false);
        btnTestConnection.setEnabled(false);
        tfDBSchema.setEnabled(false);
        tfPassword.setEnabled(false);
        tfSchemaFile.setEnabled(false);
        tfServerIp.setEnabled(false);
        tfServerPort.setEnabled(false);
        tfUsername.setEnabled(false);
    }
        
    public void enableSettingsControls() {
        btnBrowseSchemaFile.setEnabled(true);
        btnImportTables.setEnabled(true);
        btnLoadDefinitionFile.setEnabled(true);
        btnLoadSettings.setEnabled(true);
        btnSaveSettings.setEnabled(true);
        btnSaveState.setEnabled(true);
        btnTestConnection.setEnabled(true);
        tfDBSchema.setEnabled(true);
        tfPassword.setEnabled(true);
        tfSchemaFile.setEnabled(true);
        tfServerIp.setEnabled(true);
        tfServerPort.setEnabled(true);
        tfUsername.setEnabled(true);
    }

    class SettingsPanelMouseAdapter extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {

            if (e.getSource() == btnBrowseSchemaFile) {

            }
            if (e.getSource() == btnImportTables) {
                disableSettingsControls();
                actionsListener.executeTablesImport();
            }
            if (e.getSource() == btnTestConnection) {
                testConnection();
            }
            if (e.getSource() == btnLoadDefinitionFile) {
                actionsListener.loadTableRedefinitionsFromFile();
            }
            if (e.getSource() == btnSaveSettings) {
                try {
                    settings.saveSettings();
                    reportMessage("Configuracion guardada exitosamente");
                } catch (IOException ex) {
                    Logger.getLogger(SettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
                    reportMessage("Hubo un error insesperado durante el guardado.\n" + ex.toString());
                }
            }
            if (e.getSource() == btnSaveState) {
                actionsListener.saveTableRedefinitionsToFile();
            }
            if (e.getSource() == btnLoadSettings) {
                if (!settings.loadSettings()) {
                    reportMessage("No fue imposible importar el archivo de configuracion.");
                    return;
                }
                initializeSettingsFields();
                reportMessage("Configuracion importada exitosamente");
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

        dbConnectionPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnTestConnection = new javax.swing.JButton();
        tfServerIp = new javax.swing.JTextField();
        tfServerPort = new javax.swing.JTextField();
        tfDBSchema = new javax.swing.JTextField();
        tfUsername = new javax.swing.JTextField();
        tfPassword = new javax.swing.JPasswordField();
        btnSaveSettings = new javax.swing.JButton();
        btnLoadSettings = new javax.swing.JButton();
        generalActionsPanel = new javax.swing.JPanel();
        btnLoadDefinitionFile = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btnSaveState = new javax.swing.JButton();
        btnImportTables = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        btnBrowseSchemaFile = new javax.swing.JButton();
        tfSchemaFile = new javax.swing.JTextField();

        dbConnectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("DB Connection"));

        jLabel1.setText("Server IP:");

        jLabel2.setText("Server Port:");

        jLabel3.setText("DB Schema:");

        jLabel4.setText("Username:");

        jLabel5.setText("Password:");

        btnTestConnection.setText("Test Connection");

        javax.swing.GroupLayout dbConnectionPanelLayout = new javax.swing.GroupLayout(dbConnectionPanel);
        dbConnectionPanel.setLayout(dbConnectionPanelLayout);
        dbConnectionPanelLayout.setHorizontalGroup(
            dbConnectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dbConnectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dbConnectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dbConnectionPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnTestConnection))
                    .addGroup(dbConnectionPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfServerIp))
                    .addGroup(dbConnectionPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfServerPort))
                    .addGroup(dbConnectionPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfDBSchema))
                    .addGroup(dbConnectionPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfPassword))
                    .addGroup(dbConnectionPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfUsername)))
                .addContainerGap())
        );

        dbConnectionPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5});

        dbConnectionPanelLayout.setVerticalGroup(
            dbConnectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dbConnectionPanelLayout.createSequentialGroup()
                .addGroup(dbConnectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfServerIp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dbConnectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dbConnectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tfDBSchema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dbConnectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dbConnectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnTestConnection)
                .addContainerGap())
        );

        btnSaveSettings.setText("Save Settings");

        btnLoadSettings.setText("Load Settings");

        generalActionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Manage Tables Definitions"));

        btnLoadDefinitionFile.setText("Load Schema");

        jLabel7.setText("Load Schema Definition File:");

        jLabel8.setText("Import Tables from Database:");

        jLabel9.setText("Save Schema Definition in File:");

        btnSaveState.setText("Save Schema");

        btnImportTables.setText("Import Tables");

        jLabel6.setText("Tables Definitions File:");

        btnBrowseSchemaFile.setText("Browse");

        javax.swing.GroupLayout generalActionsPanelLayout = new javax.swing.GroupLayout(generalActionsPanel);
        generalActionsPanel.setLayout(generalActionsPanelLayout);
        generalActionsPanelLayout.setHorizontalGroup(
            generalActionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalActionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalActionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(generalActionsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfSchemaFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBrowseSchemaFile))
                    .addGroup(generalActionsPanelLayout.createSequentialGroup()
                        .addGroup(generalActionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 213, Short.MAX_VALUE)
                        .addGroup(generalActionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSaveState, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnLoadDefinitionFile, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnImportTables, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );

        generalActionsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnImportTables, btnLoadDefinitionFile, btnSaveState});

        generalActionsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel7, jLabel8, jLabel9});

        generalActionsPanelLayout.setVerticalGroup(
            generalActionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalActionsPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(generalActionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBrowseSchemaFile)
                    .addComponent(tfSchemaFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(generalActionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLoadDefinitionFile)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(generalActionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaveState)
                    .addComponent(jLabel9))
                .addGap(5, 5, 5)
                .addGroup(generalActionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(btnImportTables))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dbConnectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnLoadSettings)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSaveSettings))
                    .addComponent(generalActionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dbConnectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(generalActionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaveSettings)
                    .addComponent(btnLoadSettings))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowseSchemaFile;
    private javax.swing.JButton btnImportTables;
    private javax.swing.JButton btnLoadDefinitionFile;
    private javax.swing.JButton btnLoadSettings;
    private javax.swing.JButton btnSaveSettings;
    private javax.swing.JButton btnSaveState;
    private javax.swing.JButton btnTestConnection;
    private javax.swing.JPanel dbConnectionPanel;
    private javax.swing.JPanel generalActionsPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField tfDBSchema;
    private javax.swing.JPasswordField tfPassword;
    javax.swing.JTextField tfSchemaFile;
    private javax.swing.JTextField tfServerIp;
    private javax.swing.JTextField tfServerPort;
    private javax.swing.JTextField tfUsername;
    // End of variables declaration//GEN-END:variables
}
