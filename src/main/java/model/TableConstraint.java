/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author juanv
 */
public abstract class TableConstraint implements Serializable {
    
    public final static String UNIQUE_TYPE = "UNIQUE";
    public final static String PRIMARY_KEY_TYPE = "PRIMARY KEY";
    public final static String FOREIGN_KEY_TYPE = "FOREIGN KEY";
    public final static String CHECK_TYPE = "CHECK";
    
    protected String name;
    protected String type;
    protected Map<String, Integer> columnsOrdinalPositions;
    
    public TableConstraint(String name) {
        this.name = name;
        columnsOrdinalPositions = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Map<String, Integer> getColumnsOrdinalPositions() {
        return columnsOrdinalPositions;
    }
    
    public Set<String> getColumnNames() {
        return columnsOrdinalPositions.keySet();
    }
        
}
