/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author juanv
 */
public class TableRedefinition implements Serializable {

    private UUID id;
    private String name;
    private String engine;
    private String collationName;
    private Integer autoIncrement;
    private Map<UUID, Column> columns;
    //New Column Id - Old Column Id
    private Map<UUID, UUID> columnsMappings;
    private String comment;
    private Table originalTable;
    private Boolean markedAsDeleted;
    private Boolean markedAsCompleted;
    private PrimaryKeyConstraint primaryKeyConstraint;
    private List<ForeignKeyRedefinition> foreignKeyConstraints;
    private List<UniqueKeyConstraint> uniqueKeyConstraints;
    private List<Index> indexes;
    private Set<UUID> deletedColumnsIds;

    public TableRedefinition() {
        this.id = UUID.randomUUID();
        this.engine = "InnoDB";
        this.collationName = "utf8_general_ci";
        this.columns = new HashMap<UUID, Column>();
        this.markedAsDeleted = false;
        this.markedAsCompleted = false;
        this.deletedColumnsIds = new HashSet<UUID>();
        this.columnsMappings = new HashMap<>();
        foreignKeyConstraints = new ArrayList<>();
        uniqueKeyConstraints = new ArrayList<>();
        indexes = new ArrayList<>();
    }

    public TableRedefinition(Table originalTable) {
        this();
        this.originalTable = originalTable;
        this.name = originalTable.getName();
        this.autoIncrement = originalTable.getAutoIncrement();
        this.comment = originalTable.getComment();

        List<Column> originalColumns = originalTable.getColumns();
        HashMap<UUID, UUID> reversedColumnsMappings = new HashMap<UUID, UUID>();

        for (Column c : originalColumns) {
            String name = c.getName();
            Integer ordinalPosition = c.getOrdinalPosition();
            Boolean isNullable = c.getIsNullable();
            String defaultValue = c.getDefaultValue();
            String type = c.getType();
            String collationName = "utf8_general_ci";
            String comment = c.getComment();
            String extra = c.getExtra();

            Column newColumn = new Column(name, ordinalPosition, isNullable, defaultValue, type, collationName, comment, extra);

            columnsMappings.put(newColumn.getId(), c.getId());
            reversedColumnsMappings.put(c.getId(), newColumn.getId());

            addColumn(newColumn);
        }

        if (originalTable.getPrimaryKeyConstraint() != null) {
            PrimaryKeyConstraint originalPk = originalTable.getPrimaryKeyConstraint();

            PrimaryKeyConstraint newConstraint = new PrimaryKeyConstraint(originalPk.getName());
            Map<UUID, Integer> ordinalPositions = originalPk.getColumnsOrdinalPositions();
            for (UUID c : ordinalPositions.keySet()) {
                newConstraint.getColumnsOrdinalPositions()
                        .put(reversedColumnsMappings.get(c), ordinalPositions.get(c));
            }
        }

        this.uniqueKeyConstraints = originalTable.getUniqueKeyConstraints().stream()
                .map(u -> {
                    UniqueKeyConstraint newConstraint = new UniqueKeyConstraint(u.getName());
                    Map<UUID, Integer> ordinalPositions = u.getColumnsOrdinalPositions();
                    for (UUID c : ordinalPositions.keySet()) {
                        newConstraint.getColumnsOrdinalPositions()
                                .put(reversedColumnsMappings.get(c), ordinalPositions.get(c));
                    }
                    return newConstraint;
                })
                .collect(Collectors.toList());
        this.indexes = originalTable.getIndexes().stream()
                .map(i -> {
                    Index newIndex = new Index(i.getName(), i.getUnique(), i.getComment());
                    Map<UUID, Integer> ordinalPositions = i.getColumnsOrdinalPositions();
                    for (UUID c : ordinalPositions.keySet()) {
                        newIndex.getColumnsOrdinalPositions()
                                .put(reversedColumnsMappings.get(c), ordinalPositions.get(c));
                    }
                    return newIndex;
                })
                .collect(Collectors.toList());

    }

    public Boolean isNew() {
        return originalTable == null;
    }

    public Table getOriginalTable() {
        return originalTable;
    }

    public UUID getId() {
        return id;
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

    public List<Column> getColumns() {
        return columns.values().stream().collect(Collectors.toList());
    }

    public Column getColumn(UUID id) {
        return columns.get(id);
    }

    public void addColumn(Column column) {
        columns.put(column.getId(), column);
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return name + (isNew() ? "" : "/" + originalTable.getName());
    }

}
