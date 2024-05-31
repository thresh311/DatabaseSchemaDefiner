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
public class UniqueKeyConstraint extends TableConstraint {
    
    public UniqueKeyConstraint(String name) {
        super(name);
        type = TableConstraint.UNIQUE_TYPE;
    }
    
}
