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
public class PrimaryKeyConstraint extends TableConstraint {
    
    private static final long serialVersionUID = 1L;

    public PrimaryKeyConstraint(String name) {
        super(name);
        type = TableConstraint.PRIMARY_KEY_TYPE;
    }

}
