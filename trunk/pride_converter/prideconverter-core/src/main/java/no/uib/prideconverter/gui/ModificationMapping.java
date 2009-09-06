package no.uib.prideconverter.gui;

import no.uib.prideconverter.PRIDEConverter;
import be.proteomics.mascotdatfile.util.interfaces.Modification;
import java.awt.Window;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.xml.rpc.ServiceException;
import no.uib.olsdialog.OLSDialog;
import no.uib.olsdialog.OLSInputable;
import no.uib.prideconverter.util.Util;
import uk.ac.ebi.ook.web.services.Query;
import uk.ac.ebi.ook.web.services.QueryService;
import uk.ac.ebi.ook.web.services.QueryServiceLocator;
import uk.ac.ebi.pride.model.implementation.mzData.CvParamImpl;

/**
 * A dialog that lets the user map detected modifications to PSI-MOD CV terms.
 *
 * @author  Harald Barsnes
 * 
 * Created July 2008
 */
public class ModificationMapping extends javax.swing.JDialog implements OLSInputable {

    private ProgressDialog progressDialog;
    private OutputDetails outputDetails;
    private CvParamImpl selectedCvTerm;
    private Modification modification;
    private boolean fixedModification = false;

    /**
     * Creates a new ModificationMapping dialog.
     * 
     * @param parent
     * @param modal
     * @param progressDialog
     * @param modification
     * @param cvParam
     */
    public ModificationMapping(java.awt.Frame parent, boolean modal, ProgressDialog progressDialog,
            Modification modification, CvParamImpl cvParam, boolean fixedModification) {
        super(parent, modal);
        initComponents();

        outputDetails = (OutputDetails) parent;
        this.progressDialog = progressDialog;
        this.selectedCvTerm = cvParam;
        this.fixedModification = fixedModification;

        if (selectedCvTerm != null) {
            psiModJTextField.setText(selectedCvTerm.getName() + " [" +
                    selectedCvTerm.getAccession() + "]");
            psiModJTextField.setCaretPosition(0);
            okJButton.setEnabled(true);
        }

        this.modification = modification;

        nameJTextField.setText(modification.getType());
        massJTextField.setText("" + modification.getMass());
        locationJTextField.setText(modification.getLocation());

        // only works for Java 1.6 and newer
//        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().
//                getResource("/no/uib/prideconverter/icons/prideConverter_16.GIF")));

        psiModJButton.requestFocus();

        setLocationRelativeTo(outputDetails);
        setVisible(true);
    }

