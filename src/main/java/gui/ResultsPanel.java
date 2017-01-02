/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import classes.ParFile;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JTextPane;

/**
 *
 * @author woutermkievit
 */
public class ResultsPanel extends JPanel{
    
    public final static boolean test = false;
    public final static boolean live = true;
    
    
    public javax.swing.JTextPane jTextPane1 = new JTextPane();
    
    public HashMap<String, ParFile> parFiles = new HashMap<String, ParFile>();
    
    public static ResultsPanel panel;
    
    public static String indentifingColumnName;
    
    public HashMap<String, ParFile> GetparFiles(){
        return parFiles;
    }
    
    public void UpdateList(){
    }
}
