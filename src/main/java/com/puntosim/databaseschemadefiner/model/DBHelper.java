/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.puntosim.databaseschemadefiner.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author juanv
 */
public class DBHelper {

    private String serverIp, port, schemaName, username, password;
    private Connection connection = null;

    public DBHelper(String serverIp, String port, String schemaName, String username, String password) {
        this.serverIp = serverIp;
        this.port = port;
        this.schemaName = schemaName;
        this.username = username;
        this.password = password;
    }

    public void openConnection() throws SQLException {
        if (connection != null) {
            return;
        }
        connection = DriverManager.getConnection(getConnectionString(), username, password);
    }

    public void closeConnection() throws SQLException {
        if (connection == null) {
            return;
        }
        connection.close();
        connection = null;
    }

    public List<String> getAllTableNames() throws SQLException {

        List<String> tableNames = new ArrayList<>();
        String baseQueryString = "SELECT `COLUMN_NAME` \n"
                + "FROM `INFORMATION_SCHEMA`.`TABLES` \n"
                + "WHERE `TABLE_SCHEMA`= ? \n"
                + "    AND `TABLE_TYPE`='BASE TABLE';";

        try (PreparedStatement statement = connection.prepareStatement(baseQueryString)) {
            statement.setString(1, schemaName);

            try (ResultSet resultSet = statement.executeQuery();) {

                while (resultSet.next()) {
                    tableNames.add(resultSet.getString(1));
                }
            }
        }

        return tableNames;
    }

    private String getConnectionString() {
        String baseConnectionString = "jdbc:mysql://%s:%s/%s";
        return String.format(baseConnectionString, serverIp, port, schemaName);
    }

}
