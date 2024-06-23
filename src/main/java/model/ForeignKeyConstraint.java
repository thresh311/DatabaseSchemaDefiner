/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author juanv
 */
public class ForeignKeyConstraint extends TableConstraint{
    
    private static final long serialVersionUID = 1L;
    
    private String referencedTableSchema;
    private String referencedTable;
    //Key: Child Table Column - Value: Parent Table Column
    private Map<UUID, String> referencedColumnsMatches;
    private String onUpdateAction;
    private String onDeleteAction;

    public ForeignKeyConstraint(String name, String referencedTableSchema, String referencedTable, String onUpdateAction, String onDeleteAction) {
        super(name);
        type = TableConstraint.FOREIGN_KEY_TYPE;
        this.referencedTableSchema = referencedTableSchema;
        this.referencedTable = referencedTable;
        this.onUpdateAction = onUpdateAction;
        this.onDeleteAction = onDeleteAction;
        referencedColumnsMatches = new HashMap<UUID, String>();
    }
    
    public String getReferencedTableSchema() {
        return referencedTableSchema;
    }

    public String getReferencedTable() {
        return referencedTable;
    }
    
    public Set<String> getReferencedColumnNames() {
        return new HashSet<>(referencedColumnsMatches.values());
    }
    
    public String getColumnReferencedBy(String columnName) {
        return referencedColumnsMatches.get(columnName);
    }

    public Map<UUID, String> getReferencedColumnsMatches() {
        return referencedColumnsMatches;
    }
    
    public String getOnUpdateAction() {
        return onUpdateAction;
    }

    public String getOnDeleteAction() {
        return onDeleteAction;
    }
        
}
