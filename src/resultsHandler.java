
import utils.HibernateUtil;
import classes.ParFile;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import liveresultsclient.entity.Atleten;
import liveresultsclient.entity.Onderdelen;
import liveresultsclient.entity.Uitslagen;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import utils.HibernateSessionHandler;

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

    public static void main(String args[]) {
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        JFrame frame;
        frame = new JFrame();
        frame.setVisible(true);
        FileDialog dialog = new FileDialog(frame);
        dialog.setMultipleMode(false);
        dialog.setVisible(true);
        System.setProperty("apple.awt.fileDialogForDirectories", "false");
        File baseDir = dialog.getFiles()[0];
        if (!baseDir.isDirectory()) {
            baseDir = baseDir.getParentFile();
        }
        java.awt.EventQueue.invokeLater(new resultsHandler(baseDir));
    }

    private File dir;
    private Stack uitslagen = new Stack();
    private ArrayList<File> files = new ArrayList<File>();

    public resultsHandler(File dir) {
        this.dir = dir;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("start reading");
            for (File fileEntry : dir.listFiles()) {
                if (fileEntry.getName().endsWith("txt")) {
                    ParFile parFile = AtletiekNuPanel.panel.parFiles.get(fileEntry.getName().replace("txt", "par"));
                    if (parFile != null&&parFile.resultSize!=fileEntry.length()) {
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
            System.out.println("end reading");
            try {
                sleep(30000);
            } catch (InterruptedException ex) {
                Logger.getLogger(resultsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }

    }

    private void readResults(File file, ParFile parFile) {
        parFile.resultSize=file.length();
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
        String[] lines = content.split("\n");
        for (int i = 8; i < lines.length; i++) {
            String[] split = lines[i].split("\t");
            Uitslagen uitslag = new Uitslagen();
            uitslag.setBaan(Integer.parseInt(split[1]));
            uitslag.setResultaat(split[2]);
            Atleten atleet = (Atleten) HibernateSessionHandler.get().getObject(split[3], "startnummer", "Atleten");
            uitslag.setAtleten(atleet);
            uitslag.setInvoerTijd(new Date());
            System.out.println(Integer.parseInt(parFile.serie.replaceAll("[\n\r ]", "")));
            uitslag.setSerieNummer(Integer.parseInt(parFile.serie.replaceAll("[\n\r ]", "")));
            uitslag.setOnderdelen((Onderdelen) HibernateSessionHandler.get().getObject(parFile.startlijst_onderdeel_id, "externalId", "Onderdelen"));
            uitslagen.push(uitslag);
        }
        
        SaveNewRecords();
        String text=AtletiekNuPanel.panel.jTextPane1.getText();
        AtletiekNuPanel.panel.jTextPane1.setText(file.getName()+" met "+uitslagen.size()+" atleten\n"+text);
    }

    private void SaveNewRecords() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            while (!uitslagen.isEmpty()) {
                session.save(uitslagen.pop());
            }
            session.getTransaction().commit();
        } catch (HibernateException he) {
            he.printStackTrace();
        }
    }
}
