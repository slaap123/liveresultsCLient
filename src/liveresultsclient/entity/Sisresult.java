package liveresultsclient.entity;
// Generated Sep 19, 2016 7:42:13 PM by Hibernate Tools 4.3.1

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jssc.SerialPortList;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import utils.HibernateSessionHandler;
import utils.SerialHandler;
import utils.ResultsHandler;


/**
 * Sisresult generated by hbm2java
 */
public class Sisresult implements java.io.Serializable {

    private int idSisresult;
    private int startN;
    private int headN;
    private String raceName;
    private String starterName;
    private Set sislanes = new HashSet(0);
    public String rawData = null;
    public static Stack<Sisresult> starts = new Stack<Sisresult>();

    public Sisresult() {
    }

    public Sisresult(int idSisresult, int startN, int headN, String raceName, String starterName) {
        this.idSisresult = idSisresult;
        this.startN = startN;
        this.headN = headN;
        this.raceName = raceName;
        this.starterName = starterName;
    }

    public Sisresult(int idSisresult, int startN, int headN, String raceName, String starterName, Set sislanes) {
        this.idSisresult = idSisresult;
        this.startN = startN;
        this.headN = headN;
        this.raceName = raceName;
        this.starterName = starterName;
        this.sislanes = sislanes;
    }

    public int getIdSisresult() {
        return this.idSisresult;
    }

    public void setIdSisresult(int idSisresult) {
        this.idSisresult = idSisresult;
    }

    public int getStartN() {
        return this.startN;
    }

    public void setStartN(int startN) {
        this.startN = startN;
    }

    public int getHeadN() {
        return this.headN;
    }

    public void setHeadN(int headN) {
        this.headN = headN;
    }

    public String getRaceName() {
        return this.raceName;
    }

    public void setRaceName(String raceName) {
        this.raceName = raceName;
    }

    public String getStarterName() {
        return this.starterName;
    }

    public void setStarterName(String starterName) {
        this.starterName = starterName;
    }

    public Set getSislanes() {
        return this.sislanes;
    }
    public Sislane getSislane(int l) {
        for(Object lane : this.sislanes) {
            if(((Sislane)lane).getLane()==l){
                return (Sislane)lane;
            }
        }
        return null;
    }

    public void setSislanes(Set sislanes) {
        this.sislanes = sislanes;
    }

    public static void startHandler() {
        if (SerialPortList.getPortNames().length > 0) {
            SerialHandler serial = new SerialHandler(SerialPortList.getPortNames()[0] + "");
            serial.setHandleEvent(Sisresult.class, "handeleResult");
        }
    }
    static String buffer = "";

    public static void handeleResult(byte[] bytes) {
        buffer += new String(bytes);
        //System.out.println(buffer.replace("\n", "\\n\n").replace("\r", "\\n\r").replace("\t", "\\n\t"));

        if ((int) buffer.substring(buffer.length() - 1).charAt(0) == 3) {
            try {
                //System.out.print(buffer + "");
                System.out.println("found result");
                String lines[] = buffer.split("\r");
                if (lines[0].contains("Start number")) {
                    Sisresult result = new Sisresult();
                    result.startN = Integer.parseInt(lines[0].split(":")[1].trim().replace("sec", ""));

                    int lastLine = 0;
                    for (int i = 4; i < lines.length; i++) {
                        if (lines[i].contains("------")) {
                            lastLine = i + 1;
                            System.out.println("break" + i);
                            break;
                        }

                        Pattern p = Pattern.compile("(..)([ |\\d]\\d) (.....) (...)  (.....)   ");
                        Matcher m = p.matcher(lines[i]);
                        if (m.find()) {
                            System.out.println("FOUND LINE:");
                            Sislane sislane = new Sislane(m, result);
                            result.getSislanes().add(sislane);
                        }
                    }
                    if (lastLine < lines.length) {
                        System.out.println(lines[lastLine]);
                        result.setStarterName(lines[lastLine + 3].split(":")[1].trim());
                        result.headN = Integer.parseInt(lines[lastLine + 4].split(":")[1].trim());
                        result.raceName = lines[lastLine + 5].split(":")[1].trim();
                        starts.add(result);
                        
                        ResultsHandler.uitslagen.add(result);
                        for (Object lane : result.getSislanes()) {
                            ResultsHandler.uitslagen.add(lane);
                        }
                        
                        //result.print();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            buffer = buffer.substring(buffer.length());
        }
    }

}