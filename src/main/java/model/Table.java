/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author juanv
 */
public class Table implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String schema;
    private String name;
    private String engine;
    private String collationName;
    private Integer autoIncrement;
    private Map<UUID, Column> columns;
    private PrimaryKeyConstraint primaryKeyConstraint;
    private List<ForeignKeyConstraint> foreignKeyConstraints;
    private List<UniqueKeyConstraint> uniqueKeyConstraints;
    private List<Index> indexes;
    private String comment;

    public Table(String schema, String name, String engine, String collation, Integer autoIncrement, String comment) {
        this.schema = schema;
        this.name = name;
        this.engine = engine;
        this.collationName = collation;
        this.autoIncrement = autoIncrement;
        this.comment = comment;
        columns = new HashMap<UUID, Column>();
        foreignKeyConstraints = new ArrayList<>();
        uniqueKeyConstraints = new ArrayList<>();
        indexes = new ArrayList<>();
    }

    public List<Column> getColumns() {
        return columns.values().stream().collect(Collectors.toList());
    }

    public void addColumn(Column newColumn) {
        columns.put(newColumn.getId(), newColumn);
    }
    
    public Column getColumn(UUID id) {
        return columns.get(id);
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

    public void setIndexes(List<Index> indexes) {
        this.indexes = indexes;
    }

    public String getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }

    public String getEngine() {
        return engine;
    }

    public String getCollationName() {
        return collationName;
    }

    public Integer getAutoIncrement() {
        return autoIncrement;
    }

    public PrimaryKeyConstraint getPrimaryKeyConstraint() {
        return primaryKeyConstraint;
    }

    public List<ForeignKeyConstraint> getForeignKeyConstraints() {
        return foreignKeyConstraints;
    }

    public List<UniqueKeyConstraint> getUniqueKeyConstraints() {
        return uniqueKeyConstraints;
    }

    public List<Index> getIndexes() {
        return indexes;
    }

    public String getComment() {
        return comment;
    }
    
}
