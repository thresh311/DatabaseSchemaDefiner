/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.ColumnsTable;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author juanv
 */
public class IconButton extends JButton {

    public IconButton() {
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(3, 3, 3, 3));
//        addComponentListener(new ComponentAdapter() {
//                        
//                        @Override
//                        public void componentResized(ComponentEvent e) {
//                            JButton btn = (JButton) e.getComponent();
//                            Dimension size = btn.getSize();
//                            Insets insets = btn.getInsets();
//                            size.width -= insets.left + insets.right;
//                            size.height -= insets.top + insets.bottom;
//                            if(btn.getIcon() == null || !(btn.getIcon() instanceof SvgIcon)) return;
//                            SvgIcon icon = ((SvgIcon) btn.getIcon());
//                            int ratio = icon.getIconWidth()/icon.getIconHeight();
//                            if (size.width > size.height) {
//                                size.width = size.height * ratio;
//                            } else {
//                                size.height = size.width / ratio;
//                            }
//                                                        
//                            SvgIcon scaled = new SvgIcon(((SvgIcon) btn.getIcon()).getSvgUrl(), size.width, size.height);
//                            btn.setIcon(scaled);
//                        }
//                        
//                    });
    }

//    @Override
//    public Icon getIcon() {
//        ImageIcon defaultIcon = (ImageIcon) super.getIcon();
//        
//        if(defaultIcon != null) {
//            Image newImg = defaultIcon.getImage().getScaledInstance(getWidth(), getHeight(),  java.awt.Image.SCALE_SMOOTH );
//            defaultIcon.setImage(newImg);
//            
//        }      
//
//        return defaultIcon;
//    }
}
