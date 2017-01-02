package liveresultsclient.entity;
// Generated May 8, 2016 11:03:12 AM by Hibernate Tools 3.6.0


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Atleten generated by hbm2java
 */
public class Atleten  implements java.io.Serializable {


     private Integer id;
     private Wedstrijden wedstrijden;
     private Integer startnummer;
     private String naam;
     private Character geslacht;
     private Date geb;
     private String vereniging;
     private Set uitslagens = new HashSet(0);
     private Set startLijstens = new HashSet(0);

    public Atleten() {
    }
	
    public Atleten(Wedstrijden wedstrijden, String naam, String vereniging) {
        this.wedstrijden = wedstrijden;
        this.naam = naam;
        this.vereniging = vereniging;
    }
    public Atleten(Wedstrijden wedstrijden, Integer startnummer, String naam, Character geslacht, Date geb, String vereniging, Set uitslagens, Set startLijstens) {
       this.wedstrijden = wedstrijden;
       this.startnummer = startnummer;
       this.naam = naam;
       this.geslacht = geslacht;
       this.geb = geb;
       this.vereniging = vereniging;
       this.uitslagens = uitslagens;
       this.startLijstens = startLijstens;
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
    public Integer getStartnummer() {
        return this.startnummer;
    }
    
    public void setStartnummer(Integer startnummer) {
        this.startnummer = startnummer;
    }
    public String getNaam() {
        return this.naam;
    }
    
    public void setNaam(String naam) {
        this.naam = naam;
    }
    public Character getGeslacht() {
        return this.geslacht;
    }
    
    public void setGeslacht(Character geslacht) {
        this.geslacht = geslacht;
    }
    public Date getGeb() {
        return this.geb;
    }
    
    public void setGeb(Date geb) {
        this.geb = geb;
    }
    public String getVereniging() {
        return this.vereniging;
    }
    
    public void setVereniging(String vereniging) {
        this.vereniging = vereniging;
    }
    public Set getUitslagens() {
        return this.uitslagens;
    }
    
    public void setUitslagens(Set uitslagens) {
        this.uitslagens = uitslagens;
    }
    public Set getStartLijstens() {
        return this.startLijstens;
    }
    
    public void setStartLijstens(Set startLijstens) {
        this.startLijstens = startLijstens;
    }




}


