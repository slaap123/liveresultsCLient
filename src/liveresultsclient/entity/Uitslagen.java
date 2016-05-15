package liveresultsclient.entity;
// Generated May 8, 2016 11:03:12 AM by Hibernate Tools 3.6.0


import java.util.Date;

/**
 * Uitslagen generated by hbm2java
 */
public class Uitslagen  implements java.io.Serializable {


     private Integer id;
     private Onderdelen onderdelen;
     private Atleten atleten;
     private Integer serieNummer;
     private Integer baan;
     private String categorie;
     private String resultaat;
     private String opmerking;
     private Date invoerTijd;
     private String reactieTijd;

    public Uitslagen() {
    }

	
    public Uitslagen(Onderdelen onderdelen, Atleten atleten, String resultaat, String opmerking, Date invoerTijd) {
        this.onderdelen = onderdelen;
        this.atleten = atleten;
        this.resultaat = resultaat;
        this.opmerking = opmerking;
        this.invoerTijd = invoerTijd;
    }
    public Uitslagen(Onderdelen onderdelen, Atleten atleten, Integer serieNummer, Integer baan, String categorie, String resultaat, String opmerking, Date invoerTijd, String reactieTijd) {
       this.onderdelen = onderdelen;
       this.atleten = atleten;
       this.serieNummer = serieNummer;
       this.baan = baan;
       this.categorie = categorie;
       this.resultaat = resultaat;
       this.opmerking = opmerking;
       this.invoerTijd = invoerTijd;
       this.reactieTijd = reactieTijd;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Onderdelen getOnderdelen() {
        return this.onderdelen;
    }
    
    public void setOnderdelen(Onderdelen onderdelen) {
        this.onderdelen = onderdelen;
    }
    public Atleten getAtleten() {
        return this.atleten;
    }
    
    public void setAtleten(Atleten atleten) {
        this.atleten = atleten;
    }
    public Integer getSerieNummer() {
        return this.serieNummer;
    }
    
    public void setSerieNummer(Integer serieNummer) {
        this.serieNummer = serieNummer;
    }
    public Integer getBaan() {
        return this.baan;
    }
    
    public void setBaan(Integer baan) {
        this.baan = baan;
    }
    public String getCategorie() {
        return this.categorie;
    }
    
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    public String getResultaat() {
        return this.resultaat;
    }
    
    public void setResultaat(String resultaat) {
        this.resultaat = resultaat;
    }
    public String getOpmerking() {
        return this.opmerking;
    }
    
    public void setOpmerking(String opmerking) {
        this.opmerking = opmerking;
    }
    public Date getInvoerTijd() {
        return this.invoerTijd;
    }
    
    public void setInvoerTijd(Date invoerTijd) {
        this.invoerTijd = invoerTijd;
    }
    public String getReactieTijd() {
        return this.reactieTijd;
    }
    
    public void setReactieTijd(String reactieTijd) {
        this.reactieTijd = reactieTijd;
    }




}


