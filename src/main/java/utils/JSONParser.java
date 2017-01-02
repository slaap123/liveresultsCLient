/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author woutermkievit
 */
public class JSONParser {
    public JSONParser parent=null;
    public HashMap<String,Object> Objects=new HashMap<String,Object>();
    public JSONParser(String jsonS){
        this.parse(jsonS);
    }
    public JSONParser(String jsonS,JSONParser parent){
        this.parent=parent;
        this.parse(jsonS);
    }
    static Object parse(String jsonS){
        Stack walker=new Stack();
        int objStart=jsonS.indexOf('{');
        int arrayStart=jsonS.indexOf('[');
        int nextElementStart=jsonS.indexOf(',');
        int objEnd=jsonS.indexOf('}');
        int arrayEnd=jsonS.indexOf(']');
        Object returnObject;
        
        
        return null;
    }
    private int min(int... numbers){
        int min=Integer.MAX_VALUE;
        //for(String arg: numbers) {
		//	System.out.print(", " + arg);
		//}
        return 0;
        
    }
}
