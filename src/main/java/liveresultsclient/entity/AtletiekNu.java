package liveresultsclient.entity;
// Generated May 8, 2016 11:03:12 AM by Hibernate Tools 3.6.0



/**
 * AtletiekNu generated by hbm2java
 */
public class AtletiekNu  implements java.io.Serializable {


     private Integer id;
     private Wedstrijden wedstrijden;
     private int nuid;

    public AtletiekNu() {
    }

    public AtletiekNu(Wedstrijden wedstrijden, int nuid) {
       this.wedstrijden = wedstrijden;
       this.nuid = nuid;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Wedstrijden getWedstrijden() {
        return this.wedstrijden;
    }
    
    public void setWedstrijden(Wedstrijden wedstrijden) {
        this.wedstrijden = wedstrijden;
    }
    public int getNuid() {
        return this.nuid;
    }
    
    public void setNuid(int nuid) {
        this.nuid = nuid;
    }




}


