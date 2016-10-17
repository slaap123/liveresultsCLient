package gui;

import classes.LoginHandler;
import utils.ResultsHandler;
import classes.ParFile;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import liveresultsclient.entity.Onderdelen;
import liveresultsclient.entity.StartLijsten;
import utils.HibernateSessionHandler;
import utils.UnzipUtility;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author woutermkievit
 */
public class LiveResultsPanel extends ResultsPanel {

    private javax.swing.JSplitPane jSplitPane1 = new javax.swing.JSplitPane();
    private javax.swing.JTable parFileNames = new JTable();
    private javax.swing.JScrollPane jScrollPane4 = new JScrollPane();
    private javax.swing.JScrollPane jScrollPane5 = new JScrollPane();
    private JTabbedPane tPane;
    public LoginHandler loginHandler;
    private UnzipUtility unzip;
    private File baseDir;

    public static ResultsPanel GetAtletiekNuPanel(final JTabbedPane pane) {
        if (panel == null) {
            panel = new LiveResultsPanel(pane);
            indentifingColumnName = "id";
        }
        return panel;
    }

    private LiveResultsPanel(final JTabbedPane pane) {

        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        FileDialog dialog = new FileDialog(MainWindow.mainObj);
        dialog.setMultipleMode(false);
        dialog.setVisible(true);
        System.setProperty("apple.awt.fileDialogForDirectories", "false");
        baseDir = dialog.getFiles()[0];
        if (!baseDir.isDirectory()) {
            baseDir = baseDir.getParentFile();
        }

        tPane = pane;
        parFileNames.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "parfile", "onderdeel", "serie"
                }
        ) {
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane4.setViewportView(parFileNames);

        jSplitPane1.setLeftComponent(jScrollPane4);

        jScrollPane5.setViewportView(jTextPane1);

        jSplitPane1.setRightComponent(jScrollPane5);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(this);
        this.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane1)
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
        );

        tPane.addTab("LiveResults", this);
        unzip = new UnzipUtility();
        try {
            if (!panel.test) {
                UpdateListRemote();
            } else {
                UpdateListFromLocal();
            }

        } catch (Exception ex) {
            Logger.getLogger(LiveResultsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultsHandler handelr = new ResultsHandler(baseDir);
        handelr.start();
    }

    @Override
    public void UpdateList() {
        try {
            if (!panel.test) {
                UpdateListRemote();
            } else {
                UpdateListFromLocal();
            }

        } catch (Exception ex) {
            Logger.getLogger(LiveResultsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void UpdateListRemote() {
        try {
            getParFiles();
            ((DefaultTableModel) parFileNames.getModel()).setRowCount(0);
            for (File fileEntry : baseDir.listFiles()) {
                if (fileEntry.getName().endsWith("par")) {
                    ParFile entry = null;
                    if (parFiles.containsKey(fileEntry.getName())) {
                        entry = parFiles.get(fileEntry.getName());
                        entry.getValuesFromFile(fileEntry);
                    } else {
                        entry = new ParFile(fileEntry);
                    }
                    System.out.println("GotResults:" + entry.gotResults);
                    if (!entry.gotResults) {
                        ((DefaultTableModel) parFileNames.getModel()).addRow(new Object[]{entry.fileName, entry.onderdeel + " " + entry.startgroep, entry.serie});
                    }
                    parFiles.put(fileEntry.getName(), entry);
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(LiveResultsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void UpdateListFromLocal() {
        try {
            ((DefaultTableModel) parFileNames.getModel()).setRowCount(0);
            for (File fileEntry : baseDir.listFiles()) {
                if (fileEntry.getName().endsWith("par")) {
                    ParFile entry = null;
                    if (parFiles.containsKey(fileEntry.getName())) {
                        entry = parFiles.get(fileEntry.getName());
                        entry.getValuesFromFile(fileEntry);
                    } else {
                        entry = new ParFile(fileEntry);
                    }
                    System.out.println("GotResults:" + entry.gotResults);
                    if (!entry.gotResults) {
                        ((DefaultTableModel) parFileNames.getModel()).addRow(new Object[]{entry.fileName, entry.onderdeel + " " + entry.startgroep, entry.serie});
                    }
                    parFiles.put(fileEntry.getName(), entry);
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(LiveResultsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void getParFiles() {
        int i = 1;
        for (Onderdelen onderdeel : MainWindow.mainObj.wedstrijdOnderdelen) {
            TreeSet<StartLijsten> set = new TreeSet<StartLijsten>(new Comparator<StartLijsten>() {
                public int compare(StartLijsten one, StartLijsten other) {
                    return one.getSerie() > other.getSerie() ? +1 : one.getSerie() < other.getSerie() ? -1 : one.getBaan() > other.getBaan() ? +1 : one.getBaan() < other.getBaan() ? -1 : 0;
                }
            });
            Set startLijstens = onderdeel.getStartLijstens();
            set.addAll(startLijstens);
            PrintWriter writer = null;
            int lastSerie = -1;
            boolean open = false;
            for (StartLijsten startlijst : set) {
                try {
                    if (lastSerie != startlijst.getSerie()) {
                        if (open) {
                            writer.close();
                            i++;
                        }
                        open = true;
                        lastSerie = startlijst.getSerie();
                        writer = new PrintWriter(baseDir.getAbsolutePath() + String.format("/%03d.par", i), "UTF-8");
                        writer.printf("# startlijst_onderdeel_id:%d\n", onderdeel.getId());
                        writer.printf("# Versie:\t\t1\n");
                        writer.printf("# Begin Tijd:\t%tY-%<tm-%<td_%<tH%<tMu\n", onderdeel.getTijd());
                        writer.printf("# Startgroep:\t%s\n", onderdeel.getCategorie());
                        writer.printf("# Onderdeel:\t%s\n", onderdeel.getOnderdeel());
                        writer.printf("# Serie:\t\t%s\n", startlijst.getSerie());
                    }
                    writer.printf("%d\t%d\t%s (%s)\t%s\n", startlijst.getAtleten().getStartnummer(), startlijst.getBaan(), startlijst.getAtleten().getNaam(), startlijst.getAtleten().getVereniging(), startlijst.getOnderdelen().getRaceName());

                    /*
# startlijst_onderdeel_id:31677201
# Versie:		2
# Begin Tijd:	2016-09-04_1100u
# Startgroep:	MJD1
# Onderdeel:	60mH
# Serie:		1	
287	1	Belle  Schyns (PHNX)	MJD1 60mH serie 1 versie 2
                     */
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(LiveResultsPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(LiveResultsPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (open) {
                writer.close();
                i++;
            }
        }
    }

}
