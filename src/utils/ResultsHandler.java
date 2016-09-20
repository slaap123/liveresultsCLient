package utils;


import classes.ParFile;
import gui.AtletiekNuPanel;
import gui.MainWindow;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import liveresultsclient.entity.Atleten;
import liveresultsclient.entity.Img;
import liveresultsclient.entity.Onderdelen;
import liveresultsclient.entity.Serie;
import liveresultsclient.entity.Sisresult;
import liveresultsclient.entity.Uitslagen;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import utils.HibernateSessionHandler;
import utils.HibernateUtil;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author woutermkievit
 */
public class ResultsHandler extends Thread {

    private File dir;
    public static Queue uitslagen = new LinkedList();
    private ArrayList<ParFile> files = new ArrayList<ParFile>();
    private HibernateSessionHandler sessionHandler;

    public ResultsHandler(File dir) {
        this.dir = dir;
    }

    @Override
    public void run() {
        sessionHandler = HibernateSessionHandler.get();
        while (true) {
            System.out.println("start reading");
            for (File fileEntry : dir.listFiles()) {
                if (fileEntry.getName().endsWith("txt")) {
                    ParFile parFile = AtletiekNuPanel.panel.parFiles.get(fileEntry.getName().replace("txt", "par"));
                    if (parFile != null && parFile.resultSize != fileEntry.length()) {
                        readResults(fileEntry, parFile);
                        parFile.resultFile=fileEntry;
                        parFile.gotResults = true;
                        files.add(parFile);
                    }
                }
            }
            try {
                if(!AtletiekNuPanel.panel.test&&files.size()>0){
                    AtletiekNuPanel.panel.loginHandler.submitResults(files);
                }
                System.out.println("upload");
                files.clear();
            } catch (Exception ex) {
                System.out.println("failed to upload");
                Logger.getLogger(ResultsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(!AtletiekNuPanel.panel.test){
                AtletiekNuPanel.panel.UpdateList();
            }
            AtletiekNuPanel.panel.UpdateListFromLocal();
            System.out.println("end reading");
            //if(!AtletiekNuPanel.panel.test){
                SaveNewRecords();
            //}
            try {
                sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ResultsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    private void readResults(File file, ParFile parFile) {
        parFile.resultSize = file.length();
        String content = null;
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(ParFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(ParFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        Onderdelen onderdeel = sessionHandler.getObject(Onderdelen.class, new Object[]{parFile.startlijst_onderdeel_id}, new String[]{"externalId"});
        int serieNr = Integer.parseInt(parFile.serie.replaceAll("[\n\r ]", ""));
        Serie serie = sessionHandler.getObject(Serie.class, new Object[]{onderdeel, serieNr}, new String[]{"onderdelen", "serieNummer"});

        File jpg = new File(file.getAbsolutePath().replace("txt", "jpg"));
        Img foto = null;
        if (jpg.exists()) {
            if (serie != null) {
                foto = serie.getImg();
            }
            if(foto==null){
                foto = new Img();
            }
            try {
                foto.setContent(Files.readAllBytes(Paths.get(jpg.getPath())));
                foto.setName(jpg.getName());
                foto.setSize((int) jpg.length());
                foto.setType("application/octet-stream");
                foto.setUploadDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            } catch (IOException ex) {
                Logger.getLogger(ResultsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(!AtletiekNuPanel.panel.test){
                uitslagen.add(foto);
            }
        }
        if (serie == null) {
            serie = new Serie();
        }
        ArrayList<String> lines = new ArrayList<String>(Arrays.asList(content.split("\n")));
        lines=CheckResultsFile(file, parFile, lines);
        serie.setWind(lines.get(0).split("\t")[1]);
        serie.setImg(foto);
        serie.setOnderdelen(onderdeel);
        serie.setSerieNummer(serieNr);
        
        if(!Sisresult.starts.empty()&&parFile.startInformatie==null){
            parFile.startInformatie=Sisresult.starts.pop();
        }
        if(!AtletiekNuPanel.panel.test){
            uitslagen.add(serie);
        }
        int atleten = 0;
        for (int i = 8; i < lines.size(); i++) {
            String[] split = lines.get(i).replaceAll("\n\r", "").split("\t");
            Uitslagen uitslag;
            if (split.length >= 4 && split[3].length() > 0) {
                int baan = 0;
                if (split[1].length() > 0) {
                    baan = Integer.parseInt(split[1]);
                }
                uitslag = sessionHandler.getObject(Uitslagen.class, new Object[]{serieNr, onderdeel.getId(), Integer.parseInt(split[3])}, new String[]{"serieNummer", "onderdelen.id", "atleten.startnummer"});

                if (uitslag == null) {
                    uitslag = new Uitslagen();
                }
                uitslag.setOpmerking("");
                uitslag.setBaan(baan);
                uitslag.setResultaat(split[2]);
                Atleten atleet = sessionHandler.getObject(Atleten.class, new Object[]{Integer.parseInt(split[3]), MainWindow.mainObj.wedstrijdId}, new String[]{"startnummer", "wedstrijden.id"});
                uitslag.setAtleten(atleet);
                uitslag.setInvoerTijd(new Date());
                uitslag.setSerieNummer(serieNr);
                uitslag.setOnderdelen(onderdeel);
                if(parFile.startInformatie!=null){
                    uitslag.setReactieTijd(parFile.startInformatie.getSislane(baan).getReactionTime()+"");
                    AtletiekNuPanel.panel.jTextPane1.setText("reactie tijd :"+uitslag.getReactieTijd());
                }
                if(!AtletiekNuPanel.panel.test){
                    uitslagen.add(uitslag);
                }
                atleten++;
            }
        }

        String text = AtletiekNuPanel.panel.jTextPane1.getText();
        AtletiekNuPanel.panel.jTextPane1.setText(file.getName() + " met " + atleten + " atleten\n" + text);
    }

    private void SaveNewRecords() {
        while (!uitslagen.isEmpty()) {
            try {
                System.out.println("saving: " + uitslagen.peek().getClass().getName());
                sessionHandler.save(uitslagen.poll());
            } catch (HibernateException he) {
                he.printStackTrace();
            }catch (AssertionFailure he){
                he.printStackTrace();
            }
        }
    }

    private ArrayList<String> CheckResultsFile(File file, ParFile parFile, ArrayList<String> lines) {
        if (!lines.get(1).startsWith("#")) {
            ArrayList<String> newLines = new ArrayList<String>();
            newLines.addAll(lines);
            newLines.addAll(1, parFile.getHeaderInfo());
            FileWriter writer = null;
            
            try {
                writer = new FileWriter(file.getAbsolutePath());
                for (int i = 0; i < newLines.size(); i++) {
                    if(i>0)
                        writer.append(System.getProperty("line.separator"));
                    writer.append(newLines.get(i));
                }
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(ResultsHandler.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(ResultsHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            file = new File(file.getAbsolutePath());
            parFile.resultSize = file.length();
            lines=newLines;
        }
        return lines;
    }
}
