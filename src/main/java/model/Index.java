/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author juanv
 */
public class Index {
    
    protected String name;
    protected Map<String, Integer> columnsOrdinalPositions;
    
    public Index(String name) {
        this.name = name;
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
