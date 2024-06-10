/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import model.Column;
import model.ForeignKeyConstraint;
import model.Index;
import model.PrimaryKeyConstraint;
import model.Table;
import model.TableConstraint;
import model.UniqueKeyConstraint;

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
        String baseQueryString = "SELECT `TABLE_NAME` \n"
                + "FROM `INFORMATION_SCHEMA`.`TABLES` \n"
                + "WHERE `TABLE_SCHEMA`= ? \n"
                + "    AND `TABLE_TYPE`='BASE TABLE';";

        try (PreparedStatement statement = connection.prepareStatement(baseQueryString)) {
            statement.setString(1, schemaName);

            try (ResultSet resultSet = statement.executeQuery();) {

                while (resultSet.next()) {
                    tableNames.add(resultSet.getString("TABLE_NAME"));
                }
            }
        }

        return tableNames;
    }

    public List<TableConstraint> getTableConstraints(String tableName, String constraintType) throws SQLException {

        List<TableConstraint> constraints = new ArrayList<>();
        String constraintsQueryString;

        if (constraintType.equals(TableConstraint.FOREIGN_KEY_TYPE)) {
            constraintsQueryString = "SELECT col.*, fk_info.UPDATE_RULE, fk_info.DELETE_RULE\n"
                    + "FROM (information_schema.TABLE_CONSTRAINTS rest\n"
                    + "JOIN\n"
                    + "information_schema.KEY_COLUMN_USAGE col\n"
                    + "ON (rest.CONSTRAINT_SCHEMA = col.CONSTRAINT_SCHEMA AND\n"
                    + "rest.CONSTRAINT_NAME = col.CONSTRAINT_NAME AND\n"
                    + "rest.TABLE_SCHEMA = col.TABLE_SCHEMA AND\n"
                    + "rest.TABLE_NAME = col.TABLE_NAME))\n"
                    + "JOIN information_schema.REFERENTIAL_CONSTRAINTS fk_info\n"
                    + "ON (rest.CONSTRAINT_SCHEMA = fk_info.CONSTRAINT_SCHEMA AND\n"
                    + "rest.CONSTRAINT_NAME = fk_info.CONSTRAINT_NAME AND\n"
                    + "rest.TABLE_NAME = fk_info.TABLE_NAME\n"
                    + ")\n"
                    + "WHERE rest.`TABLE_SCHEMA`= ?\n"
                    + " AND rest.`TABLE_NAME`= ?\n"
                    + " AND rest.`CONSTRAINT_TYPE`= ?\n"
                    + " ORDER BY rest.CONSTRAINT_NAME, col.ORDINAL_POSITION;";
        } else {
            constraintsQueryString = "SELECT col.*\n"
                    + "FROM (information_schema.TABLE_CONSTRAINTS rest\n"
                    + "JOIN\n"
                    + "information_schema.KEY_COLUMN_USAGE col\n"
                    + "ON (rest.CONSTRAINT_SCHEMA = col.CONSTRAINT_SCHEMA AND\n"
                    + "rest.CONSTRAINT_NAME = col.CONSTRAINT_NAME AND\n"
                    + "rest.TABLE_SCHEMA = col.TABLE_SCHEMA AND\n"
                    + "rest.TABLE_NAME = col.TABLE_NAME))"
                    + "WHERE rest.`TABLE_SCHEMA`= ?\n"
                    + " AND rest.`TABLE_NAME`= ?\n"
                    + " AND rest.`CONSTRAINT_TYPE`= ?\n"
                    + " ORDER BY rest.CONSTRAINT_NAME, col.ORDINAL_POSITION;";
        }

        try (PreparedStatement statement = connection.prepareStatement(constraintsQueryString)) {
            statement.setString(1, schemaName);
            statement.setString(2, tableName);
            statement.setString(3, constraintType);

            try (ResultSet resultSet = statement.executeQuery();) {

                String lastConstraintName = null;
                TableConstraint newConstraint = null;

                while (resultSet.next()) {

                    String constraintName = resultSet.getString("CONSTRAINT_NAME");

                    if (!constraintName.equals(lastConstraintName)) {
                        lastConstraintName = constraintName;
                        switch (constraintType) {
                            case TableConstraint.PRIMARY_KEY_TYPE:
                                newConstraint = new PrimaryKeyConstraint(constraintName);
                                break;
                            case TableConstraint.FOREIGN_KEY_TYPE:
                                String referencedTableSchema = resultSet.getString("REFERENCED_TABLE_SCHEMA");
                                String referencedTable = resultSet.getString("REFERENCED_TABLE_NAME");
                                String onUpdateAction = resultSet.getString("UPDATE_RULE");
                                String onDeleteAction = resultSet.getString("DELETE_RULE");
                                newConstraint = new ForeignKeyConstraint(constraintName, referencedTableSchema, referencedTable, onUpdateAction, onDeleteAction);
                                break;
                            case TableConstraint.UNIQUE_TYPE:
                                newConstraint = new UniqueKeyConstraint(constraintName);
                                break;
                        }
                        constraints.add(newConstraint);
                    }

                    String columnName = resultSet.getString("COLUMN_NAME");
                    Integer ordinalPosition = resultSet.getInt("ORDINAL_POSITION");

                    newConstraint.getColumnsOrdinalPositions().put(columnName, ordinalPosition);

                    if (constraintType.equals(TableConstraint.FOREIGN_KEY_TYPE)) {
                        String referencedColumnName = resultSet.getString("REFERENCED_COLUMN_NAME");
                        ((ForeignKeyConstraint) newConstraint).getReferencedColumnsMatches().put(columnName, referencedColumnName);
                    }

                }
            }
        }

        if (!constraintType.equals(TableConstraint.FOREIGN_KEY_TYPE)) {
            return constraints;
        }

        return constraints;

    }

    public List<Index> getTableIndexes(String tableName, List<UniqueKeyConstraint> uniqueKeys, List<ForeignKeyConstraint> foreignKeys) throws SQLException {

        List<Index> indexes = new ArrayList<>();
        String indexesQueryString = "SELECT *\n"
                + "FROM information_schema.STATISTICS S\n"
                + "WHERE TABLE_SCHEMA = ? AND\n"
                + "TABLE_NAME = ? AND\n";

        List<String> excludedIndexNames = new ArrayList<>();
        excludedIndexNames.add("PRIMARY");
        excludedIndexNames.addAll(uniqueKeys.stream().map(u -> u.getName()).collect(Collectors.toList()));
        excludedIndexNames.addAll(foreignKeys.stream().map(f -> f.getName()).collect(Collectors.toList()));

        indexesQueryString += "INDEX_NAME NOT IN " + "('" + String.join("', '", excludedIndexNames) + "')" + "\n"
                + "ORDER BY INDEX_SCHEMA, INDEX_NAME;";

        try (PreparedStatement statement = connection.prepareStatement(indexesQueryString)) {
            statement.setString(1, schemaName);
            statement.setString(2, tableName);

            try (ResultSet resultSet = statement.executeQuery();) {

                String lastIndexName = null;
                Index newIndex = null;

                while (resultSet.next()) {
                    String name = resultSet.getString("INDEX_NAME");

                    if (!name.equals(lastIndexName)) {
                        lastIndexName = name;

                        Boolean unique = resultSet.getString("NON_UNIQUE").equals("0");
                        String comment = resultSet.getString("INDEX_COMMENT");
                        newIndex = new Index(name, unique, comment);

                        indexes.add(newIndex);
                    }

                    String columnName = resultSet.getString("COLUMN_NAME");
                    Integer ordinalPosition = resultSet.getInt("SEQ_IN_INDEX");

                    newIndex.getColumnsOrdinalPositions().put(columnName, ordinalPosition);
                }
            }
        }

        return indexes;
    }

    public List<Column> getTableColumns(String tableName) throws SQLException {

        List<Column> columns = new ArrayList<Column>();
        String columnsQueryString = "SELECT COLUMN_NAME,"
                + " ORDINAL_POSITION,"
                + " COLUMN_DEFAULT,"
                + " IS_NULLABLE,"
                + " COLLATION_NAME,"
                + " COLUMN_TYPE,"
                + " EXTRA,"
                + " COLUMN_COMMENT \n"
                + "FROM `INFORMATION_SCHEMA`.`COLUMNS` \n"
                + "WHERE `TABLE_SCHEMA`= ?\n"
                + " AND `TABLE_NAME`= ?;";

        try (PreparedStatement statement = connection.prepareStatement(columnsQueryString)) {
            statement.setString(1, schemaName);
            statement.setString(2, tableName);

            try (ResultSet resultSet = statement.executeQuery();) {

                while (resultSet.next()) {
                    String name = resultSet.getString("COLUMN_NAME");
                    Integer ordinalPosition = resultSet.getInt("ORDINAL_POSITION");
                    String defaultValue = resultSet.getString("COLUMN_DEFAULT");
                    Boolean isNullable = resultSet.getString("IS_NULLABLE").equals("YES");
                    String collation = resultSet.getString("COLLATION_NAME");
                    String type = resultSet.getString("COLUMN_TYPE");
                    String extra = resultSet.getString("EXTRA");
                    String comment = resultSet.getString("COLUMN_COMMENT");

                    columns.add(new Column(name, ordinalPosition, isNullable, defaultValue, type, collation, comment, extra));
                }
            }
        }

        return columns;
    }

    public Table getTableStructure(String tableName) throws SQLException {

        Table table = null;
        String tableQueryString = "SELECT TABLE_SCHEMA,"
                + " TABLE_NAME,"
                + " ENGINE,"
                + " AUTO_INCREMENT,"
                + " TABLE_COLLATION,"
                + " TABLE_COMMENT \n"
                + "FROM `INFORMATION_SCHEMA`.`TABLES` \n"
                + "WHERE `TABLE_SCHEMA`= ?\n"
                + " AND `TABLE_NAME`= ?\n"
                + " AND `TABLE_TYPE`='BASE TABLE';";

        try (PreparedStatement statement = connection.prepareStatement(tableQueryString)) {
            statement.setString(1, schemaName);
            statement.setString(2, tableName);

            try (ResultSet resultSet = statement.executeQuery();) {

                while (resultSet.next()) {
                    String schema = resultSet.getString("TABLE_SCHEMA");
                    String name = resultSet.getString("TABLE_NAME");
                    String engine = resultSet.getString("ENGINE");
                    Integer increment = resultSet.getInt("AUTO_INCREMENT");
                    String collation = resultSet.getString("TABLE_COLLATION");
                    String comment = resultSet.getString("TABLE_COMMENT");
                    table = new Table(schema, name, engine, collation, increment, comment);
                }
            }
        }

        if (table == null) {
            return null;
        }

        List<Column> columns = getTableColumns(tableName);
        table.setColumns(columns);

        List<PrimaryKeyConstraint> primaryKeys = getTableConstraints(tableName, TableConstraint.PRIMARY_KEY_TYPE).stream()
                .map(c -> (PrimaryKeyConstraint) c)
                .collect(Collectors.toList());
        if (!primaryKeys.isEmpty()) {
            table.setPrimaryKeyConstraint(primaryKeys.get(0));
        }

        List<ForeignKeyConstraint> foreignKeys = getTableConstraints(tableName, TableConstraint.FOREIGN_KEY_TYPE)
                .stream()
                .map(c -> (ForeignKeyConstraint) c).collect(Collectors.toList());
        table.setForeignKeyConstraints(foreignKeys);

        List<UniqueKeyConstraint> uniqueKeys = getTableConstraints(tableName, TableConstraint.UNIQUE_TYPE)
                .stream()
                .map(c -> (UniqueKeyConstraint) c).collect(Collectors.toList());
        table.setUniqueKeyConstraints(uniqueKeys);

        List<Index> indexes = getTableIndexes(tableName, uniqueKeys, foreignKeys);
        table.setIndexes(indexes);

        return table;
    }

    private String getConnectionString() {
        String baseConnectionString = "jdbc:mysql://%s:%s/%s";
        return String.format(baseConnectionString, serverIp, port, schemaName);
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
