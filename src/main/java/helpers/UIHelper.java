/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import javax.swing.JComponent;

/**
 *
 * @author juanv
 */
public abstract class UIHelper {
    
    
    public static void swapComponents(
            JComponent ui, 
            JComponent current,
            JComponent next) {
        ui.remove(current);
        ui.add(next);
        ui.revalidate();
    }
    
}