    /**
     * Creates a new ModificationMapping dialog.
     * 
     * @param parent
     * @param modal
     * @param progressDialog
     * @param modName
     * @param position
     * @param modificationMass
     * @param cvParam
     */
    public ModificationMapping(java.awt.Frame parent, boolean modal, ProgressDialog progressDialog,
            String modName, String position, Double modificationMass, CvParamImpl cvParam, boolean fixedModification) {
        super(parent, modal);
        initComponents();

        outputDetails = (OutputDetails) parent;
        this.progressDialog = progressDialog;
        this.selectedCvTerm = cvParam;
        this.fixedModification = fixedModification;

        if (selectedCvTerm != null) {
            psiModJTextField.setText(selectedCvTerm.getName() + " [" +
                    selectedCvTerm.getAccession() + "]");
            psiModJTextField.setCaretPosition(0);
            okJButton.setEnabled(true);
        }

        this.modification = null;

        nameJTextField.setText(modName);
        if (modificationMass != null) {
            massJTextField.setText("" + modificationMass.doubleValue());
        } else {
            massJTextField.setText("-");
        }
        locationJTextField.setText(position);

        // only works for Java 1.6 and newer
//        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().
//                getResource("/no/uib/prideconverter/icons/prideConverter_16.GIF")));

        psiModJButton.requestFocus();

        setLocationRelativeTo(outputDetails);
        setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelJButton = new javax.swing.JButton();
        okJButton = new javax.swing.JButton();
        aboutJButton = new javax.swing.JButton();
        helpJButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        nameJTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        massJTextField = new javax.swing.JTextField();
        locationJTextField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        psiModJTextField = new javax.swing.JTextField();
        psiModJButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Modification Detected");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        cancelJButton.setText("Cancel");
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });

        okJButton.setText("OK");
        okJButton.setEnabled(false);
        okJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okJButtonActionPerformed(evt);
            }
        });

        aboutJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/prideConverter_16.GIF"))); // NOI18N
        aboutJButton.setToolTipText("About");
        aboutJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutJButtonActionPerformed(evt);
            }
        });

        helpJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/help.GIF"))); // NOI18N
        helpJButton.setToolTipText("Help");
        helpJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpJButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Modification Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N

        jLabel3.setText("Name:");

        nameJTextField.setEditable(false);
        nameJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel4.setText("Mass:");

        jLabel5.setText("Location:");

        massJTextField.setEditable(false);
        massJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        locationJTextField.setEditable(false);
        locationJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 28, Short.MAX_VALUE)
                        .add(nameJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 379, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 31, Short.MAX_VALUE)
                        .add(massJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 379, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 15, Short.MAX_VALUE)
                        .add(locationJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 379, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(nameJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(massJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(locationJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "PSI-MOD Mapping", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N

        jLabel2.setText("PSI-MOD:");

        psiModJTextField.setEditable(false);
        psiModJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        psiModJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/ols_transparent.GIF"))); // NOI18N
        psiModJButton.setToolTipText("Ontology Lookup Service");
        psiModJButton.setPreferredSize(new java.awt.Dimension(61, 23));
        psiModJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                psiModJButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .add(18, 18, 18)
                .add(psiModJTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(psiModJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(2, 2, 2)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(psiModJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(psiModJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel1.setText("Please select the corresponding PSI-MOD modification, and click 'OK' to add the modification.");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(helpJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(aboutJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 259, Short.MAX_VALUE)
                        .add(okJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(cancelJButton)
                        .add(okJButton))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(helpJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(aboutJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Opens an About PRIDE Converter dialog.
     * 
     * @param evt
     */
    private void aboutJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutJButtonActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpDialog(this, true, getClass().getResource("/no/uib/prideconverter/helpfiles/AboutPRIDE_Converter.html"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_aboutJButtonActionPerformed

    /**
     * Opens a Help dialog.
     * 
     * @param evt
     */
    private void helpJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpJButtonActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpDialog(this, true, getClass().getResource("/no/uib/prideconverter/helpfiles/ModificationMapping.html"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_helpJButtonActionPerformed

    /**
     * Opens an OLS dialog where the CV term to map the modification to can 
     * be selected.
     * 
     * @param evt
     */
    private void psiModJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_psiModJButtonActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        String searchTerm = "";

        if (psiModJTextField.getText().length() > 0) {
            searchTerm = psiModJTextField.getText().substring(0, psiModJTextField.getText().indexOf("[") - 1);
            searchTerm = searchTerm.replaceAll("-", " ");
            searchTerm = searchTerm.replaceAll(":", " ");
            searchTerm = searchTerm.replaceAll("\\(", " ");
            searchTerm = searchTerm.replaceAll("\\)", " ");
            searchTerm = searchTerm.replaceAll("&", " ");
            searchTerm = searchTerm.replaceAll("\\+", " ");
            searchTerm = searchTerm.replaceAll("\\[", " ");
            searchTerm = searchTerm.replaceAll("\\]", " ");
        } else {
            searchTerm = nameJTextField.getText();
        }

        new OLSDialog(this, this, true, "modificationSelection",
                "Protein Modifications (PSI-MOD) [MOD]",
                searchTerm);
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_psiModJButtonActionPerformed

    /**
     * Closes the dialog and also tells the PRIDEConverter that the conversion 
     * has been stopped.
     * 
     * @param evt
     */
    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed
        PRIDEConverter.setCancelConversion(true);
        progressDialog.setVisible(false);
        progressDialog.dispose();
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_cancelJButtonActionPerformed

    /**
     * Forwards the selected mapping the to PRIDEConverter and closes the 
     * dialog. In some cases (if the needed infomation is available) a check 
     * is also performed to see if the mass of the selected modification is 
     * not to different (more than 1 Da) compared to the information extracted 
     * from the data file. 
     * 
     * @param evt
     */
    private void okJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okJButtonActionPerformed

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        boolean error = false;

        Double modificationMass = null;

        // check if the selected PSI-MOD has a mass close to the 
        // mass of the modification in the data file
        try {
            QueryService locator = new QueryServiceLocator();
            Query qs = locator.getOntologyQuery();

            String accession = selectedCvTerm.getAccession();
            String ontologyShort = selectedCvTerm.getCVLookup();

            // change PSI-MOD to MOD for PSI-MOD modifications
            if (accession.startsWith("PSI-MOD:")) {
                accession = accession.substring(4);
            }

            if (ontologyShort.equalsIgnoreCase("PSI-MOD")) {
                ontologyShort = "MOD";
            }

            try {
                Map map = qs.getTermXrefs(accession, ontologyShort);
                //Map map = qs.getTermMetadata(accession, ontologyShort);

                Iterator iterator = map.keySet().iterator();

                while (iterator.hasNext()) {
                    Object key = iterator.next();
                    String temp = "" + map.get(key);

                    if (temp.lastIndexOf("DiffMono\"") != -1) {
                        modificationMass = new Double(temp.substring(temp.indexOf("\"") + 1, temp.length() - 1));
                    }
                }

//            if (map.get("DiffMono") != null) {
//                modificationMass = new Double("" + map.get("DiffMono"));
//            }
            } catch (IndexOutOfBoundsException e) {
                Util.writeToErrorLog("An error occured while trying to get the mono mass for modification \'" + accession + "\'");
            }

            if (!massJTextField.getText().equalsIgnoreCase("-")) {

                if (PRIDEConverter.getProperties().getDataSource().equalsIgnoreCase("SEQUEST Result File") &&
                        fixedModification) {

                    // sequest fixed modifications has to be handled separatly as they  
                    // include the total mass and not the modification mass
//                    if (modificationMass != null) {
//
//                        if (Math.abs(modificationMass) -
//                                Math.abs(new Double(massJTextField.getText()).doubleValue()) > 1) {
//                            this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
//                            int option = JOptionPane.showConfirmDialog(
//                                    this, "The distance between the mass of the PSI-MOD and the mass of the detected modification is " +
//                                    roundDouble(
//                                    Math.abs(modificationMass) -
//                                    Math.abs(new Double(massJTextField.getText()).doubleValue()), 4) +
//                                    " Da.\nAre you sure you have selected the correct PSI-MOD modification?",
//                                    "Verify Modification", JOptionPane.YES_NO_CANCEL_OPTION);
//
//                            if (option != 0) {
//                                error = true;
//                            }
//                        }
//                    }
                } else {

                    if (modificationMass != null) {

                        if (diffGreaterThan(modificationMass, new Double(massJTextField.getText()).doubleValue(), 1.0)) {
                            this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                            int option = JOptionPane.showConfirmDialog(
                                    this, "The distance between the mass of the PSI-MOD and the mass of " +
                                    "the detected modification is " +
                                    roundDouble(getDistance(modificationMass, new Double(massJTextField.getText()).doubleValue()), 4) +
                                    " Da.\nAre you sure you have selected the correct PSI-MOD modification?",
                                    "Verify Modification", JOptionPane.YES_NO_CANCEL_OPTION);

                            if (option != 0) {
                                error = true;
                            }
                        }
                    }
                }
            }
        } catch (RemoteException ex) {
            this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(
                    this, "Unable to contact the OLS. Make sure that you are online.",
                    "OLS not available", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("Error when trying to access OLS: ");
            ex.printStackTrace();
            error = true;
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(
                    this, "Unable to contact the OLS. Make sure that you are online.",
                    "OLS not available", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("Error when trying to access OLS: ");
            ex.printStackTrace();
            error = true;
        }

        if (!error) {
            this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
            PRIDEConverter.addModification(nameJTextField.getText(), selectedCvTerm, modificationMass);
            this.setVisible(false);
            this.dispose();
        }
    }//GEN-LAST:event_okJButtonActionPerformed

    /**
     * Returns true if the distance between the two numbers are greater
     * than the given distance.
     *
     * @param numberA
     * @param numberB
     * @param maxDiff
     * @return true if the distance between the two numbers are greater
     *         than the given distance
     */
    private boolean diffGreaterThan(double numberA, double numberB, double maxDiff){

        double diff;

        if(numberA > 0 && numberB > 0){
            diff = Math.abs(numberA - numberB);
        } else if(numberA < 0 && numberB < 0){
            diff = Math.abs(Math.abs(numberA) - Math.abs(numberB));
        } else{
            diff = Math.abs(numberA) + Math.abs(numberB);
        }
        
        return diff > maxDiff;
    }

    /**
     * Returns the distance between the two numbers.
     *
     * @param numberA
     * @param numberB
     * @return the distance between the two numbers
     */
    private double getDistance(double numberA, double numberB){

        double diff;

        if(numberA > 0 && numberB > 0){
            diff = Math.abs(numberA - numberB);
        } else if(numberA < 0 && numberB < 0){
            diff = Math.abs(Math.abs(numberA) - Math.abs(numberB));
        } else{
            diff = Math.abs(numberA) + Math.abs(numberB);
        }

        return diff;
    }



    /**
     * See cancelJButtonActionPerformed
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cancelJButtonActionPerformed(null);
    }//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutJButton;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JButton helpJButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField locationJTextField;
    private javax.swing.JTextField massJTextField;
    private javax.swing.JTextField nameJTextField;
    private javax.swing.JButton okJButton;
    private javax.swing.JButton psiModJButton;
    private javax.swing.JTextField psiModJTextField;
    // End of variables declaration//GEN-END:variables

    /**
     * See OLSInputable
     */
    public void insertOLSResult(String field, String selectedValue, String accession,
            String ontologyShort, String ontologyLong, int modifiedRow, String mappedTerm) {


        // the code below changes the (short) name of the PSI-MOD ontology 
        // from MOD to PSI, but this resulted in a problem when searching 
        // against OLS later.
//        // change MOD to PSI-MOD for PSI-MOD modifications
//        if (accession.startsWith("MOD:")) {
//            accession = "PSI-" + accession;
//        }
//
//        if (ontologyShort.equalsIgnoreCase("MOD")) {
//            ontologyShort = "PSI-MOD";
//        }

        psiModJTextField.setText(selectedValue + " [" + accession + "]");
        psiModJTextField.setCaretPosition(0);

        selectedCvTerm = new CvParamImpl(accession, ontologyShort, selectedValue, 0, null);
        okJButton.setEnabled(true);
    }

    /**
     * See OLSInputable
     */
    public Window getWindow() {
        return (Window) this;
    }

    /**
     * Rounds of a double value to the wanted number of decimalplaces
     *
     * @param d the double to round of
     * @param places number of decimalplaces wanted
     * @return double - the new double
     */
    public static double roundDouble(double d, int places) {
        return Math.round(d * Math.pow(10, (double) places)) /
                Math.pow(10, (double) places);
    }
}
