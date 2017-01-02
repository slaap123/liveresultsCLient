/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import gui.MainWindow;
import java.util.ArrayList;
import liveresultsclient.entity.Atleten;
import liveresultsclient.entity.Onderdelen;
import liveresultsclient.entity.StartLijsten;
import liveresultsclient.entity.Wedstrijden;

/**
 *
 * @author woutermkievit
 */
public class CreateFakeWedstrijd {

    public Wedstrijden wedstrijd;

    private HibernateSessionHandler sessionHandler;

    /**
     * constructor
     */
    public CreateFakeWedstrijd() {
        sessionHandler = HibernateSessionHandler.get();
        wedstrijd = sessionHandler.getObject(Wedstrijden.class, new Object[]{MainWindow.mainObj.wedstrijdId}, new String[]{"id"});
        int aantalOnderdelen = 4;
        int aantalSeries = 2;
        int aantalAtleten = 30;
        ArrayList<Onderdelen> onderdelen = createOnderdelen(aantalOnderdelen);
        ArrayList<Atleten> atelten = createAtleten(aantalAtleten);
        ArrayList<StartLijsten> startLijsten = createStartlijsten(aantalSeries,atelten,onderdelen);
    }

    private ArrayList<Onderdelen> createOnderdelen(int aantalOnderdelen) {
        ArrayList<Onderdelen> re = new ArrayList<Onderdelen>();
        String[] namen = {"60m", "80m", "100m", "600m"};
        String[] categorieen = {"MPB", "JPA2", "JJC", "Vrouwen"};
        for (int i = 0; i < aantalOnderdelen; i++) {
            Onderdelen onderdeel = new Onderdelen(wedstrijd, namen[(int) (Math.random() * 4)] + categorieen[(int) (Math.random() * 4)], "");
            re.add(onderdeel);
            sessionHandler.save(onderdeel);
                System.out.println("saved Onderdeel");
        }
        return re;
    }

    private ArrayList<Atleten> createAtleten(int aantalAtleten) {
        ArrayList<Atleten> re = new ArrayList<Atleten>();
        String[] verenigen = {"Phanos", "Aquila", "AAC", "AV23"};
        for (int i = 0; i < aantalAtleten; i++) {
            String[] persoon=getRandomNaam();
            Atleten atleet = new Atleten(wedstrijd, persoon[1], verenigen[(int) (Math.random() * 4)]);
            atleet.setGeslacht(persoon[0].charAt(0));
            atleet.setStartnummer(i+101);
            re.add(atleet);
            sessionHandler.save(atleet);
                System.out.println("saved Atleet");
            
        }
        return re;
    }

    private String[] getRandomNaam() {
        String[][] name = {{"F", "Yelena Balicki"},
        {"F", "Gisele Longworth"},
        {"F", "Kyong Crook"},
        {"F", "Ashely Orban"},
        {"M", "Jimmie Rosalez"},
        {"M", "Arnold Stennis"},
        {"F", "Clarinda Doughty"},
        {"F", "Imelda Nogueira"},
        {"M", "Roman Mahan"},
        {"F", "Marivel Roybal"},
        {"M", "Kristopher Ritts"},
        {"F", "Donella Dorrell"},
        {"F", "Thu Matthews"},
        {"F", "Sarita Mccourt"},
        {"F", "Reagan Tijerina"},
        {"F", "Kenisha Ewell"},
        {"F", "Ossie Stipe"},
        {"F", "Indira Clutts"},
        {"F", "Natacha Pasquale"},
        {"F", "Sheila Spitz"},
        {"F", "Tammy Mcmurray"},
        {"M", "Markus Cormier"},
        {"F", "Zenia Fernald"},
        {"M", "Sean Baptist"},
        {"F", "Vita Behne"},
        {"F", "Mikki Mone"},
        {"F", "Willa Racey"},
        {"F", "Glynis Stutler"},
        {"F", "Christina Fitzgibbon"},
        {"F", "Renate Lafreniere"},
        {"F", "Myong Robison"},
        {"F", "Sanjuanita Plaza"},
        {"F", "Star Ritacco"},
        {"M", "Bryant Byington"},
        {"F", "Angelia Beene"},
        {"F", "Latosha Diener"},
        {"F", "Lois Hardaway"},
        {"F", "Christia Paniagua"},
        {"M", "Lyle Roos"},
        {"F", "Flossie Firkins"},
        {"F", "Patty Friscia"},
        {"M", "Dewitt Coats"},
        {"M", "Otha Farnsworth"},
        {"F", "Rivka Dandy"},
        {"F", "Ruthie Trosclair"},
        {"F", "Marilu Arbogast"},
        {"F", "Deane Krzeminski"},
        {"F", "Loni Betterton"},
        {"M", "Filiberto Ennis"},
        {"M", "Leif Muldrow"}
        };
          return name[(int)(Math.random()*name.length)];    
    }

    private ArrayList<StartLijsten> createStartlijsten(int aantalSeries, ArrayList<Atleten> atleten, ArrayList<Onderdelen> onderdelen) {
        ArrayList<StartLijsten> re = new ArrayList<StartLijsten>();
        for (int o = 0; o < onderdelen.size(); o++) {
                ArrayList<Integer> aleetIs=new ArrayList<Integer>();
                for (int s = 0; s < aantalSeries; s++) {
                    for (int b = 0; b < 6; b++) {
                     
                int i;
                do{
                    i=(int)(Math.random()*atleten.size());
                }while(aleetIs.contains(i));
                aleetIs.add(i);
                StartLijsten startlijst=new StartLijsten(onderdelen.get(o), atleten.get(i), s+1, b+1,true);
                re.add(startlijst);
                sessionHandler.save(startlijst);
                System.out.println("saved startlijst");
                }
                }
                
        }
        return re;
    }
}
