/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author juanv
 */
public class SettingsHelper {

    //SETTINGS IDENTIFIERS
    public static final String SCHEMA_NAME = "db.schema.name";
    public static final String SERVER_IP = "db.server.ip";
    public static final String SERVER_PORT = "db.server.port";
    public static final String USERNAME = "db.username";
    public static final String PASSWORD = "db.password";
    public static final String TABLES_FILE = "tables.path";
    
    private File settingsFile = new File("./config.properties");
    private Properties settings = null;
    
    public boolean loadSettings() {
        settings = new Properties();

        if (!settingsFile.exists()) {
            return false;
        }

        try (FileInputStream inputStream = new FileInputStream(settingsFile)) {
            settings.load(inputStream);
            return true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return false;
    }
    
    public void saveSettings() throws FileNotFoundException, IOException {
        
        try (OutputStream output = new FileOutputStream(settingsFile)) {
            settings.store(output, "");
        } 
        
    }

    public String getSetting(String identifier) {
        return settings.getProperty(identifier);
    }
    
    public void setSetting(String identifier, String value) {
        settings.setProperty(identifier, value);
    }

}
