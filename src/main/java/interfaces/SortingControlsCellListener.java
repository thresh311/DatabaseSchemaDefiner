/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

/**
 *
 * @author juanv
 */
public interface SortingControlsCellListener {
    
    public void onSendToTopClicked(int row);
    
    public void onSendToBottomClicked(int row);
    
    public void onSendUpClicked(int row);
    
    public void onSendDownClicked(int row);
    
}
