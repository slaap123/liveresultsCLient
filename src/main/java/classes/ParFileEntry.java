/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author woutermkievit
 */
public class ParFileEntry {
    String startnummer;
    String baan;
    String naam;
    String info;

    ParFileEntry(String[] params) {
        try{
        startnummer=params[0];
        baan=params[1];
        naam=params[2];
        info=params[3];
        }catch(ArrayIndexOutOfBoundsException ex){
            System.out.println(Arrays.toString(params));
            Logger.getLogger(ParFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
