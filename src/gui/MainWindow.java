package gui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import liveresultsclient.entity.Atleten;
import liveresultsclient.entity.AtletiekNu;
import liveresultsclient.entity.Onderdelen;
import liveresultsclient.entity.Sisresult;
import liveresultsclient.entity.StartLijsten;
import liveresultsclient.entity.Wedstrijden;
import utils.HibernateSessionHandler;

/**
 *
 * @author woutermkievit
 */
public class MainWindow extends javax.swing.JFrame {

    /**
     * Queries
     */
    private static String QUERY_Wedstrijden = "from Wedstrijden";
    private static String QUERY_Onderdelen = "from Onderdelen where wedstrijdid = ";
    private static String QUERY_Atleten = "from Atleten where wedstrijdid = ";
    private static String QUERY_StartLijsten = "from StartLijsten where OnderdeelID = ";

    public int wedstrijdId = -1;
    private int OnderdeelId = -1;
    
    public static MainWindow mainObj;
    
    private HibernateSessionHandler sessionHandler;
    
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        sessionHandler=HibernateSessionHandler.get();
        UpdateGui();
        
    }

    public void UpdateGui() {
        displayResultWedstrijden(QUERY_Wedstrijden, WedstrijdTable);
        for (int i = 0; i < Tabs.getTabCount(); i++) {
            if (Tabs.getComponentAt(i) instanceof StartlijstPanel || Tabs.getComponentAt(i) instanceof AtletiekNuPanel) {
                Tabs.remove(i);
            }
        }
        if (wedstrijdId == -1) {
            Tabs.setEnabledAt(1, false);
            Tabs.setEnabledAt(2, false);

        } else {
            Tabs.remove(0);
            displayResultOnderdelen(QUERY_Onderdelen + wedstrijdId, OnderdelenTable);
            displayResultAtleten(QUERY_Atleten + wedstrijdId, AtletenTable);
            Tabs.setEnabledAt(0, true);
            Tabs.setEnabledAt(1, true);
            try{
                int wid=((AtletiekNu)sessionHandler.executeHQLQuery("from AtletiekNu where wedstrijdid = "+wedstrijdId).get(0)).getNuid();
                AtletiekNuPanel.GetAtletiekNuPanel(Tabs,wid);
            }catch(Exception ex){
                Logger.getLogger(AtletiekNuPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    private void displayResultWedstrijden(String query, JTable table) {
        List resultList = sessionHandler.executeHQLQuery(query);
        Vector<String> tableHeaders = new Vector<String>();
        Vector tableData = new Vector();
        tableHeaders.add("Naam");
        tableHeaders.add("Datum");
        tableHeaders.add("Locatie");
        tableHeaders.add("Public");
        tableHeaders.add("");
        for (Object o : resultList) {
            Wedstrijden wedstrijd = (Wedstrijden) o;
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(wedstrijd.getNaam());
            oneRow.add(wedstrijd.getDatum());
            oneRow.add(wedstrijd.getLocatie());
            oneRow.add((Boolean) wedstrijd.getPublic_());
            oneRow.add(wedstrijd.getId());
            tableData.add(oneRow);
        }
        table.setModel(new DefaultTableModel(tableData, tableHeaders));
        Action open = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();
                int modelRow = Integer.valueOf(e.getActionCommand());
                wedstrijdId = (int) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 4);
                UpdateGui();
                Sisresult.startHandler();
                Tabs.setSelectedIndex(1);
            }
        };
        Action niks = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                
            }
        };
        CheckboxColumn checkboxColumn = new CheckboxColumn(table,niks, 3);
        ButtonColumn buttonColumn = new ButtonColumn(table, open, 4, "Open");

    }

    private void displayResultOnderdelen(String query, JTable table) {
        List resultList = sessionHandler.executeHQLQuery(query);
        Vector<String> tableHeaders = new Vector<String>();
        Vector tableData = new Vector();
        tableHeaders.add("Naam");
        tableHeaders.add("Filter");
        tableHeaders.add("Opmerking");
        tableHeaders.add("Racenaam");
        tableHeaders.add("Ready");
        tableHeaders.add("");
        for (Object o : resultList) {
            Onderdelen onderdeel = (Onderdelen) o;
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(onderdeel.getOnderdeel());
            oneRow.add(onderdeel.getFilter());
            oneRow.add(onderdeel.getOpmerking());
            oneRow.add(onderdeel.getRaceName());
            oneRow.add(onderdeel.getReady());
            oneRow.add(onderdeel.getId());
            tableData.add(oneRow);
        }
        table.setModel(new DefaultTableModel(tableData, tableHeaders));
        Action open = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();
                int modelRow = Integer.valueOf(e.getActionCommand());
                OnderdeelId = (int) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 5);
                String name = (String) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 0);
                StartlijstPanel panel = new StartlijstPanel(Tabs);

                displayResultStartLijst(QUERY_StartLijsten + OnderdeelId, panel.getTable());
                Tabs.add(name, panel);
                int i = Tabs.indexOfComponent(panel);
                Tabs.setTabComponentAt(i,
                        new ButtonTabComponent(Tabs));
                Tabs.setSelectedIndex(i);
            }
        };
        CheckboxColumn checkboxColumn = new CheckboxColumn(table, null, 4);
        ButtonColumn buttonColumn = new ButtonColumn(table, open, 5, "Open");

    }

    private void displayResultAtleten(String query, JTable table) {
        List resultList = sessionHandler.executeHQLQuery(query);
        Vector<String> tableHeaders = new Vector<String>();
        Vector tableData = new Vector();
        tableHeaders.add("Naam");
        tableHeaders.add("Startnummer");
        tableHeaders.add("geslacht");
        tableHeaders.add("Geboorte datum");
        tableHeaders.add("vereniging");
        for (Object o : resultList) {
            Atleten atleten = (Atleten) o;
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(atleten.getNaam());
            oneRow.add(atleten.getStartnummer());
            oneRow.add(atleten.getGeslacht());
            oneRow.add(atleten.getGeb());
            oneRow.add(atleten.getVereniging());
            tableData.add(oneRow);
        }
        table.setModel(new DefaultTableModel(tableData, tableHeaders));

    }

    private void displayResultStartLijst(String query, JTable table) {
        List resultList = sessionHandler.executeHQLQuery(query);
        Vector<String> tableHeaders = new Vector<String>();
        Vector tableData = new Vector();
        tableHeaders.add("Serie");
        tableHeaders.add("Baan");
        tableHeaders.add("NR");
        tableHeaders.add("Naam");
        tableHeaders.add("Vereniging");
        tableHeaders.add("Qpresatie");
        tableHeaders.add("Afmelding");
        tableHeaders.add("");
        for (Object o : resultList) {
            StartLijsten startLijsten = (StartLijsten) o;
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(startLijsten.getSerie());
            oneRow.add(startLijsten.getBaan());
            oneRow.add(startLijsten.getAtleten().getStartnummer());
            oneRow.add(startLijsten.getAtleten().getNaam());
            oneRow.add(startLijsten.getAtleten().getVereniging());
            oneRow.add(startLijsten.getQpresatie());
            oneRow.add(!startLijsten.isNextRound());

            tableData.add(oneRow);
        }
        table.setModel(new DefaultTableModel(tableData, tableHeaders));
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Tabs = new javax.swing.JTabbedPane();
        Wedstrijden = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        WedstrijdTable = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        Onderdelen = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        OnderdelenTable = new javax.swing.JTable();
        Atleten = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        AtletenTable = new javax.swing.JTable();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        WedstrijdTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(WedstrijdTable);
        WedstrijdTable.getAccessibleContext().setAccessibleName("");

        jButton1.setText("Save");
        jButton1.setActionCommand("");

        jButton2.setText("Reload");

        javax.swing.GroupLayout WedstrijdenLayout = new javax.swing.GroupLayout(Wedstrijden);
        Wedstrijden.setLayout(WedstrijdenLayout);
        WedstrijdenLayout.setHorizontalGroup(
            WedstrijdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WedstrijdenLayout.createSequentialGroup()
                .addGroup(WedstrijdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(WedstrijdenLayout.createSequentialGroup()
                        .addGap(0, 506, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2))
                    .addGroup(WedstrijdenLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        WedstrijdenLayout.setVerticalGroup(
            WedstrijdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WedstrijdenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(WedstrijdenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)))
        );

        Tabs.addTab("Wedstrijden", Wedstrijden);

        OnderdelenTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(OnderdelenTable);

        javax.swing.GroupLayout OnderdelenLayout = new javax.swing.GroupLayout(Onderdelen);
        Onderdelen.setLayout(OnderdelenLayout);
        OnderdelenLayout.setHorizontalGroup(
            OnderdelenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, OnderdelenLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE)
                .addContainerGap())
        );
        OnderdelenLayout.setVerticalGroup(
            OnderdelenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
        );

        Tabs.addTab("Onderdelen", Onderdelen);

        AtletenTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(AtletenTable);

        javax.swing.GroupLayout AtletenLayout = new javax.swing.GroupLayout(Atleten);
        Atleten.setLayout(AtletenLayout);
        AtletenLayout.setHorizontalGroup(
            AtletenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AtletenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
                .addContainerGap())
        );
        AtletenLayout.setVerticalGroup(
            AtletenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AtletenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                .addContainerGap())
        );

        Tabs.addTab("Atleten", Atleten);

        jInternalFrame1.setVisible(true);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jMenu1.setText("Wedstrijden");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("New");
        jMenuItem1.setToolTipText("");
        jMenuItem1.setActionCommand("");
        jMenu1.add(jMenuItem1);
        jMenuItem1.getAccessibleContext().setAccessibleName("");

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Import/Export");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabs)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabs, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        Tabs.getAccessibleContext().setAccessibleName("tabs");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        /*try {
         for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
         if ("Nimbus".equals(info.getName())) {
         javax.swing.UIManager.setLookAndFeel(info.getClassName());
         break;
         }
         }
         } catch (ClassNotFoundException ex) {
         java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
         } catch (InstantiationException ex) {
         java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
         } catch (IllegalAccessException ex) {
         java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
         } catch (javax.swing.UnsupportedLookAndFeelException ex) {
         java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
         }
         //</editor-fold>

         /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                mainObj=new MainWindow();
                mainObj.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Atleten;
    private javax.swing.JTable AtletenTable;
    private javax.swing.JPanel Onderdelen;
    private javax.swing.JTable OnderdelenTable;
    private javax.swing.JTabbedPane Tabs;
    private javax.swing.JTable WedstrijdTable;
    private javax.swing.JPanel Wedstrijden;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
}
