/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author juanv
 */
public class Column {
    
    private String name;
    private Integer ordinalPosition;
    private Boolean isNullable;
    private String defaultValue;
    private String type;
    private String collationName;
    private String comment;
    //https://dev.mysql.com/doc/refman/8.0/en/information-schema-columns-table.html
    private String extra;

    public Column(String name, Integer ordinalPosition, Boolean isNullable, String defaultValue, String type, String collationName, String comment, String extra) {
        this.name = name;
        this.ordinalPosition = ordinalPosition;
        this.isNullable = isNullable;
        this.defaultValue = defaultValue;
        this.type = type;
        this.collationName = collationName;
        this.comment = comment;
        this.extra = extra;
    }
    
    public Boolean isAutoIncremental() {
        return extra.equals("auto_increment");
    }
    
}
