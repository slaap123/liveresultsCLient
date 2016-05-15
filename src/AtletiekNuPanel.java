
import classes.ParFile;
import java.awt.FileDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
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
public class AtletiekNuPanel extends JPanel {

    private javax.swing.JSplitPane jSplitPane1 = new javax.swing.JSplitPane();
    public javax.swing.JTextPane jTextPane1=new JTextPane();
    private javax.swing.JTable parFileNames=new JTable();
    private javax.swing.JScrollPane jScrollPane4=new JScrollPane();
    private javax.swing.JScrollPane jScrollPane5=new JScrollPane();
    private JTabbedPane tPane;
    public HashMap<String,ParFile> parFiles=new HashMap<String, ParFile>();
    public LoginHandler loginHandler;
    private UnzipUtility unzip;
    private String nuid;
    private File baseDir;
    
    public static AtletiekNuPanel panel;

    public static AtletiekNuPanel GetAtletiekNuPanel(final JTabbedPane pane,int nuid) {
        panel=new AtletiekNuPanel(pane,nuid);
        return panel;
    }
    private AtletiekNuPanel(final JTabbedPane pane,int nuid) {
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        FileDialog dialog = new FileDialog(MainWindow.mainObj);
        dialog.setMultipleMode(false);
        dialog.setVisible(true);
        System.setProperty("apple.awt.fileDialogForDirectories", "false");
        baseDir=dialog.getFiles()[0];
        if(!baseDir.isDirectory()){
            baseDir=baseDir.getParentFile();
        }
        tPane = pane;
        this.nuid=nuid+"";
        parFileNames.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "parfile", "onderdeel", "serie"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
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

        tPane.addTab("AteltiekNu", this);
        unzip = new UnzipUtility();
        try {
            loginHandler = new LoginHandler(this.nuid);
            UpdateList();

        } catch (Exception ex) {
            Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        resultsHandler handelr = new resultsHandler(baseDir);
        handelr.start();
    }

    private void UpdateList() {
        try {
            loginHandler.getZip(MainWindow.mainObj.wedstrijdId + "");
            unzip.unzip("tmp.zip", baseDir.getPath());
            new File("tmp.zip").delete();
            
            for (File fileEntry : baseDir.listFiles()) {
                if (fileEntry.getName().endsWith("par")) {
                    ParFile entry=new ParFile(fileEntry);
                    ((DefaultTableModel)parFileNames.getModel()).addRow(new Object[]{entry.fileName,entry.onderdeel+" "+entry.startgroep,entry.serie});
                    parFiles.put(fileEntry.getName(),entry);
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
