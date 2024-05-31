/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author juanv
 */
public class Table {

    private String schema;
    private String name;
    private String engine;
    private String collationName;
    private Integer autoIncrement;
    private List<Column> columns;
    private PrimaryKeyConstraint primaryKeyConstraint;
    private List<ForeignKeyConstraint> foreignKeyConstraints;
    private List<UniqueKeyConstraint> uniqueKeyConstraints;
    private String comment;

    public Table(String schema, String name, String engine, String collation, Integer autoIncrement, String comment) {
        this.schema = schema;
        this.name = name;
        this.engine = engine;
        this.collationName = collation;
        this.autoIncrement = autoIncrement;
        this.comment = comment;
        columns = new ArrayList<>();
        foreignKeyConstraints = new ArrayList<>();
        uniqueKeyConstraints = new ArrayList<>();
    } 
    
    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
    
        public void setPrimaryKeyConstraint(PrimaryKeyConstraint primaryKeyConstraint) {
        this.primaryKeyConstraint = primaryKeyConstraint;
    }

    public void setForeignKeyConstraints(List<ForeignKeyConstraint> foreignKeyConstraints) {
        this.foreignKeyConstraints = foreignKeyConstraints;
    }

    public void setUniqueKeyConstraints(List<UniqueKeyConstraint> uniqueKeyConstraints) {
        this.uniqueKeyConstraints = uniqueKeyConstraints;
    }

}
