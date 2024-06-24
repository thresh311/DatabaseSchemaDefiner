/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author juanv
 */
public class Column implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private UUID id;
    private String name;
    private int ordinalPosition;
    private boolean isNullable;
    private String defaultValue;
    private String type;
    private String collationName;
    private String comment;
    //https://dev.mysql.com/doc/refman/8.0/en/information-schema-columns-table.html
    private String extra;

    public Column() {
        this.id = UUID.randomUUID();
        this.name = "";
        this.defaultValue = "";
        this.type = "";
        this.collationName = "";
        this.comment = "";
        this.extra = "";
    }
    
    public Column(String name, Integer ordinalPosition, Boolean isNullable, String defaultValue, String type, String collationName, String comment, String extra) {
        this();
        this.name = name;
        this.ordinalPosition = ordinalPosition;
        this.isNullable = isNullable;
        this.defaultValue = defaultValue;
        this.type = type;
        this.collationName = collationName;
        this.comment = comment;
        this.extra = extra;
    }
       
    @Override
    public Column clone() {
        Column newColumn = new Column();
        
        newColumn.id = id;
        newColumn.name = name;
        newColumn.ordinalPosition = ordinalPosition;
        newColumn.isNullable = isNullable;
        newColumn.defaultValue = defaultValue;
        newColumn.type = type;
        newColumn.collationName = collationName;
        newColumn.comment = comment;
        newColumn.extra = extra;
    
        return newColumn;
    }
         
    public Boolean isAutoIncremental() {
        return extra.equals("auto_increment");
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrdinalPosition(Integer ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public void setIsNullable(Boolean isNullable) {
        this.isNullable = isNullable;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCollationName(String collationName) {
        this.collationName = collationName;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
    
    

    public UUID getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public Integer getOrdinalPosition() {
        return ordinalPosition;
    }

    public Boolean getIsNullable() {
        return isNullable;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getType() {
        return type;
    }

    public String getCollationName() {
        return collationName;
    }

    public String getComment() {
        return comment;
    }

    public String getExtra() {
        return extra;
    }
    
        
}
