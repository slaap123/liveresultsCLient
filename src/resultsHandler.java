
import classes.ParFile;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import liveresultsclient.entity.Atleten;
import liveresultsclient.entity.Img;
import liveresultsclient.entity.Onderdelen;
import liveresultsclient.entity.Serie;
import liveresultsclient.entity.Uitslagen;
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
public class resultsHandler extends Thread {

    

    private File dir;
    private Queue uitslagen = new LinkedList();
    private ArrayList<File> files = new ArrayList<File>();
    private HibernateSessionHandler sessionHandler;
    public resultsHandler(File dir) {
        this.dir = dir;
    }

    @Override
    public void run() {
        sessionHandler=HibernateSessionHandler.get();
        while (true) {
            System.out.println("start reading");
            for (File fileEntry : dir.listFiles()) {
                if (fileEntry.getName().endsWith("txt")) {
                    ParFile parFile = AtletiekNuPanel.panel.parFiles.get(fileEntry.getName().replace("txt", "par"));
                    if (parFile != null && parFile.resultSize != fileEntry.length()) {
                        readResults(fileEntry, parFile);
                        parFile.gotResults = true;
                        files.add(fileEntry);
                    }
                }
            }
            try {
                AtletiekNuPanel.panel.loginHandler.submitResults(files);
                files.clear();
            } catch (Exception ex) {
                Logger.getLogger(resultsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            AtletiekNuPanel.panel.UpdateList();
            System.out.println("end reading");
            try {
                sleep(30000);
            } catch (InterruptedException ex) {
                Logger.getLogger(resultsHandler.class.getName()).log(Level.SEVERE, null, ex);
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
        Onderdelen onderdeel = sessionHandler.getObject(Onderdelen.class,new Object[] {parFile.startlijst_onderdeel_id}, new String[]{"externalId"});
        int serieNr=Integer.parseInt(parFile.serie.replaceAll("[\n\r ]", ""));
        Serie serie = sessionHandler.getObject(Serie.class,new Object[] {onderdeel,serieNr} , new String[]{"onderdelen","serieNummer"});
        
        File jpg = new File(file.getAbsolutePath().replace("txt", "jpg"));
        Img foto=null;
        if (jpg.exists()) {
            if(serie==null){
                foto=new Img();
            }else
                foto = serie.getImg();
            try {
                foto.setContent(Files.readAllBytes(Paths.get(jpg.getPath())));
                foto.setName(jpg.getName());
                foto.setSize((int) jpg.length());
                foto.setType("application/octet-stream");
                foto.setUploadDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            } catch (IOException ex) {
                Logger.getLogger(resultsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            uitslagen.add(foto);
        }
        if(serie==null){
            serie=new Serie();
        }
        String[] lines = content.split("\n");
        serie.setWind(lines[0].split("\t")[1]);
        serie.setImg(foto);
        serie.setOnderdelen(onderdeel);
        serie.setSerieNummer(serieNr);
        uitslagen.add(serie);
        int atleten = 0;
        for (int i = 8; i < lines.length; i++) {
            String[] split = lines[i].split("\t");
            Uitslagen uitslag= sessionHandler.getObject(Uitslagen.class,new Object[]{serieNr,onderdeel.getId(),Integer.parseInt(split[1])}, new String[]{"serieNummer","onderdelen.id","baan"});
            if(uitslag==null){
                uitslag = new Uitslagen();
            }
            uitslag.setOpmerking("");
            uitslag.setBaan(Integer.parseInt(split[1]));
            uitslag.setResultaat(split[2]);
            Atleten atleet = sessionHandler.getObject(Atleten.class,new Object[]{Integer.parseInt(split[3]),MainWindow.mainObj.wedstrijdId}, new String[]{"startnummer","wedstrijden.id"});
            uitslag.setAtleten(atleet);
            uitslag.setInvoerTijd(new Date());
            uitslag.setSerieNummer(serieNr);
            uitslag.setOnderdelen(onderdeel);
            uitslagen.add(uitslag);
            atleten++;
        }
        String text = AtletiekNuPanel.panel.jTextPane1.getText();
        AtletiekNuPanel.panel.jTextPane1.setText(file.getName() + " met " + atleten + " atleten\n" + text);
        SaveNewRecords();
    }

    private void SaveNewRecords() {
        while (!uitslagen.isEmpty()) {
            try {
                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                System.out.println("saving: "+uitslagen.peek().getClass().getName());
                session.saveOrUpdate(uitslagen.poll());
                session.getTransaction().commit();
            } catch (HibernateException he) {
                he.printStackTrace();
            }
        }
    }
}
