package gui;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class MenuBuilder {

    public MenuBuilder(){

    }

    public static JMenu createJMenu(String name,String description,JMenuItem... args){
        JMenu jMenu = new JMenu(name);
        jMenu.getAccessibleContext().setAccessibleDescription((description));
        jMenu.setMnemonic(KeyEvent.VK_V);

        if(args != null){
            for( JMenuItem el : args){
                jMenu.add(el);
            }
        }

        return jMenu;
    }

    public static JMenuItem createJMenuItem(String text,Runnable action){
        JMenuItem jMenuItem = new JMenuItem(text);

        if(action != null){
            jMenuItem.addActionListener((event) -> action.run());
        }

        return jMenuItem;
    }


}
