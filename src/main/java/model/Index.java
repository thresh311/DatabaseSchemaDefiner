/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author juanv
 */
public class Index implements Serializable {
    
    protected String name;
    protected Boolean unique;
    protected String comment;
    protected Map<String, Integer> columnsOrdinalPositions;
    
    public Index(String name, Boolean unique, String comment) {
        this.name = name;
        this.unique = unique;
        this.comment = comment;
        columnsOrdinalPositions = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Integer> getColumnsOrdinalPositions() {
        return columnsOrdinalPositions;
    }

    public void setColumnsOrdinalPositions(Map<String, Integer> columnsOrdinalPositions) {
        this.columnsOrdinalPositions = columnsOrdinalPositions;
    }
        
}
